package com.visualnotes.ai;

import com.visualnotes.ai.domain.*;
import com.visualnotes.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SemanticEngineAiProvider implements AIProvider {

    private static final Logger log = LoggerFactory.getLogger(SemanticEngineAiProvider.class);

    private final TopicUnderstandingEngine topicUnderstandingEngine;
    private final ContentPlanningEngine contentPlanningEngine;
    private final ContentSynthesisEngine contentSynthesisEngine;
    private final ContentQualityValidator contentQualityValidator;
    private final PagePlanningEngine pagePlanningEngine;

    public SemanticEngineAiProvider(
            TopicUnderstandingEngine topicUnderstandingEngine,
            ContentPlanningEngine contentPlanningEngine,
            ContentSynthesisEngine contentSynthesisEngine,
            ContentQualityValidator contentQualityValidator,
            PagePlanningEngine pagePlanningEngine) {
        this.topicUnderstandingEngine = topicUnderstandingEngine;
        this.contentPlanningEngine = contentPlanningEngine;
        this.contentSynthesisEngine = contentSynthesisEngine;
        this.contentQualityValidator = contentQualityValidator;
        this.pagePlanningEngine = pagePlanningEngine;
    }

    @Override
    public String getProviderName() {
        return "SemanticEngine";
    }

    @Override
    public PromptAnalysisResponse analyzePrompt(String prompt, String preferredStyle, Integer requestedPageCount) {
        if (prompt == null || prompt.trim().isEmpty()) {
            prompt = "Binary Search";
        }
        TopicUnderstandingEngine.TopicUnderstandingResult understanding =
                topicUnderstandingEngine.understand(prompt, requestedPageCount);

        String mainSubject = understanding.getNormalizedTopic();
        if (understanding.getPrimarySubtopics() != null && understanding.getPrimarySubtopics().size() > 1) {
            mainSubject = String.join(" vs ", understanding.getPrimarySubtopics());
        }

        String pageMode = understanding.getRequestedPageCount() != null ? "EXPLICIT" : "AUTOMATIC";

        return PromptAnalysisResponse.builder()
                .rawPrompt(prompt.trim())
                .mainSubject(mainSubject)
                .domain(understanding.getDomain().name())
                .subdomain(understanding.getSubdomain())
                .requirements(new ArrayList<>(understanding.getRequiredBlocks()))
                .detectedTopics(understanding.getPrimarySubtopics())
                .topicCount(understanding.getPrimarySubtopics().size())
                .audience(understanding.getAcademicAudience())
                .difficulty(understanding.getDifficulty())
                .detailLevel("Detailed")
                .detectedStyle(preferredStyle != null && !preferredStyle.isBlank() ? preferredStyle : "Handwritten")
                .pageMode(pageMode)
                .requiresDiagram(understanding.isRequiresDiagram())
                .requiresAlgorithm(understanding.isRequiresAlgorithm())
                .requiresExample(understanding.isRequiresWorkedExample())
                .isExamOriented(true)
                .requestedPageCount(understanding.getRequestedPageCount())
                .suggestedPageCount(understanding.getSuggestedPageCount())
                .quickPageOptions(List.of(1, 2, 3, 4))
                .userPromptFeedback(understanding.getPedagogicalGoal())
                .build();
    }

    @Override
    public PagePlanDto planPages(String prompt, int pageCount, String audience, String style, String difficulty) {
        TopicUnderstandingEngine.TopicUnderstandingResult understanding =
                topicUnderstandingEngine.understand(prompt, pageCount);
        ContentPlanningEngine.TeachingPlan plan =
                contentPlanningEngine.createTeachingPlan(understanding);

        return pagePlanningEngine.plan(prompt, pageCount, understanding, plan);
    }

    @Override
    public PageContentDto generatePageContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea,
            String plannedDiagramType) {

        String promptToAnalyze = (overallPrompt != null && !overallPrompt.isBlank()) ? overallPrompt : topic;
        TopicUnderstandingEngine.TopicUnderstandingResult understanding =
                topicUnderstandingEngine.understand(promptToAnalyze, totalPages);
        if (topic != null && !topic.isBlank()) {
            understanding.setNormalizedTopic(topicUnderstandingEngine.extractCleanTopicTitle(topic));
        }
        ContentPlanningEngine.TeachingPlan plan =
                contentPlanningEngine.createTeachingPlan(understanding);

        if (plannedDiagramType != null && !plannedDiagramType.isBlank()) {
            plan.setDiagramType(plannedDiagramType);
        }

        // 1. Synthesize Content
        PageContentDto page = contentSynthesisEngine.synthesizePage(
                understanding, plan, pageNumber, totalPages, style, audience, difficulty
        );

        // 2. Run Second-Pass Quality Critic & Template Purge
        contentQualityValidator.validateAndSanitize(page, understanding, plan);

        return page;
    }

    @Override
    public PageContentDto regeneratePage(PageContentDto current, String instruction, String customModifier, String style) {
        String topic = current.getTopicTitle();
        String combinedPrompt = (instruction != null ? instruction : "") + " " + (customModifier != null ? customModifier : "");

        PageContentDto regenerated = generatePageContent(
                topic,
                topic + " " + combinedPrompt,
                current.getPageNumber(),
                current.getTotalPages(),
                style != null ? style : current.getStyleTheme(),
                current.getCategoryBadge(),
                current.getDifficultyLevel(),
                "Regenerated: " + combinedPrompt.trim(),
                current.getDiagram() != null ? current.getDiagram().getType() : "concept-map"
        );

        return regenerated;
    }
}
