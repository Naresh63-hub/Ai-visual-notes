package com.visualnotes.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visualnotes.ai.domain.*;
import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Transport-agnostic 3-step LLM pipeline:
 *   Step 1: semantic understanding of the prompt
 *   Step 2: educational content synthesis
 *   Step 3: critic & self-repair
 *
 * Concrete providers (OpenAI, Gemini, ...) only implement {@link #isConfigured()}
 * and {@link #callLlm(String, String)}; every prompt and the whole orchestration
 * live here so all backends behave identically.
 */
public abstract class LlmContentProvider implements AIProvider {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final SemanticEngineAiProvider semanticEngine;
    private final TopicUnderstandingEngine topicUnderstandingEngine;
    private final ContentPlanningEngine contentPlanningEngine;
    private final PagePlanningEngine pagePlanningEngine;
    private final ContentQualityValidator contentQualityValidator;
    private final DiagramEngine diagramEngine;
    protected final ObjectMapper objectMapper;

    protected LlmContentProvider(
            SemanticEngineAiProvider semanticEngine,
            TopicUnderstandingEngine topicUnderstandingEngine,
            ContentPlanningEngine contentPlanningEngine,
            PagePlanningEngine pagePlanningEngine,
            ContentQualityValidator contentQualityValidator,
            DiagramEngine diagramEngine,
            ObjectMapper objectMapper) {
        this.semanticEngine = semanticEngine;
        this.topicUnderstandingEngine = topicUnderstandingEngine;
        this.contentPlanningEngine = contentPlanningEngine;
        this.pagePlanningEngine = pagePlanningEngine;
        this.contentQualityValidator = contentQualityValidator;
        this.diagramEngine = diagramEngine;
        this.objectMapper = objectMapper.copy()
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    /** Whether a usable API key is present for this backend. */
    public abstract boolean isConfigured();

    /**
     * Send a system + user prompt to the LLM and return the raw text reply.
     * Implementations should already enforce JSON output where the backend supports it.
     */
    protected abstract String callLlm(String systemPrompt, String userPrompt) throws Exception;

    @Override
    public PromptAnalysisResponse analyzePrompt(String prompt, String preferredStyle, Integer requestedPageCount) {
        log.info("[REQUEST] Analyze prompt: '{}', requestedPages: {}", prompt, requestedPageCount);
        if (!isConfigured()) {
            return semanticEngine.analyzePrompt(prompt, preferredStyle, requestedPageCount);
        }

        try {
            SemanticAnalysisDto semanticAnalysis = understandPromptViaLlm(prompt, requestedPageCount);
            log.info("[AI UNDERSTANDING] mainTopic='{}', intent={}, domain={}, scope={}, isMultiEntity={}",
                    semanticAnalysis.getMainTopic(), semanticAnalysis.getIntent(), semanticAnalysis.getDomain(),
                    semanticAnalysis.getScope(), semanticAnalysis.isMultiEntityComparison());

            int targetPages = requestedPageCount != null ? requestedPageCount : Math.max(1, semanticAnalysis.getSuggestedPageCount());

            return PromptAnalysisResponse.builder()
                    .rawPrompt(prompt != null ? prompt.trim() : "")
                    .mainSubject(semanticAnalysis.getMainTopic())
                    .domain(semanticAnalysis.getDomain())
                    .subdomain(semanticAnalysis.getSubdomain())
                    .requirements(semanticAnalysis.getRequiredConcepts() != null ? semanticAnalysis.getRequiredConcepts() : List.of())
                    .detectedTopics(semanticAnalysis.getTargetEntities() != null && !semanticAnalysis.getTargetEntities().isEmpty()
                            ? semanticAnalysis.getTargetEntities()
                            : List.of(semanticAnalysis.getMainTopic()))
                    .topicCount(semanticAnalysis.getTargetEntities() != null ? Math.max(1, semanticAnalysis.getTargetEntities().size()) : 1)
                    .audience("Undergraduate / Exam Student")
                    .difficulty("Exam-Ready")
                    .detailLevel("Detailed")
                    .detectedStyle(preferredStyle != null && !preferredStyle.isBlank() ? preferredStyle : "Handwritten")
                    .pageMode(requestedPageCount != null ? "EXPLICIT" : "AUTOMATIC")
                    .requiresDiagram(semanticAnalysis.isRequiresDiagram())
                    .requiresAlgorithm(semanticAnalysis.isRequiresAlgorithm())
                    .requiresExample(semanticAnalysis.isRequiresWorkedExample())
                    .isExamOriented(true)
                    .requestedPageCount(requestedPageCount)
                    .suggestedPageCount(targetPages)
                    .quickPageOptions(List.of(1, 2, 3, 4))
                    .userPromptFeedback("Focused study notes on " + semanticAnalysis.getMainTopic() + (semanticAnalysis.getScope() != null ? " (" + semanticAnalysis.getScope() + ")" : ""))
                    .build();
        } catch (Exception e) {
            log.warn("{} prompt analysis error: {}. Falling back to Semantic Engine.", getProviderName(), e.getMessage());
            return semanticEngine.analyzePrompt(prompt, preferredStyle, requestedPageCount);
        }
    }

    @Override
    public PagePlanDto planPages(String prompt, int pageCount, String audience, String style, String difficulty) {
        log.info("[REQUEST] Plan {} pages for prompt: '{}'", pageCount, prompt);
        if (!isConfigured()) {
            return semanticEngine.planPages(prompt, pageCount, audience, style, difficulty);
        }

        try {
            SemanticAnalysisDto semanticAnalysis = understandPromptViaLlm(prompt, pageCount);
            log.info("[AI UNDERSTANDING] mainTopic='{}', domain={}, requestedPages={}",
                    semanticAnalysis.getMainTopic(), semanticAnalysis.getDomain(), pageCount);

            TopicUnderstandingEngine.TopicUnderstandingResult localUnderstanding =
                    topicUnderstandingEngine.understand(prompt, pageCount);
            localUnderstanding.setNormalizedTopic(semanticAnalysis.getMainTopic());
            localUnderstanding.setMultiEntityComparison(semanticAnalysis.isMultiEntityComparison());
            if (semanticAnalysis.getTargetEntities() != null && !semanticAnalysis.getTargetEntities().isEmpty()) {
                localUnderstanding.setTargetEntities(semanticAnalysis.getTargetEntities());
            }

            ContentPlanningEngine.TeachingPlan plan = contentPlanningEngine.createTeachingPlan(localUnderstanding);
            if (semanticAnalysis.getSuggestedDiagramType() != null && !semanticAnalysis.getSuggestedDiagramType().isBlank()) {
                plan.setDiagramType(semanticAnalysis.getSuggestedDiagramType());
            }

            PagePlanDto pagePlan = pagePlanningEngine.plan(prompt, pageCount, localUnderstanding, plan);
            log.info("[PAGE PLAN] Planned {} pages for topic: '{}'", pagePlan.getTotalPages(), pagePlan.getDocumentTitle());
            return pagePlan;
        } catch (Exception e) {
            log.warn("{} page planning error: {}. Falling back to Semantic Engine.", getProviderName(), e.getMessage());
            return semanticEngine.planPages(prompt, pageCount, audience, style, difficulty);
        }
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
        log.info("[REQUEST] Generate full document for topic='{}', overallPrompt='{}', totalPages={}", topic, promptToAnalyze, totalPages);

        if (!isConfigured()) {
            throw new IllegalStateException("AI provider '" + getProviderName() + "' is not configured. Please set the API key in the backend environment.");
        }

        try {
            // Step 1: Semantic Understanding
            SemanticAnalysisDto semanticAnalysis = understandPromptViaLlm(promptToAnalyze, totalPages);
            log.info("[AI UNDERSTANDING] mainTopic='{}', intent={}, domain={}, scope={}, isMultiEntity={}",
                    semanticAnalysis.getMainTopic(), semanticAnalysis.getIntent(), semanticAnalysis.getDomain(),
                    semanticAnalysis.getScope(), semanticAnalysis.isMultiEntityComparison());

            // Step 2: Educational Content Synthesis
            log.info("[AI SYNTHESIS] Synthesizing topic content via {}...", getProviderName());
            SemanticDocumentDto doc = synthesizeContentViaLlm(semanticAnalysis, promptToAnalyze, totalPages, audience, difficulty);
            log.info("[AI SYNTHESIS] Synthesized document with {} sections", doc.getSections() != null ? doc.getSections().size() : 0);

            // Step 3: Critic Review & Targeted Self-Repair
            log.info("[AI CRITIC] Reviewing document pedagogical quality and coverage...");
            doc = criticAndRepairViaLlm(doc, semanticAnalysis, promptToAnalyze, totalPages, audience, difficulty);

            // Step 4: Diagram Generation if missing or semantic
            if (doc.getDiagram() == null) {
                String diagramType = plannedDiagramType != null && !plannedDiagramType.isBlank()
                        ? plannedDiagramType
                        : (semanticAnalysis.getSuggestedDiagramType() != null ? semanticAnalysis.getSuggestedDiagramType() : "concept-map");
                DiagramDataDto diagram = diagramEngine.generateDiagram(doc.getTopicTitle(), diagramType, promptToAnalyze);
                doc.setDiagram(diagram);
            }

            // Step 5: Deterministic Validation
            contentQualityValidator.validateAndSanitize(doc, semanticAnalysis);

            return doc;
        } catch (Exception e) {
            log.error("{} document generation failed: {}", getProviderName(), e.getMessage(), e);
            throw new RuntimeException("AI generation failed (" + getProviderName() + "): " + e.getMessage(), e);
        }
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

        if (!isConfigured()) {
            return semanticEngine.generatePageContent(
                    topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType
            );
        }

        SemanticDocumentDto doc = generateDocumentContent(topic, overallPrompt, totalPages, style, audience, difficulty, plannedDiagramType);
        return pagePlanningEngine.distributeNoteDocument(doc, pageNumber, totalPages, style, audience, difficulty);
    }

    @Override
    public PageContentDto regeneratePage(PageContentDto currentContent, String instruction, String customModifier, String style) {
        String topic = currentContent.getTopicTitle();
        String combinedPrompt = (instruction != null ? instruction : "") + " " + (customModifier != null ? customModifier : "");
        return generatePageContent(
                topic,
                topic + " " + combinedPrompt,
                currentContent.getPageNumber(),
                currentContent.getTotalPages(),
                style != null ? style : currentContent.getStyleTheme(),
                "Undergraduate / Exam Student",
                currentContent.getDifficultyLevel() != null ? currentContent.getDifficultyLevel() : "Exam-Ready",
                instruction,
                currentContent.getDiagram() != null ? currentContent.getDiagram().getType() : null
        );
    }

    // ==========================================
    // STEP 1: Semantic Understanding
    // ==========================================
    protected SemanticAnalysisDto understandPromptViaLlm(String prompt, Integer requestedPages) throws Exception {
        String systemPrompt = """
                You are a world-class academic topic analysis engine.
                Analyze the user's study prompt and return a structured JSON object with EXACTLY these fields:
                - mainTopic (String): Canonical, accurate topic name (e.g., 'Network Topology', 'OSI Model', 'Binary Search', 'Photosynthesis', 'Newton's Laws of Motion', 'TCP vs UDP').
                - intent (String): One of [EXPLAIN, COMPARE, DEFINE, CLASSIFY_TYPES, PROCESS_MECHANISM, DERIVE_FORMULATION, WORKED_EXAMPLE].
                - scope (String): Detailed scope requested (e.g., 'Types of network topology: Bus, Star, Ring, Mesh, Hybrid' or 'Seven layers of the OSI model').
                - relationship (String): Relationship between concepts (e.g. 'TYPES_OF', 'COMPONENTS_OF', 'VS', 'STEP_BY_STEP', 'NONE').
                - domain (String): One of [COMPUTER_NETWORKS, ALGORITHMS, DATA_STRUCTURES, OPERATING_SYSTEMS, DBMS, AUTOMATA_THEORY, ARTIFICIAL_INTELLIGENCE, MATHEMATICS, PHYSICS, BIOLOGY, CHEMISTRY, SOFTWARE_ENGINEERING, GENERAL_ACADEMIC].
                - subdomain (String): Subfield (e.g., 'Network Topologies', 'Search Algorithms', 'Molecular Biology').
                - isMultiEntityComparison (boolean): true ONLY if comparing two distinct entities (e.g. TCP vs UDP, NFA vs DFA, Stack vs Queue).
                - targetEntities (List<String>): List of entities if comparison or classification.
                - requiredConcepts (List<String>): 3-6 core technical concepts that MUST be covered for this topic.
                - excludedConcepts (List<String>): Concepts that are irrelevant or out of scope.
                - requiresDiagram (boolean): true if a visual diagram/architecture is useful.
                - suggestedDiagramType (String): e.g., 'network-topology', 'binary-search-array', 'layer-stack', 'flowchart', 'concept-map', 'formula-math', 'protocol-comparison', 'dbms-normalization'.
                - requiresFormula (boolean): true only if mathematical formulas/equations exist.
                - requiresComparisonTable (boolean): true if comparing entities or types.
                - requiresAlgorithm (boolean): true only for CS algorithms/procedures.
                - requiresWorkedExample (boolean): true if worked numerical/code example is helpful.
                - suggestedPageCount (int): Recommended page count (default: 1-2).

                CRITICAL SEMANTIC RULES:
                1. If the user asks 'Explain topology and types', do NOT treat 'Types' as a separate topic. The main topic is 'Network Topology' and the scope is 'Types of network topology: Bus, Star, Ring, Mesh, Hybrid'.
                2. If the user asks 'Explain the OSI model and its seven layers', the main topic is 'OSI Model' and the scope is 'Seven layers of OSI model'.
                3. If the user asks 'Compare TCP and UDP', the intent is COMPARE and isMultiEntityComparison is true.
                4. If the user asks 'Explain binary search with an example', requiresWorkedExample must be true.
                5. If the user asks 'Define NFA and DFA', the intent is DEFINE and both automata should be explained and compared.
                6. If the user asks 'Explain photosynthesis and its stages', domain is BIOLOGY and scope includes light and dark reactions.
                """;

        String userPrompt = "Study prompt: '" + prompt + "', requestedPages: " + requestedPages;
        return parseJson(callLlm(systemPrompt, userPrompt), SemanticAnalysisDto.class);
    }

    // ==========================================
    // STEP 2: Educational Content Synthesis
    // ==========================================
    protected SemanticDocumentDto synthesizeContentViaLlm(
            SemanticAnalysisDto semantic,
            String originalPrompt,
            int totalPages,
            String audience,
            String difficulty) throws Exception {

        String systemPrompt = """
                You are a master university professor and pedagogical note author.
                Generate rigorous, clear, concise, highly structured academic study notes for the given topic in JSON.

                JSON SCHEMA:
                {
                  "topicTitle": "Topic Name",
                  "topicSubtitle": "Concise subtitle describing focus",
                  "categoryBadge": "Discipline / Domain Badge",
                  "domain": "DOMAIN_NAME",
                  "subdomain": "Subdomain",
                  "difficultyLevel": "Exam-Ready",
                  "definition": "Precise, formal 1-2 sentence definition",
                  "simpleExplanation": "Clear intuitive analogy or plain-English explanation",
                  "purpose": "Why this concept is fundamental and where it is applied",
                  "sections": [
                    {
                      "heading": "Specific Technical Section Heading",
                      "badge": "Badge Name",
                      "type": "concept|classification|comparison|process|example|formula|procedure",
                      "content": "Rich explanatory paragraph directly teaching this concept.",
                      "bulletPoints": ["Point 1 with clear explanation", "Point 2 with details"],
                      "highlights": ["Key term 1", "Key term 2"]
                    }
                  ],
                  "comparisonTable": {
                    "title": "Comparison Title (if comparing entities or types, else null)",
                    "headers": ["Parameter", "Entity A", "Entity B"],
                    "rows": [["Criteria 1", "Val A", "Val B"]],
                    "conclusion": "Summary takeaway"
                  },
                  "formula": {
                    "title": "Formula Name (if applicable, else null)",
                    "latex": "LaTeX formula string",
                    "explanation": "Detailed explanation of variables and meaning",
                    "variables": [{"symbol": "x", "meaning": "Description", "unit": "Unit"}]
                  },
                  "algorithm": [
                    {"stepNumber": 1, "action": "Step action", "explanation": "Detailed step context"}
                  ],
                  "pseudocode": "Clean pseudocode (if CS algorithm, else null)",
                  "example": {
                    "title": "Worked Example / Practical Case Study",
                    "scenario": "Problem statement",
                    "input": "Input parameters or test case",
                    "stepByStep": ["Step 1 trace...", "Step 2 trace...", "Step 3 trace..."],
                    "outputOrResult": "Final verified outcome",
                    "takeaway": "Key educational takeaway"
                  },
                  "complexity": {
                    "timeComplexityBest": "O(1)",
                    "timeComplexityAverage": "O(n)",
                    "timeComplexityWorst": "O(n)",
                    "spaceComplexity": "O(1)",
                    "explanation": "Complexity trade-offs"
                  },
                  "keyPoints": [
                    {"point": "Crucial concept or rule", "whyImportant": "Why this matters in exams/interviews", "category": "Concept", "starred": true}
                  ],
                  "examTips": [
                    {"tip": "High yield exam advice", "commonMistake": "Common trap or misconception", "mnemonic": "Helpful mnemonic or memory trigger"}
                  ]
                }

                RULES FOR PEDAGOGICAL INTEGRITY:
                1. NO GENERIC FILLER: Do NOT use generic placeholder phrases like 'Governs state progression', 'Concept Architecture & Core Mechanics', 'Foundations & Rules', 'Verify consistency and units'.
                2. DOMAIN RELEVANCE: Write actual, topic-specific formulas, laws, protocols, mechanisms, or definitions.
                3. DEPTH & SECTION VOLUME: Scale depth appropriately for the requested totalPages. When totalPages >= 3, provide at least totalPages technical sections in "sections" (minimum 1 section per page), ensuring rich and comprehensive coverage without leaving any page sparse.
                4. RELEVANCE ONLY: Do not output CS algorithms/pseudocode for physics, biology, or non-algorithmic topics.
                """;

        String userPrompt = String.format(
                "Generate comprehensive study notes for:\nTopic: %s\nScope: %s\nDomain: %s\nOriginal Prompt: %s\nTotal Pages to span: %d\nAudience: %s\nDifficulty: %s\nRequired Concepts: %s\nExcluded Concepts: %s",
                semantic.getMainTopic(),
                semantic.getScope(),
                semantic.getDomain(),
                originalPrompt,
                totalPages,
                audience,
                difficulty,
                semantic.getRequiredConcepts(),
                semantic.getExcludedConcepts()
        );

        return parseJson(callLlm(systemPrompt, userPrompt), SemanticDocumentDto.class);
    }

    // ==========================================
    // STEP 3: Critic & Self-Repair
    // ==========================================
    protected SemanticDocumentDto criticAndRepairViaLlm(
            SemanticDocumentDto doc,
            SemanticAnalysisDto semantic,
            String originalPrompt,
            int totalPages,
            String audience,
            String difficulty) {

        int maxIterations = 2;
        int iteration = 0;
        SemanticDocumentDto currentDoc = doc;

        while (iteration < maxIterations) {
            iteration++;
            try {
                String criticSystemPrompt = """
                        You are a strict Senior Academic Reviewer and Content Critic.
                        Evaluate the provided study note document JSON against the user's prompt and semantic goals.
                        Return a JSON object with:
                        - valid (boolean): true if the notes accurately and comprehensively teach the requested topic without generic boilerplate.
                        - answersRequest (boolean): true if the core user question is directly answered.
                        - scopeCovered (boolean): true if all requested subtopics / types are covered.
                        - genericFiller (boolean): true if generic meaningless boilerplate was detected (e.g. 'Governs state progression', 'Concept Architecture').
                        - irrelevantContent (List<String>): List of any irrelevant sections.
                        - missingConcepts (List<String>): Key concepts that were missing.
                        - errors (List<String>): Factual errors or mismatched domains.
                        - repairInstructions (List<String>): Specific instructions to fix the note if invalid.
                        """;

                String criticUserPrompt = "Topic: '" + semantic.getMainTopic() + "', Original Prompt: '" + originalPrompt + "'\nDocument JSON:\n"
                        + objectMapper.writeValueAsString(currentDoc);

                ContentCriticDto critic = parseJson(callLlm(criticSystemPrompt, criticUserPrompt), ContentCriticDto.class);

                log.info("[AI CRITIC] Iteration {}: valid={}, answersRequest={}, scopeCovered={}, genericFiller={}, missing={}",
                        iteration, critic.isValid(), critic.isAnswersRequest(), critic.isScopeCovered(), critic.isGenericFiller(), critic.getMissingConcepts());

                if (critic.isValid() && critic.isAnswersRequest() && !critic.isGenericFiller()) {
                    return currentDoc;
                }

                // If invalid, execute repair prompt
                log.info("[AI CRITIC] Initiating repair cycle for topic '{}' with instructions: {}", semantic.getMainTopic(), critic.getRepairInstructions());
                String repairSystemPrompt = """
                        You are a master academic professor and note editor.
                        You must repair the provided study note document JSON based on the reviewer's critique.
                        You MUST return the COMPLETE repaired NoteDocument JSON according to the schema:
                        - topicTitle (String)
                        - topicSubtitle (String)
                        - categoryBadge (String)
                        - definition (String)
                        - simpleExplanation (String)
                        - purpose (String)
                        - sections (Array of objects with heading, content, bulletPoints, highlights)
                        - comparisonTable (Object or null)
                        - formula (Object or null)
                        - algorithm (Array of objects with stepNumber, action, explanation or null)
                        - pseudocode (String or null)
                        - example (Object with title, scenario, input, stepByStep, outputOrResult, takeaway or null)
                        - complexity (Object with timeComplexityBest, timeComplexityAverage, timeComplexityWorst, spaceComplexity, explanation or null)
                        - keyPoints (Array of objects with point, whyImportant, category, starred)
                        - examTips (Array of objects with tip, commonMistake, mnemonic)

                        IMPORTANT: You must preserve and improve existing valid sections and fields, do NOT drop sections. Return valid JSON only.
                        """;

                String repairPrompt = String.format(
                        "Repair the study notes JSON for topic: '%s'.\n" +
                        "Reviewer Issues:\n- Generic Filler: %b\n- Missing Concepts: %s\n- Irrelevant: %s\n- Repair Instructions: %s\n\n" +
                        "Current Document JSON to repair:\n%s",
                        semantic.getMainTopic(),
                        critic.isGenericFiller(),
                        critic.getMissingConcepts(),
                        critic.getIrrelevantContent(),
                        critic.getRepairInstructions(),
                        objectMapper.writeValueAsString(currentDoc)
                );

                SemanticDocumentDto repairedDoc = parseJson(callLlm(repairSystemPrompt, repairPrompt), SemanticDocumentDto.class);
                if (repairedDoc != null) {
                    if ((repairedDoc.getSections() == null || repairedDoc.getSections().isEmpty()) && currentDoc.getSections() != null) {
                        repairedDoc.setSections(currentDoc.getSections());
                    }
                    if ((repairedDoc.getTopicTitle() == null || repairedDoc.getTopicTitle().isBlank()) && currentDoc.getTopicTitle() != null) {
                        repairedDoc.setTopicTitle(currentDoc.getTopicTitle());
                    }
                    if (repairedDoc.getDefinition() == null && currentDoc.getDefinition() != null) {
                        repairedDoc.setDefinition(currentDoc.getDefinition());
                    }
                    currentDoc = repairedDoc;
                }
            } catch (Exception e) {
                log.warn("[AI CRITIC] Error during critic/repair iteration {}: {}. Continuing with current doc.", iteration, e.getMessage());
                break;
            }
        }

        return currentDoc;
    }

    /**
     * Parse a model reply as JSON, tolerating markdown code fences
     * (```json ... ```) which some backends emit even in JSON mode.
     */
    protected <T> T parseJson(String rawReply, Class<T> type) throws Exception {
        return objectMapper.readValue(stripCodeFences(rawReply), type);
    }

    protected String stripCodeFences(String raw) {
        if (raw == null) {
            return null;
        }
        String text = raw.trim();
        if (text.startsWith("```")) {
            int firstNewline = text.indexOf('\n');
            int lastFence = text.lastIndexOf("```");
            if (firstNewline > 0 && lastFence > firstNewline) {
                text = text.substring(firstNewline + 1, lastFence).trim();
            }
        }
        return text;
    }
}
