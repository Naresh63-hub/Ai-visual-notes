package com.visualnotes.ai;

import com.visualnotes.dto.PageContentDto;
import com.visualnotes.dto.PagePlanDto;
import com.visualnotes.dto.PromptAnalysisResponse;
import com.visualnotes.dto.SemanticDocumentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private final OpenAiProvider openAiProvider;
    private final GeminiProvider geminiProvider;
    private final SemanticEngineAiProvider semanticEngineAiProvider;

    @Value("${ai.provider:semantic}")
    private String configuredProvider;

    private volatile String lastAnnouncedProvider;

    public AiService(
            OpenAiProvider openAiProvider,
            GeminiProvider geminiProvider,
            SemanticEngineAiProvider semanticEngineAiProvider) {
        this.openAiProvider = openAiProvider;
        this.geminiProvider = geminiProvider;
        this.semanticEngineAiProvider = semanticEngineAiProvider;
    }

    private AIProvider getActiveProvider() {
        String requested = configuredProvider == null ? "gemini" : configuredProvider.trim();
        AIProvider active = semanticEngineAiProvider;
        String note = null;

        if ("gemini".equalsIgnoreCase(requested)) {
            if (geminiProvider.isConfigured()) {
                active = geminiProvider;
            } else if (openAiProvider.isConfigured()) {
                active = openAiProvider;
                note = "ai.provider=gemini but GEMINI_API_KEY is missing - using configured OpenAI provider.";
            } else {
                active = semanticEngineAiProvider;
                note = "ai.provider=gemini but no AI API key configured - running in explicit Offline Mode.";
            }
        } else if ("openai".equalsIgnoreCase(requested)) {
            if (openAiProvider.isConfigured()) {
                active = openAiProvider;
            } else if (geminiProvider.isConfigured()) {
                active = geminiProvider;
                note = "ai.provider=openai but AI_API_KEY is missing - using configured Gemini provider.";
            } else {
                active = semanticEngineAiProvider;
                note = "ai.provider=openai but no AI API key configured - running in explicit Offline Mode.";
            }
        } else if (!"semantic".equalsIgnoreCase(requested) && !"offline".equalsIgnoreCase(requested)) {
            active = semanticEngineAiProvider;
            note = "Unknown ai.provider='" + requested + "' - using built-in Offline Mode.";
        }

        String providerName = active.getProviderName() != null ? active.getProviderName() : active.getClass().getSimpleName();
        if (note != null) {
            log.warn("[AI PROVIDER] {} Active provider: {}", note, providerName);
            lastAnnouncedProvider = null;
        } else if (!providerName.equals(lastAnnouncedProvider)) {
            log.info("[AI PROVIDER] Active provider: {} (configured: {})", providerName, requested);
            lastAnnouncedProvider = providerName;
        }
        return active;
    }

    public SemanticDocumentDto generateDocument(
            String topic,
            String overallPrompt,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String plannedDiagramType) {

        log.info("Generating full document notes for topic: '{}', totalPages: {}", topic, totalPages);
        AIProvider provider = getActiveProvider();
        SemanticDocumentDto doc = provider.generateDocumentContent(topic, overallPrompt, totalPages, style, audience, difficulty, plannedDiagramType);
        if (doc.getTopicTitle() == null || doc.getTopicTitle().isBlank()) {
            doc.setTopicTitle(topic);
        }
        return doc;
    }

    public PromptAnalysisResponse analyzePrompt(String prompt, String preferredStyle, Integer requestedPageCount) {
        log.info("Analyzing study prompt: '{}'", prompt);
        PromptAnalysisResponse response = getActiveProvider().analyzePrompt(prompt, preferredStyle, requestedPageCount);
        validateAnalysis(response);
        return response;
    }

    public PagePlanDto planPages(String prompt, int pageCount, String audience, String style, String difficulty) {
        log.info("Planning {} pages for prompt: '{}' in style '{}'", pageCount, prompt, style);
        PagePlanDto plan = getActiveProvider().planPages(prompt, pageCount, audience, style, difficulty);
        validatePlan(plan);
        return plan;
    }

    public PageContentDto generatePage(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea,
            String plannedDiagramType) {

        log.info("Generating page {}/{} for topic: '{}'", pageNumber, totalPages, topic);
        PageContentDto content = getActiveProvider().generatePageContent(
                topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType
        );
        validatePageContent(content, topic, pageNumber, totalPages);
        return content;
    }

    public PageContentDto regeneratePage(PageContentDto currentContent, String instruction, String customModifier, String style) {
        log.info("Regenerating page {} with instruction: '{}'", currentContent.getPageNumber(), instruction);
        PageContentDto regenerated = getActiveProvider().regeneratePage(currentContent, instruction, customModifier, style);
        validatePageContent(regenerated, currentContent.getTopicTitle(), currentContent.getPageNumber(), currentContent.getTotalPages());
        return regenerated;
    }

    private void validateAnalysis(PromptAnalysisResponse analysis) {
        if (analysis.getDetectedTopics() == null || analysis.getDetectedTopics().isEmpty()) {
            analysis.setDetectedTopics(java.util.List.of("Study Topic"));
        }
        if (analysis.getSuggestedPageCount() == null || analysis.getSuggestedPageCount() < 1) {
            analysis.setSuggestedPageCount(1);
        }
    }

    private void validatePlan(PagePlanDto plan) {
        if (plan.getPages() == null || plan.getPages().isEmpty()) {
            throw new IllegalStateException("Generated page plan cannot be empty");
        }
    }

    private void validatePageContent(PageContentDto content, String expectedTopic, int pageNum, int totalPages) {
        if (content.getTopicTitle() == null || content.getTopicTitle().isBlank()) {
            content.setTopicTitle(expectedTopic);
        }
        content.setPageNumber(pageNum);
        content.setTotalPages(totalPages);
    }
}
