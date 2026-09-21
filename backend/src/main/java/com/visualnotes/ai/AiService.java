package com.visualnotes.ai;

import com.visualnotes.dto.PageContentDto;
import com.visualnotes.dto.PagePlanDto;
import com.visualnotes.dto.PromptAnalysisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private final OpenAiProvider openAiProvider;
    private final SemanticEngineAiProvider semanticEngineAiProvider;

    @Value("${ai.provider:semantic}")
    private String configuredProvider;

    public AiService(OpenAiProvider openAiProvider, SemanticEngineAiProvider semanticEngineAiProvider) {
        this.openAiProvider = openAiProvider;
        this.semanticEngineAiProvider = semanticEngineAiProvider;
    }

    private AIProvider getActiveProvider() {
        if ("openai".equalsIgnoreCase(configuredProvider) && openAiProvider.isConfigured()) {
            return openAiProvider;
        }
        return semanticEngineAiProvider;
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
        if (content.getDefinition() == null || content.getDefinition().isBlank()) {
            content.setDefinition(content.getTopicTitle() + " is an essential concept with structured mechanisms and practical applications.");
        }
        if (content.getSimpleExplanation() == null || content.getSimpleExplanation().isBlank()) {
            content.setSimpleExplanation("In simple terms, " + content.getTopicTitle() + " provides a streamlined method to solve specific computational or real-world tasks.");
        }
    }
}
