package com.visualnotes.ai;

import com.visualnotes.ai.domain.ContentQualityValidator;
import com.visualnotes.ai.domain.DomainType;
import com.visualnotes.ai.domain.PromptUnderstandingEngine;
import com.visualnotes.ai.strategy.*;
import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SemanticEngineAiProvider implements AIProvider {

    private static final Logger log = LoggerFactory.getLogger(SemanticEngineAiProvider.class);

    private final DiagramEngine diagramEngine;
    private final PromptUnderstandingEngine promptUnderstandingEngine;
    private final ContentQualityValidator contentQualityValidator;
    private final PhysicsContentStrategy physicsStrategy;
    private final BiologyContentStrategy biologyStrategy;
    private final MathematicsContentStrategy mathStrategy;
    private final DbmsContentStrategy dbmsStrategy;
    private final NetworkingContentStrategy networkingStrategy;
    private final AiMlContentStrategy aiMlStrategy;
    private final AlgorithmsContentStrategy algorithmsStrategy;
    private final AutomataContentStrategy automataStrategy;
    private final OperatingSystemsContentStrategy operatingSystemsStrategy;
    private final GeneralTheoryContentStrategy generalStrategy;

    public SemanticEngineAiProvider(
            DiagramEngine diagramEngine,
            PromptUnderstandingEngine promptUnderstandingEngine,
            ContentQualityValidator contentQualityValidator,
            PhysicsContentStrategy physicsStrategy,
            BiologyContentStrategy biologyStrategy,
            MathematicsContentStrategy mathStrategy,
            DbmsContentStrategy dbmsStrategy,
            NetworkingContentStrategy networkingStrategy,
            AiMlContentStrategy aiMlStrategy,
            AlgorithmsContentStrategy algorithmsStrategy,
            AutomataContentStrategy automataStrategy,
            OperatingSystemsContentStrategy operatingSystemsStrategy,
            GeneralTheoryContentStrategy generalStrategy) {
        this.diagramEngine = diagramEngine;
        this.promptUnderstandingEngine = promptUnderstandingEngine;
        this.contentQualityValidator = contentQualityValidator;
        this.physicsStrategy = physicsStrategy;
        this.biologyStrategy = biologyStrategy;
        this.mathStrategy = mathStrategy;
        this.dbmsStrategy = dbmsStrategy;
        this.networkingStrategy = networkingStrategy;
        this.aiMlStrategy = aiMlStrategy;
        this.algorithmsStrategy = algorithmsStrategy;
        this.automataStrategy = automataStrategy;
        this.operatingSystemsStrategy = operatingSystemsStrategy;
        this.generalStrategy = generalStrategy;
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
        PromptUnderstandingEngine.PromptAnalysisResult analysis = promptUnderstandingEngine.analyze(prompt, preferredStyle, requestedPageCount);

        String mainSubject = analysis.getTopic();
        if (analysis.getDetectedTopics() != null && analysis.getDetectedTopics().size() > 1) {
            mainSubject = analysis.getDetectedTopics().get(0) + " & " + (analysis.getDetectedTopics().size() - 1) + " more topics";
        }

        String pageMode = analysis.getRequestedPageCount() != null ? "EXPLICIT" : "AUTOMATIC";
        boolean requiresAlgorithm = analysis.getDomain() == DomainType.ALGORITHMS || analysis.getDomain() == DomainType.DATA_STRUCTURES;

        return PromptAnalysisResponse.builder()
                .rawPrompt(prompt.trim())
                .mainSubject(mainSubject)
                .domain(analysis.getDomain().name())
                .subdomain(analysis.getSubdomain())
                .requirements(analysis.getRequirements())
                .detectedTopics(analysis.getDetectedTopics())
                .topicCount(analysis.getDetectedTopics() != null ? analysis.getDetectedTopics().size() : 1)
                .audience(analysis.getAudience())
                .difficulty(analysis.getDifficulty())
                .detailLevel(analysis.getDetailLevel())
                .detectedStyle(preferredStyle != null && !preferredStyle.isBlank() ? preferredStyle : "Handwritten")
                .pageMode(pageMode)
                .requiresDiagram(true)
                .requiresAlgorithm(requiresAlgorithm)
                .requiresExample(true)
                .isExamOriented(analysis.getAudience().contains("Exam") || prompt.toLowerCase().contains("exam"))
                .requestedPageCount(analysis.getRequestedPageCount())
                .suggestedPageCount(analysis.getSuggestedPageCount())
                .quickPageOptions(analysis.getQuickPageOptions())
                .userPromptFeedback(analysis.getFeedback())
                .build();
    }

    @Override
    public PagePlanDto planPages(String prompt, int pageCount, String audience, String style, String difficulty) {
        PromptUnderstandingEngine.PromptAnalysisResult analysis = promptUnderstandingEngine.analyze(prompt, style, pageCount);
        List<String> topics = analysis.getDetectedTopics();
        if (topics == null || topics.isEmpty()) {
            topics = List.of(analysis.getTopic());
        }

        int totalTopics = topics.size();
        int pagesNum = Math.max(1, pageCount);
        List<PagePlanItemDto> plannedPages = new ArrayList<>();

        if (totalTopics == 1) {
            String topic = topics.get(0);
            if (pagesNum == 1) {
                plannedPages.add(PagePlanItemDto.builder()
                        .pageNumber(1)
                        .pageTitle(topic + " (Core Overview)")
                        .topics(List.of(topic))
                        .focusArea("Definition, Principles, Visual Diagram & Key High-Yield Notes")
                        .plannedDiagramType(deriveDiagramType(topic, analysis.getDomain()))
                        .estimatedDensity("Balanced")
                        .keyConceptsSummary("Complete one-page synthesis of " + topic)
                        .build());
            } else if (pagesNum == 2) {
                plannedPages.add(PagePlanItemDto.builder()
                        .pageNumber(1)
                        .pageTitle(topic + " — Part 1: Concepts & Visual Architecture")
                        .topics(List.of(topic))
                        .focusArea("Definition, Core Principles, Intuition, and Full Step-by-Step Visual Diagram")
                        .plannedDiagramType(deriveDiagramType(topic, analysis.getDomain()))
                        .estimatedDensity("Spacious & Clear")
                        .keyConceptsSummary("Foundational Intuition and Visual Architecture")
                        .build());

                plannedPages.add(PagePlanItemDto.builder()
                        .pageNumber(2)
                        .pageTitle(topic + " — Part 2: Formulations, Trace & Exam Prep")
                        .topics(List.of(topic))
                        .focusArea(analysis.getDomain() == DomainType.ALGORITHMS ? "Detailed Algorithm, Pseudocode, Step-by-Step Trace & Complexity Proof" : "Governing Equations, Real-World Applications, Worked Example & Exam Tips")
                        .plannedDiagramType("formula-math")
                        .estimatedDensity("Spacious & Clear")
                        .keyConceptsSummary("Detailed Dynamics, Worked Example, and Exam High-Yield Points")
                        .build());
            } else {
                for (int i = 1; i <= pagesNum; i++) {
                    plannedPages.add(PagePlanItemDto.builder()
                            .pageNumber(i)
                            .pageTitle(topic + " — Part " + i + " of " + pagesNum)
                            .topics(List.of(topic))
                            .focusArea("In-depth breakdown part " + i + " for " + topic)
                            .plannedDiagramType(i == 1 ? deriveDiagramType(topic, analysis.getDomain()) : "formula-math")
                            .estimatedDensity("Spacious")
                            .keyConceptsSummary("Academic Section " + i)
                            .build());
                }
            }
        } else {
            // Multi-topic breakdown
            for (int i = 0; i < pagesNum; i++) {
                String topic = (i < totalTopics) ? topics.get(i) : ("Comparative Review & Summary of " + topics.get(0));
                plannedPages.add(PagePlanItemDto.builder()
                        .pageNumber(i + 1)
                        .pageTitle(topic)
                        .topics(List.of(topic))
                        .focusArea("Comprehensive breakdown, visual diagram, equations/principles, and worked examples")
                        .plannedDiagramType(deriveDiagramType(topic, analysis.getDomain()))
                        .estimatedDensity("Balanced")
                        .keyConceptsSummary("Structured study notes for " + topic)
                        .build());
            }
        }

        String docTitle = analysis.getTopic() + (pagesNum > 1 ? (" (" + pagesNum + " Pages)") : "");

        return PagePlanDto.builder()
                .documentTitle(docTitle)
                .totalPages(pagesNum)
                .overallSummary("Well-prepared study notes spanning " + pagesNum + " spacious page(s) in " + analysis.getDomain().getDisplayName() + ".")
                .style(style != null ? style : "Handwritten")
                .audience(audience != null ? audience : analysis.getAudience())
                .difficulty(difficulty != null ? difficulty : analysis.getDifficulty())
                .pages(plannedPages)
                .build();
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

        PromptUnderstandingEngine.PromptAnalysisResult analysis = promptUnderstandingEngine.analyze(topic + " " + overallPrompt, style, totalPages);
        DomainType domain = analysis.getDomain();

        PageContentDto content;

        switch (domain) {
            case THEORY_OF_COMPUTATION:
                content = automataStrategy.generate(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType, analysis.getRequirements());
                break;
            case OPERATING_SYSTEMS:
                content = operatingSystemsStrategy.generate(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType, analysis.getRequirements());
                break;
            case PHYSICS:
            case ELECTRONICS:
                content = physicsStrategy.generate(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType, analysis.getRequirements());
                break;
            case BIOLOGY:
                content = biologyStrategy.generate(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType, analysis.getRequirements());
                break;
            case MATHEMATICS:
                content = mathStrategy.generate(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType, analysis.getRequirements());
                break;
            case DBMS:
                content = dbmsStrategy.generate(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType, analysis.getRequirements());
                break;
            case COMPUTER_NETWORKS:
                content = networkingStrategy.generate(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType, analysis.getRequirements());
                break;
            case AI_ML:
                content = aiMlStrategy.generate(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType, analysis.getRequirements());
                break;
            case ALGORITHMS:
            case DATA_STRUCTURES:
                content = algorithmsStrategy.generate(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType, analysis.getRequirements());
                break;
            default:
                content = generalStrategy.generate(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType, analysis.getRequirements());
                break;
        }

        // Apply quality validation and sanitize domain purity
        contentQualityValidator.validateAndSanitize(content, domain, topic);

        return content;
    }

    @Override
    public PageContentDto regeneratePage(PageContentDto current, String instruction, String customModifier, String style) {
        String topic = current.getTopicTitle();
        String combinedModifier = (instruction != null ? instruction : "") + " " + (customModifier != null ? customModifier : "");
        String lower = combinedModifier.toLowerCase();

        PageContentDto regenerated = generatePageContent(
                topic,
                combinedModifier,
                current.getPageNumber(),
                current.getTotalPages(),
                style != null ? style : current.getStyleTheme(),
                current.getCategoryBadge(),
                current.getDifficultyLevel(),
                "Regenerated: " + combinedModifier.trim(),
                current.getDiagram() != null ? current.getDiagram().getType() : "concept-map"
        );

        if (lower.contains("simpler") || lower.contains("beginner")) {
            regenerated.setSimpleExplanation("Super Simple Breakdown: " + regenerated.getSimpleExplanation());
            regenerated.setDifficultyLevel("Beginner");
        } else if (lower.contains("exam")) {
            regenerated.setCategoryBadge("High-Yield Exam Notes ★★★");
            if (regenerated.getExamTips() != null) {
                regenerated.getExamTips().add(0, ExamTipDto.builder()
                        .tip("★ High Probability Exam Question: Always draw the labeled diagram first, state formulas, and write step-by-step points.")
                        .commonMistake("Writing paragraphs instead of clear numbered points and labeled drawings.")
                        .mnemonic("Read ➔ Draw ➔ Point-by-Point")
                        .build());
            }
        }

        return regenerated;
    }

    private String deriveDiagramType(String topic, DomainType domain) {
        String l = topic.toLowerCase();
        if (domain == DomainType.THEORY_OF_COMPUTATION || l.contains("nfa") || l.contains("dfa") || l.contains("automata")) return "automata-state-transition";
        if (domain == DomainType.OPERATING_SYSTEMS || l.contains("scheduling") || l.contains("gantt") || l.contains("round robin") || l.contains("fcfs") || l.contains("sjf")) return "os-gantt-chart";
        if (l.contains("sql") || l.contains("join")) return "sql-join-venn";
        if (l.contains("ohm") || l.contains("resistor") || l.contains("circuit")) return "circuit-schematic";
        if (domain == DomainType.PHYSICS || l.contains("newton") || l.contains("force")) return "physics-diagram";
        if (domain == DomainType.BIOLOGY || l.contains("photosynthesis") || l.contains("cell")) return "science-reaction";
        if (domain == DomainType.DBMS || l.contains("normalization") || l.contains("database")) return "dbms-normalization";
        if (domain == DomainType.AI_ML || l.contains("gradient descent") || l.contains("neural")) return "neural-network";
        if (domain == DomainType.COMPUTER_NETWORKS || l.contains("osi") || l.contains("layer")) return "layer-stack";
        if (domain == DomainType.MATHEMATICS || l.contains("bayes") || l.contains("math")) return "formula-math";
        if (l.contains("binary search")) return "binary-search-array";
        if (l.contains("quick sort") || l.contains("merge sort") || l.contains("sort")) return "sorting-partition";
        if (l.contains("bfs") || l.contains("tree") || l.contains("heap")) return "tree-traversal";
        if (l.contains("dfs") || l.contains("graph") || l.contains("dijkstra")) return "graph-network";
        return "concept-map";
    }
}
