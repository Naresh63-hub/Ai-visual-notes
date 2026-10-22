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
        log.info("[REQUEST] Analyze prompt: '{}', requestedPages: {}", prompt, requestedPageCount);
        TopicUnderstandingEngine.TopicUnderstandingResult understanding =
                topicUnderstandingEngine.understand(prompt, requestedPageCount);

        log.info("[UNDERSTANDING] mainConcept='{}', intent={}, domain={}, scope={}, isMultiEntity={}",
                understanding.getMainConcept(), understanding.getIntent(), understanding.getDomain(),
                understanding.getRequestedScope(), understanding.isMultiEntityComparison());

        String mainSubject = understanding.getNormalizedTopic();
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
        log.info("[REQUEST] Plan {} pages for prompt: '{}'", pageCount, prompt);
        TopicUnderstandingEngine.TopicUnderstandingResult understanding =
                topicUnderstandingEngine.understand(prompt, pageCount);
        ContentPlanningEngine.TeachingPlan plan =
                contentPlanningEngine.createTeachingPlan(understanding);

        log.info("[CONTENT PLAN] Topic: '{}', Sections: {}, IncludeDiagram: {}, IncludeTable: {}, IncludeFormula: {}",
                plan.getTopic(), plan.getSections().size(), plan.isIncludeDiagram(), plan.isIncludeComparisonTable(), plan.isIncludeFormula());

        return pagePlanningEngine.plan(prompt, pageCount, understanding, plan);
    }

    @Override
    public SemanticDocumentDto generateDocumentContent(
            String topic,
            String overallPrompt,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String plannedDiagramType) {

        String promptToAnalyze = (overallPrompt != null && !overallPrompt.isBlank()) ? overallPrompt : topic;
        log.info("[REQUEST] [OFFLINE] Generate full document for topic='{}', overallPrompt='{}', totalPages={}", topic, promptToAnalyze, totalPages);

        TopicUnderstandingEngine.TopicUnderstandingResult understanding =
                topicUnderstandingEngine.understand(promptToAnalyze, totalPages);
        if (topic != null && !topic.isBlank() && !understanding.isMultiEntityComparison()) {
            understanding.setNormalizedTopic(topicUnderstandingEngine.extractCleanTopicTitle(topic));
        }

        ContentPlanningEngine.TeachingPlan plan =
                contentPlanningEngine.createTeachingPlan(understanding);

        if (plannedDiagramType != null && !plannedDiagramType.isBlank()) {
            plan.setDiagramType(plannedDiagramType);
        }

        SemanticDocumentDto doc = contentSynthesisEngine.synthesizeDocument(
                understanding, plan, totalPages, style, audience, difficulty
        );

        // Convert understanding into SemanticAnalysisDto for validation
        SemanticAnalysisDto analysis = new SemanticAnalysisDto();
        analysis.setMainTopic(understanding.getNormalizedTopic());
        analysis.setDomain(understanding.getDomain().name());
        analysis.setSubdomain(understanding.getSubdomain());
        analysis.setScope(understanding.getRequestedScope() != null ? String.join(", ", understanding.getRequestedScope()) : "");
        analysis.setMultiEntityComparison(understanding.isMultiEntityComparison());
        contentQualityValidator.validateAndSanitize(doc, analysis);

        return doc;
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
        log.info("[REQUEST] Generate page {}/{} for topic='{}', overallPrompt='{}'", pageNumber, totalPages, topic, promptToAnalyze);

        TopicUnderstandingEngine.TopicUnderstandingResult understanding =
                topicUnderstandingEngine.understand(promptToAnalyze, totalPages);
        if (topic != null && !topic.isBlank() && !understanding.isMultiEntityComparison()) {
            understanding.setNormalizedTopic(topicUnderstandingEngine.extractCleanTopicTitle(topic));
        }

        log.info("[UNDERSTANDING] mainConcept='{}', intent={}, domain={}, scope={}",
                understanding.getMainConcept(), understanding.getIntent(), understanding.getDomain(), understanding.getRequestedScope());

        ContentPlanningEngine.TeachingPlan plan =
                contentPlanningEngine.createTeachingPlan(understanding);

        if (plannedDiagramType != null && !plannedDiagramType.isBlank()) {
            plan.setDiagramType(plannedDiagramType);
        }

        log.info("[CONTENT PLAN] Generating page {} with plan for topic='{}'", pageNumber, plan.getTopic());

        // 1. Synthesize Content
        PageContentDto page = contentSynthesisEngine.synthesizePage(
                understanding, plan, pageNumber, totalPages, style, audience, difficulty
        );
        log.info("[SYNTHESIS] Synthesized page {} title='{}', sections={}", pageNumber, page.getTopicTitle(), page.getSections() != null ? page.getSections().size() : 0);

        // 2. Run Second-Pass Quality Critic & Template Purge
        contentQualityValidator.validateAndSanitize(page, understanding, plan);
        log.info("[VALIDATION] Validated page {} topicTitle='{}', categoryBadge='{}'", pageNumber, page.getTopicTitle(), page.getCategoryBadge());

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
