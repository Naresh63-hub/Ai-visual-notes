package com.visualnotes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visualnotes.ai.AiService;
import com.visualnotes.ai.GeminiProvider;
import com.visualnotes.ai.OpenAiProvider;
import com.visualnotes.ai.SemanticEngineAiProvider;
import com.visualnotes.ai.domain.*;
import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.GenerateNotesRequest;
import com.visualnotes.dto.NoteDocumentDto;
import com.visualnotes.dto.NotePageDto;
import com.visualnotes.dto.PageContentDto;
import com.visualnotes.repository.NoteDocumentRepository;
import com.visualnotes.repository.NotePageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class EducationalContentVerificationTest {

    private NoteService noteService;

    @BeforeEach
    void setUp() {
        DiagramEngine diagramEngine = new DiagramEngine();
        TopicUnderstandingEngine topicEngine = new TopicUnderstandingEngine();
        ContentPlanningEngine planningEngine = new ContentPlanningEngine();
        PagePlanningEngine pagePlanningEngine = new PagePlanningEngine();
        ContentQualityValidator validator = new ContentQualityValidator();
        ContentSynthesisEngine synthesisEngine = new ContentSynthesisEngine(diagramEngine);

        SemanticEngineAiProvider offlineProvider = new SemanticEngineAiProvider(
                topicEngine, planningEngine, synthesisEngine, validator, pagePlanningEngine
        );

        OpenAiProvider mockOpenAi = Mockito.mock(OpenAiProvider.class);
        GeminiProvider mockGemini = Mockito.mock(GeminiProvider.class);
        when(mockOpenAi.isConfigured()).thenReturn(false);
        when(mockGemini.isConfigured()).thenReturn(false);

        AiService aiService = new AiService(mockOpenAi, mockGemini, offlineProvider);
        ReflectionTestUtils.setField(aiService, "configuredProvider", "offline");

        NoteDocumentRepository docRepo = Mockito.mock(NoteDocumentRepository.class);
        NotePageRepository pageRepo = Mockito.mock(NotePageRepository.class);
        AuthService authService = Mockito.mock(AuthService.class);
        ObjectMapper objectMapper = new ObjectMapper();

        when(docRepo.save(any())).thenAnswer(invocation -> {
            com.visualnotes.entity.NoteDocument doc = invocation.getArgument(0);
            if (doc.getId() == null) doc.setId(101L);
            return doc;
        });

        when(pageRepo.save(any())).thenAnswer(invocation -> {
            com.visualnotes.entity.NotePage page = invocation.getArgument(0);
            if (page.getId() == null) page.setId(201L);
            return page;
        });

        noteService = new NoteService(docRepo, pageRepo, aiService, authService, pagePlanningEngine, objectMapper);
    }

    private void verifyNoFiller(NoteDocumentDto doc) {
        for (NotePageDto page : doc.getPages()) {
            PageContentDto c = page.getContent();
            assertNotNull(c);
            String fullText = (c.getTopicTitle() + " " + c.getDefinition() + " " + c.getSimpleExplanation() + " " + c.getPurpose()).toLowerCase();
            assertFalse(fullText.contains("governs state progression"), "Must not contain generic boilerplate filler");
            assertFalse(fullText.contains("streamlined method to solve specific computational"), "Must not contain generic boilerplate filler");
            assertFalse(fullText.contains("concept architecture & core mechanics"), "Must not contain generic template header");

            if (c.getSections() != null) {
                Set<String> sectionHeadings = new HashSet<>();
                for (var sec : c.getSections()) {
                    String secText = (sec.getTitle() + " " + sec.getContent()).toLowerCase();
                    assertFalse(secText.contains("governs state progression"));
                    assertFalse(secText.contains("streamlined method to solve specific computational"));
                    // Check no exact duplicate section titles on the same page
                    String heading = sec.getTitle() != null ? sec.getTitle().toLowerCase().trim() : "";
                    assertFalse(sectionHeadings.contains(heading), "Duplicate section heading detected: " + heading);
                    sectionHeadings.add(heading);
                }
            }
        }
    }

    @Test
    void test1_OsiModelSevenLayers() {
        GenerateNotesRequest req = new GenerateNotesRequest();
        req.setPrompt("Explain the OSI model and its seven layers");
        req.setPageCount(2);
        req.setStyle("Handwritten");
        req.setAudience("B.Tech / College");

        NoteDocumentDto doc = noteService.generateNotes(req);

        assertEquals(2, doc.getPageCount(), "Must generate exactly 2 pages");
        assertEquals(2, doc.getPages().size(), "Rendered page count must match 2");
        assertTrue(doc.getTitle().toLowerCase().contains("osi"), "Title must relate to OSI");

        // Page 1 should contain OSI architecture / layers
        PageContentDto p1 = doc.getPages().get(0).getContent();
        assertTrue(p1.getTopicTitle().toLowerCase().contains("osi") || (p1.getTopicSubtitle() != null && p1.getTopicSubtitle().toLowerCase().contains("layer")));
        assertNotNull(p1.getDiagram(), "OSI model should include visual diagram");

        // Page 2 should cover upper/lower layer mechanics
        PageContentDto p2 = doc.getPages().get(1).getContent();
        assertNotNull(p2.getSections(), "Page 2 must have sections");
        assertFalse(p2.getSections().isEmpty(), "Page 2 sections must not be empty");

        verifyNoFiller(doc);
    }

    @Test
    void test2_DbmsNormalizationWithExamples() {
        GenerateNotesRequest req = new GenerateNotesRequest();
        req.setPrompt("Explain DBMS normalization with examples");
        req.setPageCount(2);
        req.setStyle("Handwritten");
        req.setAudience("B.Tech / College");

        NoteDocumentDto doc = noteService.generateNotes(req);

        assertEquals(2, doc.getPageCount());
        assertEquals(2, doc.getPages().size());
        assertTrue(doc.getTitle().toLowerCase().contains("normal") || doc.getTitle().toLowerCase().contains("dbms"));

        // Normalization should contain comparison or normal forms table/details
        boolean hasNormalForms = doc.getPages().stream().anyMatch(p -> {
            PageContentDto c = p.getContent();
            StringBuilder sb = new StringBuilder(c.getTopicTitle() + " " + c.getDefinition() + " ");
            if (c.getSections() != null) {
                for (var s : c.getSections()) {
                    sb.append(s.getTitle()).append(" ").append(s.getContent()).append(" ");
                    if (s.getBulletPoints() != null) {
                        sb.append(String.join(" ", s.getBulletPoints())).append(" ");
                    }
                }
            }
            String text = sb.toString().toLowerCase();
            return text.contains("1nf") || text.contains("2nf") || text.contains("3nf") || text.contains("bcnf") || text.contains("normal form");
        });
        assertTrue(hasNormalForms, "Must explicitly teach normal forms (1NF, 2NF, 3NF)");

        verifyNoFiller(doc);
    }

    @Test
    void test3_BinarySearchWithWorkedExample() {
        GenerateNotesRequest req = new GenerateNotesRequest();
        req.setPrompt("Explain binary search with a worked example");
        req.setPageCount(1);
        req.setStyle("Handwritten");
        req.setAudience("B.Tech / College");

        NoteDocumentDto doc = noteService.generateNotes(req);

        assertEquals(1, doc.getPageCount());
        assertEquals(1, doc.getPages().size());
        PageContentDto p1 = doc.getPages().get(0).getContent();

        assertTrue(p1.getTopicTitle().toLowerCase().contains("binary search"));
        assertNotNull(p1.getAlgorithm(), "Binary search must include algorithmic steps");
        assertNotNull(p1.getDiagram(), "Binary search should include an array diagram");

        verifyNoFiller(doc);
    }

    @Test
    void test4_PhotosynthesisAndStages() {
        GenerateNotesRequest req = new GenerateNotesRequest();
        req.setPrompt("Explain photosynthesis and its stages");
        req.setPageCount(2);
        req.setStyle("Handwritten");
        req.setAudience("B.Tech / College");

        NoteDocumentDto doc = noteService.generateNotes(req);

        assertEquals(2, doc.getPageCount());
        assertEquals(2, doc.getPages().size());

        // Biology domain check: Must NOT contain CS algorithm steps or pseudocode
        for (NotePageDto page : doc.getPages()) {
            assertNull(page.getContent().getPseudocode(), "Biology note must not contain CS pseudocode");
        }

        // Must contain photosynthesis equation or chemical pathway
        boolean hasChemicalEquation = doc.getPages().stream().anyMatch(p -> p.getContent().getFormula() != null);
        assertTrue(hasChemicalEquation, "Photosynthesis must include overall chemical reaction formula");

        verifyNoFiller(doc);
    }

    @Test
    void test5_GradientDescentWithNumericalExample() {
        GenerateNotesRequest req = new GenerateNotesRequest();
        req.setPrompt("Explain gradient descent with a numerical example");
        req.setPageCount(2);
        req.setStyle("Handwritten");
        req.setAudience("B.Tech / College");

        NoteDocumentDto doc = noteService.generateNotes(req);

        assertEquals(2, doc.getPageCount());
        assertEquals(2, doc.getPages().size());

        // Must contain mathematical update formula
        boolean hasMathFormula = doc.getPages().stream().anyMatch(p -> p.getContent().getFormula() != null);
        assertTrue(hasMathFormula, "Gradient descent must include mathematical update formula");

        verifyNoFiller(doc);
    }

    @Test
    void test6_Pagination_FivePagesDistribution() {
        GenerateNotesRequest req = new GenerateNotesRequest();
        req.setPrompt("Explain the OSI model and its seven layers");
        req.setPageCount(5);
        req.setStyle("Handwritten");
        req.setAudience("B.Tech / College");

        NoteDocumentDto doc = noteService.generateNotes(req);

        assertEquals(5, doc.getPageCount(), "Requested 5 pages must produce 5 pages");
        assertEquals(5, doc.getPages().size(), "Page list size must match 5");

        // Verify that each of the 5 pages has non-empty valid content and correct 1-based page numbers
        for (int i = 0; i < doc.getPages().size(); i++) {
            NotePageDto page = doc.getPages().get(i);
            assertEquals(i + 1, page.getPageNumber(), "Page number must be sequential 1-indexed");
            assertNotNull(page.getContent(), "Page content must not be null");
            assertNotNull(page.getContent().getTopicTitle(), "Page topicTitle must not be null");
            assertFalse(page.getContent().getTopicTitle().isBlank(), "Page topicTitle must not be blank");
        }

        verifyNoFiller(doc);
    }
}
