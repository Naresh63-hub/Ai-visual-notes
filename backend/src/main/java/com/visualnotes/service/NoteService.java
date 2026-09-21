package com.visualnotes.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visualnotes.ai.AiService;
import com.visualnotes.dto.*;
import com.visualnotes.entity.NoteDocument;
import com.visualnotes.entity.NotePage;
import com.visualnotes.entity.User;
import com.visualnotes.repository.NoteDocumentRepository;
import com.visualnotes.repository.NotePageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteService.class);
    private static final int MAX_TITLE_LENGTH = 250;
    private static final int MAX_TOPIC_TITLE_LENGTH = 180;
    private static final int MAX_ALLOWED_PAGES = 10;

    private final NoteDocumentRepository documentRepository;
    private final NotePageRepository pageRepository;
    private final AiService aiService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public NoteService(NoteDocumentRepository documentRepository, NotePageRepository pageRepository,
                       AiService aiService, AuthService authService, ObjectMapper objectMapper) {
        this.documentRepository = documentRepository;
        this.pageRepository = pageRepository;
        this.aiService = aiService;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    public PromptAnalysisResponse analyzePrompt(AnalyzePromptRequest request) {
        Integer clampedPages = request.getRequestedPageCount() != null
                ? Math.max(1, Math.min(MAX_ALLOWED_PAGES, request.getRequestedPageCount()))
                : null;
        return aiService.analyzePrompt(request.getPrompt(), request.getPreferredStyle(), clampedPages);
    }

    public PagePlanDto planPages(String prompt, int pageCount, String audience, String style, String difficulty) {
        int clampedPageCount = Math.max(1, Math.min(MAX_ALLOWED_PAGES, pageCount));
        return aiService.planPages(prompt, clampedPageCount, audience, style, difficulty);
    }

    public PagePlanDto planPages(PlanPagesRequest request) {
        int pageCount = request.getPageCount() != null ? request.getPageCount() : 1;
        return planPages(request.getPrompt(), pageCount, request.getAudience(), request.getStyle(), request.getDifficulty());
    }

    @Transactional
    public NoteDocumentDto generateNotes(GenerateNotesRequest request) {
        User currentUser = authService.getCurrentUser();

        Integer reqPageCount = request.getPageCount() != null
                ? Math.max(1, Math.min(MAX_ALLOWED_PAGES, request.getPageCount()))
                : null;

        PromptAnalysisResponse analysis = aiService.analyzePrompt(request.getPrompt(), request.getStyle(), reqPageCount);
        
        int targetPageCount = (reqPageCount != null && reqPageCount > 0)
                ? reqPageCount
                : (analysis.getRequestedPageCount() != null ? analysis.getRequestedPageCount() : analysis.getSuggestedPageCount());

        targetPageCount = Math.max(1, Math.min(MAX_ALLOWED_PAGES, targetPageCount));

        String targetStyle = (request.getStyle() != null && !request.getStyle().isBlank())
                ? request.getStyle()
                : analysis.getDetectedStyle();

        String targetAudience = (request.getAudience() != null && !request.getAudience().isBlank())
                ? request.getAudience()
                : analysis.getAudience();

        String targetDifficulty = (request.getDifficulty() != null && !request.getDifficulty().isBlank())
                ? request.getDifficulty()
                : analysis.getDifficulty();

        PagePlanDto plan = request.getCustomPlan();
        if (plan == null || plan.getPages() == null || plan.getPages().isEmpty()) {
            plan = aiService.planPages(request.getPrompt(), targetPageCount, targetAudience, targetStyle, targetDifficulty);
        }

        String safeTitle = truncate(plan.getDocumentTitle(), MAX_TITLE_LENGTH);
        String safePrompt = truncate(request.getPrompt(), 2000);

        NoteDocument document = NoteDocument.builder()
                .user(currentUser)
                .title(safeTitle)
                .originalPrompt(safePrompt)
                .pageCount(plan.getPages().size())
                .style(targetStyle)
                .audience(targetAudience)
                .difficulty(targetDifficulty)
                .status("IN_PROGRESS")
                .pages(new ArrayList<>())
                .build();

        document = documentRepository.save(document);

        int totalPages = plan.getPages().size();
        for (int i = 0; i < totalPages; i++) {
            PagePlanItemDto item = plan.getPages().get(i);
            int pageNum = i + 1;
            String pageTopic = truncate(item.getPageTitle(), MAX_TOPIC_TITLE_LENGTH);

            try {
                PageContentDto contentDto = aiService.generatePage(
                        pageTopic,
                        safePrompt,
                        pageNum,
                        totalPages,
                        targetStyle,
                        targetAudience,
                        targetDifficulty,
                        item.getFocusArea(),
                        item.getPlannedDiagramType()
                );

                String jsonContent = objectMapper.writeValueAsString(contentDto);

                NotePage page = NotePage.builder()
                        .document(document)
                        .pageNumber(pageNum)
                        .topicTitle(pageTopic)
                        .contentJson(jsonContent)
                        .layoutType(contentDto.getLayoutHint() != null ? contentDto.getLayoutHint() : "standard")
                        .diagramType(contentDto.getDiagram() != null ? contentDto.getDiagram().getType() : "concept-map")
                        .build();

                page = pageRepository.save(page);
                document.getPages().add(page);

            } catch (Exception e) {
                log.error("Error generating page {} for doc {}: {}", pageNum, document.getId(), e.getMessage(), e);
                PageContentDto fallbackContent = createFallbackPage(pageTopic, pageNum, totalPages, targetStyle, targetAudience, targetDifficulty);
                try {
                    String fallbackJson = objectMapper.writeValueAsString(fallbackContent);
                    NotePage fallbackPage = NotePage.builder()
                            .document(document)
                            .pageNumber(pageNum)
                            .topicTitle(pageTopic)
                            .contentJson(fallbackJson)
                            .layoutType("standard")
                            .diagramType("concept-map")
                            .build();
                    fallbackPage = pageRepository.save(fallbackPage);
                    document.getPages().add(fallbackPage);
                } catch (JsonProcessingException jpe) {
                    log.error("Critical: Failed to serialize fallback page: {}", jpe.getMessage());
                }
            }
        }

        document.setStatus("COMPLETED");
        document = documentRepository.save(document);

        return mapToDto(document);
    }

    @Transactional
    public NotePageDto regenerateSinglePage(Long documentId, RegeneratePageRequest request) {
        NoteDocument document = documentRepository.findByIdWithPages(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + documentId));

        validateDocumentAccess(document);

        NotePage targetPage = document.getPages().stream()
                .filter(p -> p.getPageNumber().equals(request.getPageNumber()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Page " + request.getPageNumber() + " not found in document"));

        PageContentDto currentContent = parsePageContent(targetPage.getContentJson());

        String styleToUse = request.getStyle() != null ? request.getStyle() : document.getStyle();

        PageContentDto regeneratedContent = aiService.regeneratePage(
                currentContent,
                request.getInstruction(),
                request.getCustomModifier(),
                styleToUse
        );

        try {
            targetPage.setContentJson(objectMapper.writeValueAsString(regeneratedContent));
            if (regeneratedContent.getDiagram() != null) {
                targetPage.setDiagramType(regeneratedContent.getDiagram().getType());
            }
            targetPage = pageRepository.save(targetPage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize regenerated page content", e);
        }

        return mapPageToDto(targetPage);
    }

    @Transactional
    public NotePageDto updatePageContent(Long documentId, UpdatePageRequest request) {
        NoteDocument document = documentRepository.findByIdWithPages(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + documentId));

        validateDocumentAccess(document);

        NotePage targetPage = document.getPages().stream()
                .filter(p -> p.getPageNumber().equals(request.getPageNumber()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Page " + request.getPageNumber() + " not found in document"));

        if (request.getContent() == null) {
            throw new IllegalArgumentException("Page content cannot be null");
        }

        if (request.getTopicTitle() != null && !request.getTopicTitle().isBlank()) {
            String safeTitle = truncate(request.getTopicTitle(), MAX_TOPIC_TITLE_LENGTH);
            targetPage.setTopicTitle(safeTitle);
            request.getContent().setTopicTitle(safeTitle);
        }

        try {
            targetPage.setContentJson(objectMapper.writeValueAsString(request.getContent()));
            if (request.getContent().getDiagram() != null) {
                targetPage.setDiagramType(request.getContent().getDiagram().getType());
            }
            targetPage = pageRepository.save(targetPage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize updated page content", e);
        }

        return mapPageToDto(targetPage);
    }

    @Transactional(readOnly = true)
    public NoteDocumentDto getDocumentById(Long id) {
        NoteDocument document = documentRepository.findByIdWithPages(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));
        validateDocumentReadAccess(document);
        return mapToDto(document);
    }

    @Transactional(readOnly = true)
    public List<NoteDocumentDto> getRecentDocuments(String searchQuery) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }

        List<NoteDocument> docs;
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String safeSearch = escapeSqlWildcards(searchQuery.trim());
            docs = documentRepository.searchByUserIdAndQuery(currentUser.getId(), safeSearch);
        } else {
            docs = documentRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        }

        return docs.stream().map(this::mapToSummaryDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteDocument(Long id) {
        NoteDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));
        validateDocumentAccess(document);
        documentRepository.delete(document);
    }

    @Transactional
    public NoteDocumentDto renameDocument(Long id, String newTitle) {
        NoteDocument document = documentRepository.findByIdWithPages(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));
        validateDocumentAccess(document);
        document.setTitle(truncate(newTitle.trim(), MAX_TITLE_LENGTH));
        document = documentRepository.save(document);
        return mapToDto(document);
    }

    public void validateDocumentReadAccess(NoteDocument document) {
        if (document.getUser() != null) {
            User currentUser = authService.getCurrentUser();
            if (currentUser == null || !document.getUser().getId().equals(currentUser.getId())) {
                throw new SecurityException("Unauthorized access to document");
            }
        }
    }

    public void validateDocumentAccess(NoteDocument document) {
        User currentUser = authService.getCurrentUser();
        if (document.getUser() != null) {
            if (currentUser == null || !document.getUser().getId().equals(currentUser.getId())) {
                throw new SecurityException("Unauthorized access to document");
            }
        } else {
            // Anonymous documents cannot be modified/deleted anonymously by arbitrary users
            if (currentUser == null) {
                throw new SecurityException("Authentication required to modify document");
            }
        }
    }

    public NoteDocumentDto mapToDto(NoteDocument doc) {
        List<NotePageDto> pageDtos = doc.getPages().stream()
                .map(this::mapPageToDto)
                .collect(Collectors.toList());

        return NoteDocumentDto.builder()
                .id(doc.getId())
                .userId(doc.getUser() != null ? doc.getUser().getId() : null)
                .title(doc.getTitle())
                .originalPrompt(doc.getOriginalPrompt())
                .pageCount(doc.getPageCount())
                .style(doc.getStyle())
                .audience(doc.getAudience())
                .difficulty(doc.getDifficulty())
                .status(doc.getStatus())
                .pages(pageDtos)
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    public NoteDocumentDto mapToSummaryDto(NoteDocument doc) {
        return NoteDocumentDto.builder()
                .id(doc.getId())
                .userId(doc.getUser() != null ? doc.getUser().getId() : null)
                .title(doc.getTitle())
                .originalPrompt(doc.getOriginalPrompt())
                .pageCount(doc.getPageCount())
                .style(doc.getStyle())
                .audience(doc.getAudience())
                .difficulty(doc.getDifficulty())
                .status(doc.getStatus())
                .pages(new ArrayList<>())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    public NotePageDto mapPageToDto(NotePage page) {
        return NotePageDto.builder()
                .id(page.getId())
                .pageNumber(page.getPageNumber())
                .topicTitle(page.getTopicTitle())
                .content(parsePageContent(page.getContentJson()))
                .layoutType(page.getLayoutType())
                .diagramType(page.getDiagramType())
                .createdAt(page.getCreatedAt())
                .updatedAt(page.getUpdatedAt())
                .build();
    }

    private PageContentDto parsePageContent(String json) {
        try {
            return objectMapper.readValue(json, PageContentDto.class);
        } catch (Exception e) {
            log.error("Failed to parse page JSON content: {}", e.getMessage());
            return PageContentDto.builder()
                    .documentTitle("Study Page")
                    .pageNumber(1)
                    .totalPages(1)
                    .topicTitle("Study Notes")
                    .definition("Content temporarily unavailable")
                    .build();
        }
    }

    private PageContentDto createFallbackPage(String topic, int pageNum, int totalPages, String style, String audience, String difficulty) {
        return aiService.generatePage(topic, topic, pageNum, totalPages, style, audience, difficulty, "Study Breakdown", "concept-map");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) : text;
    }

    private String escapeSqlWildcards(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
