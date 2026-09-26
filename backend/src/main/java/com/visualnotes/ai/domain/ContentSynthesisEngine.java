package com.visualnotes.ai.domain;

import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ContentSynthesisEngine {

    private final DiagramEngine diagramEngine;

    public ContentSynthesisEngine(DiagramEngine diagramEngine) {
        this.diagramEngine = diagramEngine;
    }

    public PageContentDto synthesizePage(
            TopicUnderstandingEngine.TopicUnderstandingResult understanding,
            ContentPlanningEngine.TeachingPlan plan,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty) {

        String topic = plan.getTopic();
        DomainType domain = plan.getDomain();

        PageContentDto page = new PageContentDto();
        page.setDocumentTitle(topic + (totalPages > 1 ? " (" + totalPages + " Pages)" : ""));
        page.setPageNumber(pageNumber);
        page.setTotalPages(totalPages);
        page.setTopicTitle(topic);
        page.setCategoryBadge(domain.getDisplayName());
        page.setDifficultyLevel(difficulty != null ? difficulty : understanding.getDifficulty());
        page.setStyleTheme(style != null ? style : "Handwritten");

        if (totalPages > 1) {
            page.setPagePartTitle("Part " + pageNumber + " of " + totalPages);
            page.setIsContinuation(pageNumber > 1);
            page.setContinuesOnNextPage(pageNumber < totalPages);
        }

        // 1. Definition & Core Intuition (Page 1 focus)
        if (pageNumber == 1) {
            page.setDefinition(synthesizeDefinition(topic, domain, understanding.getIntent()));
            page.setSimpleExplanation(synthesizeSimpleExplanation(topic, domain, understanding.getIntent()));
            if (understanding.getPrerequisiteConcept() != null) {
                page.setPurpose("Prerequisite & Context: Requires understanding of " + understanding.getPrerequisiteConcept() + ".");
            }
        }

        // 2. Sections: dynamically allocate sections based on page number
        List<SectionDto> synthesizedSections = new ArrayList<>();
        List<ContentPlanningEngine.PlannedSection> plannedSections = plan.getSections();

        if (plannedSections != null && !plannedSections.isEmpty()) {
            int sectionsPerPage = (int) Math.ceil((double) plannedSections.size() / totalPages);
            int startIdx = (pageNumber - 1) * sectionsPerPage;
            int endIdx = Math.min(plannedSections.size(), startIdx + sectionsPerPage);

            for (int i = startIdx; i < endIdx; i++) {
                ContentPlanningEngine.PlannedSection ps = plannedSections.get(i);
                synthesizedSections.add(synthesizeSectionContent(topic, domain, ps, understanding.getIntent()));
            }
        }
        page.setSections(synthesizedSections.isEmpty() ? null : synthesizedSections);

        // 3. Diagram (Render on Page 1 or where appropriate)
        if (plan.isIncludeDiagram() && (pageNumber == 1 || (totalPages == 2 && pageNumber == 1) || (totalPages > 2 && pageNumber <= 2))) {
            DiagramDataDto diag = diagramEngine.generateDiagram(topic, plan.getDiagramType(), understanding.getRawPrompt());
            page.setDiagram(diag);
        }

        // 4. Comparison Table (Render when comparing or multi-topic)
        if (plan.isIncludeComparisonTable() && (pageNumber == totalPages || totalPages == 1 || pageNumber == 2)) {
            page.setComparisonTable(synthesizeComparisonTable(understanding));
        }

        // 5. Mathematical Formula
        if (plan.isIncludeFormula() && (pageNumber == 1 || totalPages == 1 || (totalPages >= 2 && pageNumber == 2))) {
            page.setFormula(synthesizeFormula(topic, domain, understanding.getIntent()));
        }

        // 6. Algorithm & Pseudocode
        if (plan.isIncludeAlgorithm() && (pageNumber == totalPages || totalPages == 1 || pageNumber == 2)) {
            page.setAlgorithm(synthesizeAlgorithmSteps(topic, domain));
            if (plan.isIncludePseudocode()) {
                page.setPseudocode(synthesizePseudocode(topic, domain));
            }
        }

        // 7. Complexity Analysis
        if (plan.isIncludeComplexity() && (pageNumber == totalPages || totalPages == 1)) {
            page.setComplexity(synthesizeComplexity(topic));
        }

        // 8. Worked Example
        if (plan.isIncludeWorkedExample() && (pageNumber == totalPages || totalPages == 1 || pageNumber == 2)) {
            page.setExample(synthesizeWorkedExample(topic, domain));
        }

        // 9. Key Study Points & Exam Tips (Render on last page or single page)
        if (pageNumber == totalPages || totalPages == 1) {
            page.setKeyPoints(synthesizeKeyPoints(topic, domain, understanding.getIntent()));
            if (plan.isIncludeExamTips()) {
                page.setExamTips(synthesizeExamTips(topic, domain, understanding.getIntent()));
            }
        }

        return page;
    }

    private String synthesizeDefinition(String topic, DomainType domain, TopicUnderstandingEngine.UserIntent intent) {
        String lower = topic.toLowerCase();
        if (lower.contains("nfa") && lower.contains("dfa")) {
            return "Finite Automata are mathematical models of computation used to recognize Regular Languages. A DFA deterministically transitions to exactly one state per symbol, while an NFA can transition to zero, one, or multiple states (or spontaneously on ε).";
        }
        if (lower.contains("binary search")) {
            return "Binary Search is an optimal O(log n) divide-and-conquer searching algorithm that locates the position of a target key in a strictly sorted array by halving the search space at each iteration.";
        }
        if (lower.contains("newton")) {
            return "Newton's 3 Laws of Motion form the foundation of classical mechanics, describing the relationship between the forces acting on a body and the resulting physical motion of that body.";
        }
        if (lower.contains("normalization")) {
            return "Database Normalization is the systematic technique of organizing relational database schemas to eliminate data redundancy, enforce functional dependencies, and prevent insertion, deletion, and update anomalies.";
        }
        if (lower.contains("ohm")) {
            return "Ohm's Law states that the steady electrical current flowing through an Ohmic conductor between two points is directly proportional to the voltage across the two points, provided physical conditions (temperature) remain constant: V = I · R.";
        }
        return topic + " is a foundational concept in " + domain.getDisplayName() + " establishing rigorous theoretical principles, operational mechanics, and core analytical frameworks.";
    }

    private String synthesizeSimpleExplanation(String topic, DomainType domain, TopicUnderstandingEngine.UserIntent intent) {
        String lower = topic.toLowerCase();
        if (lower.contains("nfa") && lower.contains("dfa")) {
            return "In simple terms: A DFA reads an input and follows one strict path (like a predictable machine). An NFA explores multiple possibilities in parallel (like an optimistic tree of choices).";
        }
        if (lower.contains("binary search")) {
            return "In simple terms: Look at the middle item. If your target is smaller, search only the left half. If larger, search the right half. Repeat until found.";
        }
        if (lower.contains("newton")) {
            return "In simple terms: Objects keep doing what they are doing unless pushed (1st), pushing harder makes them accelerate faster (2nd), and every push creates an equal pushback (3rd).";
        }
        if (lower.contains("normalization")) {
            return "In simple terms: Store each piece of information in exactly one place to prevent duplicate data, avoid conflicting updates, and maintain clean table relations.";
        }
        return "Core Intuition: " + topic + " simplifies complex multi-variable interactions into structured, repeatable rules and predictable behavior.";
    }

    private SectionDto synthesizeSectionContent(String topic, DomainType domain, ContentPlanningEngine.PlannedSection ps, TopicUnderstandingEngine.UserIntent intent) {
        List<String> bullets = new ArrayList<>();
        List<String> highlights = new ArrayList<>();

        for (String concept : ps.getTargetConcepts()) {
            bullets.add("• " + concept + ": Governs state progression and maintains systemic correctness.");
            highlights.add(concept);
        }

        return SectionDto.builder()
                .heading(ps.getHeading())
                .badge(ps.getBadge())
                .content(ps.getPedagogicalPurpose())
                .bulletPoints(bullets)
                .highlights(highlights)
                .build();
    }

    private ComparisonTableDto synthesizeComparisonTable(TopicUnderstandingEngine.TopicUnderstandingResult u) {
        String lower = (u.getNormalizedTopic() + " " + u.getRawPrompt()).toLowerCase();
        if (lower.contains("nfa") && lower.contains("dfa")) {
            return ComparisonTableDto.builder()
                    .title("Key Differences: DFA vs NFA")
                    .headers(List.of("Parameter / Feature", "Deterministic (DFA)", "Non-Deterministic (NFA)"))
                    .rows(List.of(
                            List.of("Transition Rule (δ)", "δ : Q × Σ ➔ Q (Unique next state)", "δ : Q × (Σ ∪ {ε}) ➔ 2^Q (Set of states)"),
                            List.of("Null (ε) Transitions", "Strictly Forbidden", "Permitted (State change without input)"),
                            List.of("Execution Path", "Single deterministic path", "Multiple parallel computational branches"),
                            List.of("HW/SW Execution", "Fast and Direct O(n)", "Requires backtracking or subset construction"),
                            List.of("Language Power", "Recognizes Regular Languages", "Recognizes Regular Languages (Equal Power)")
                    ))
                    .conclusion("Equivalence Theorem: Every NFA can be converted to an equivalent DFA (using Subset Construction with up to 2ⁿ states).")
                    .build();
        }

        if (lower.contains("join") || (lower.contains("sql") && lower.contains("inner"))) {
            return ComparisonTableDto.builder()
                    .title("Comparison of SQL JOIN Types")
                    .headers(List.of("JOIN Type", "Matching Condition", "Unmatched Rows Handled"))
                    .rows(List.of(
                            List.of("INNER JOIN", "Returns rows when keys match in BOTH tables", "Excluded completely"),
                            List.of("LEFT JOIN", "Returns ALL rows from left table + matched right rows", "Right columns filled with NULL"),
                            List.of("RIGHT JOIN", "Returns ALL rows from right table + matched left rows", "Left columns filled with NULL"),
                            List.of("FULL OUTER JOIN", "Returns all rows from both tables", "Non-matching columns filled with NULL"),
                            List.of("CROSS JOIN", "Cartesian product of both tables", "Produces (M × N) row combinations")
                    ))
                    .conclusion("Summary: Use INNER JOIN for strict matching; LEFT/RIGHT OUTER JOIN to preserve incomplete records.")
                    .build();
        }

        if (lower.contains("scheduling") || lower.contains("fcfs") || lower.contains("round robin") || lower.contains("sjf")) {
            return ComparisonTableDto.builder()
                    .title("Comparison of CPU Scheduling Algorithms")
                    .headers(List.of("Algorithm", "Preemption Mode", "Key Strength", "Potential Drawback"))
                    .rows(List.of(
                            List.of("FCFS (First-Come, First-Served)", "Non-preemptive", "Simple FIFO queue implementation", "Convoy effect; high average waiting time"),
                            List.of("SJF (Shortest Job First)", "Non-preemptive / Preemptive (SRTF)", "Optimal minimum average waiting time", "Starvation of long processes; burst time unknown"),
                            List.of("Round Robin (RR)", "Preemptive (Time Quantum q)", "Fair CPU share; optimal response time", "High context-switch overhead if quantum is too short"),
                            List.of("Priority Scheduling", "Preemptive / Non-preemptive", "Handles urgent priority tasks", "Starvation of low-priority tasks (mitigated by aging)")
                    ))
                    .conclusion("Summary: Round Robin suits interactive time-sharing; SJF minimizes theoretical wait times.")
                    .build();
        }

        if (lower.contains("normalization") || lower.contains("1nf") || lower.contains("2nf") || lower.contains("3nf")) {
            return ComparisonTableDto.builder()
                    .title("Normal Form Hierarchy (1NF to BCNF)")
                    .headers(List.of("Normal Form", "Core Requirement", "Anomaly Eliminated"))
                    .rows(List.of(
                            List.of("1NF", "Atomic attributes (no repeating groups/arrays)", "Multi-valued attributes & repeating columns"),
                            List.of("2NF", "In 1NF + No Partial Functional Dependencies", "Update & Delete anomalies on composite keys"),
                            List.of("3NF", "In 2NF + No Transitive Dependencies (X ➔ Y)", "Transitive data redundancy & phantom updates"),
                            List.of("BCNF", "Strict: For every X ➔ Y, X must be a Super Key", "All functional dependency anomalies")
                    ))
                    .conclusion("Goal: Reach 3NF or BCNF while ensuring lossless join and dependency preservation.")
                    .build();
        }

        // Dynamic 2-column or 3-column comparison table for arbitrary comparative topics
        List<String> subtopics = u.getPrimarySubtopics();
        String itemA = subtopics.size() > 0 ? subtopics.get(0) : "Concept A";
        String itemB = subtopics.size() > 1 ? subtopics.get(1) : "Concept B";

        return ComparisonTableDto.builder()
                .title("Comparative Summary: " + itemA + " vs " + itemB)
                .headers(List.of("Characteristic", itemA, itemB))
                .rows(List.of(
                        List.of("Primary Definition", "Core theoretical foundations of " + itemA, "Core theoretical foundations of " + itemB),
                        List.of("Operational Mechanism", "Direct execution model", "Alternative execution model"),
                        List.of("Key Strengths", "High efficiency and deterministic behavior", "Flexibility and broad expressiveness"),
                        List.of("Trade-offs / Limitations", "Specific boundary conditions apply", "Requires additional overhead or complexity")
                ))
                .conclusion("Summary: Choose " + itemA + " or " + itemB + " depending on specific workload and system requirements.")
                .build();
    }

    private FormulaDto synthesizeFormula(String topic, DomainType domain, TopicUnderstandingEngine.UserIntent intent) {
        String lower = topic.toLowerCase();
        if (lower.contains("nfa") || lower.contains("dfa")) {
            return FormulaDto.builder()
                    .title("5-Tuple Formal Mathematical Definition")
                    .expression("M = (Q, \\Sigma, \\delta, q_0, F)")
                    .explanation("Q = finite set of states, Σ = input alphabet, δ = transition function, q₀ ∈ Q = initial state, F ⊆ Q = set of accept states.")
                    .build();
        }
        if (lower.contains("binary search")) {
            return FormulaDto.builder()
                    .title("Safe Midpoint Calculation & Recurrence Relation")
                    .expression("mid = low + \\lfloor \\frac{high - low}{2} \\rfloor \\quad | \\quad T(n) = T(n/2) + O(1) \\implies O(\\log n)")
                    .explanation("Using low + (high - low)/2 strictly avoids integer arithmetic overflow when low + high > Integer.MAX_VALUE.")
                    .build();
        }
        if (lower.contains("newton")) {
            return FormulaDto.builder()
                    .title("Fundamental Force & Vector Dynamics")
                    .expression("\\vec{F}_{net} = m \\cdot \\vec{a} = \\frac{d\\vec{p}}{dt}")
                    .explanation("Net external force is the time rate of change of linear momentum (p = mv). If mass is constant, F = m·a.")
                    .build();
        }
        if (lower.contains("ohm")) {
            return FormulaDto.builder()
                    .title("Ohm's Law & Electrical Power Relations")
                    .expression("V = I \\cdot R \\quad | \\quad P = V \\cdot I = I^2 R = \\frac{V^2}{R}")
                    .explanation("V = Voltage (Volts), I = Current (Amperes), R = Resistance (Ohms Ω), P = Dissipated Power (Watts).")
                    .build();
        }
        if (lower.contains("photosynthesis") || lower.contains("calvin") || lower.contains("chloroplast") || domain == DomainType.BIOLOGY) {
            return FormulaDto.builder()
                    .title("Overall Chemical Reaction Equation for Photosynthesis")
                    .expression("6CO_2 + 6H_2O + \\text{Light Energy} \\xrightarrow{\\text{Chlorophyll}} C_6H_{12}O_6 + 6O_2")
                    .explanation("Carbon dioxide and water, in the presence of solar energy absorbed by chlorophyll, synthesize glucose and release oxygen.")
                    .build();
        }
        if (lower.contains("scheduling") || lower.contains("gantt") || domain == DomainType.OPERATING_SYSTEMS) {
            return FormulaDto.builder()
                    .title("CPU Scheduling Performance Metrics")
                    .expression("\\text{TAT} = \\text{Completion Time} - \\text{Arrival Time} \\quad | \\quad \\text{WT} = \\text{TAT} - \\text{Burst Time}")
                    .explanation("Turnaround Time (TAT) measures total elapsed time; Waiting Time (WT) measures duration spent in ready queue.")
                    .build();
        }
        return FormulaDto.builder()
                .title("Governing Relationship for " + topic)
                .expression("f(x) = \\sum_{i=1}^{n} w_i \\cdot x_i + b")
                .explanation("Mathematical formulation establishing proportional relationships and equilibrium states.")
                .build();
    }

    private List<AlgorithmStepDto> synthesizeAlgorithmSteps(String topic, DomainType domain) {
        List<AlgorithmStepDto> steps = new ArrayList<>();
        steps.add(AlgorithmStepDto.builder()
                .stepNumber(1)
                .instruction("Initialize boundaries and validate precondition: set low = 0, high = n - 1.")
                .build());
        steps.add(AlgorithmStepDto.builder()
                .stepNumber(2)
                .instruction("Compute safe midpoint: mid = low + (high - low) / 2.")
                .build());
        steps.add(AlgorithmStepDto.builder()
                .stepNumber(3)
                .instruction("Evaluate target vs A[mid]: if equal, return mid; if target < A[mid], set high = mid - 1; else set low = mid + 1.")
                .build());
        steps.add(AlgorithmStepDto.builder()
                .stepNumber(4)
                .instruction("Terminate when low > high (element not found) or match is returned.")
                .build());
        return steps;
    }

    private String synthesizePseudocode(String topic, DomainType domain) {
        return "function binarySearch(A, target):\n" +
                "    low = 0, high = A.length - 1\n" +
                "    while low <= high:\n" +
                "        mid = low + floor((high - low) / 2)\n" +
                "        if A[mid] == target: return mid\n" +
                "        else if A[mid] > target: high = mid - 1\n" +
                "        else: low = mid + 1\n" +
                "    return -1 // Target not found";
    }

    private ComplexityDto synthesizeComplexity(String topic) {
        return ComplexityDto.builder()
                .timeBest("O(1)")
                .timeAverage("O(log n)")
                .timeWorst("O(log n)")
                .space("O(1)")
                .build();
    }

    private ExampleDto synthesizeWorkedExample(String topic, DomainType domain) {
        String lower = topic.toLowerCase();
        if (lower.contains("nfa") || lower.contains("dfa")) {
            return ExampleDto.builder()
                    .title("State Transition Trace on String w = '101'")
                    .scenario("Trace language L = { strings ending in '01' } over alphabet Σ = {0, 1}.")
                    .stepByStep(List.of(
                            "Step 1: Start in state q₀. Read symbol '1' ➔ Transition δ(q₀, 1) = q₀.",
                            "Step 2: Read symbol '0' ➔ Transition δ(q₀, 0) = q₁.",
                            "Step 3: Read symbol '1' ➔ Transition δ(q₁, 1) = q₂ (Accept state ∈ F)."
                    ))
                    .outputOrResult("String '101' terminates in state q₂ ∈ F ➔ ACCEPTED.")
                    .build();
        }
        if (lower.contains("binary search")) {
            return ExampleDto.builder()
                    .title("Step-by-Step Search Trace for Target = 15")
                    .scenario("Given sorted array A = [2, 5, 7, 11, 15, 18, 21].")
                    .stepByStep(List.of(
                            "Iteration 1: Low = 0, High = 6 ➔ Mid = 3 (A[3]=11 < 15) ➔ Search Right: Low = 4.",
                            "Iteration 2: Low = 4, High = 6 ➔ Mid = 5 (A[5]=18 > 15) ➔ Search Left: High = 4.",
                            "Iteration 3: Low = 4, High = 4 ➔ Mid = 4 (A[4]=15 == 15) ➔ MATCH FOUND."
                    ))
                    .outputOrResult("Element 15 located successfully at Index 4 in 3 iterations.")
                    .build();
        }
        if (lower.contains("newton")) {
            return ExampleDto.builder()
                    .title("Vehicle Acceleration Calculation (2nd Law)")
                    .scenario("A car of mass m = 1200 kg is accelerated by a net engine force F = 3600 N.")
                    .stepByStep(List.of(
                            "Formula: a = F_net / m",
                            "Substitution: a = 3600 N / 1200 kg",
                            "Calculation: a = 3.0 m/s² forward"
                    ))
                    .outputOrResult("Acceleration a = 3.0 m/s² in the direction of the applied force.")
                    .build();
        }
        return ExampleDto.builder()
                .title("Worked Application of " + topic)
                .scenario("Applying standard principles to solve a representative academic problem.")
                .stepByStep(List.of(
                        "Step 1: Identify given parameters and boundary conditions.",
                        "Step 2: Apply the governing formula/rules systematically.",
                        "Step 3: Verify consistency and units."
                ))
                .outputOrResult("System stabilizes in the expected analytical state.")
                .build();
    }

    private List<KeyPointDto> synthesizeKeyPoints(String topic, DomainType domain, TopicUnderstandingEngine.UserIntent intent) {
        List<KeyPointDto> list = new ArrayList<>();
        list.add(KeyPointDto.builder()
                .point("Understand core mathematical and theoretical invariants of " + topic + " first.")
                .starred(true)
                .category("Core Concept")
                .build());
        list.add(KeyPointDto.builder()
                .point("Always draw the labeled diagram or transition model to gain maximum clarity.")
                .starred(true)
                .category("Diagram")
                .build());
        list.add(KeyPointDto.builder()
                .point("Verify boundary conditions and preconditions before applying formulas or algorithms.")
                .starred(false)
                .category("Application")
                .build());
        return list;
    }

    private List<ExamTipDto> synthesizeExamTips(String topic, DomainType domain, TopicUnderstandingEngine.UserIntent intent) {
        return List.of(ExamTipDto.builder()
                .tip("Exam Rule: Clearly state definitions, draw labeled diagrams first, and show step-by-step mathematical/procedural derivations.")
                .commonMistake("Writing vague descriptive paragraphs instead of formal definitions, equations, and numbered points.")
                .mnemonic("Define ➔ Draw ➔ Derive ➔ Conclude")
                .build());
    }
}
