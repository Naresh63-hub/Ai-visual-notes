package com.visualnotes.ai.domain;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ContentPlanningEngine {

    public static class PlannedSection {
        private String heading;
        private String badge;
        private String pedagogicalPurpose;
        private String priority; // HIGH, MEDIUM
        private List<String> targetConcepts;

        public PlannedSection() {}

        public PlannedSection(String heading, String badge, String pedagogicalPurpose, String priority, List<String> targetConcepts) {
            this.heading = heading;
            this.badge = badge;
            this.pedagogicalPurpose = pedagogicalPurpose;
            this.priority = priority;
            this.targetConcepts = targetConcepts;
        }

        public String getHeading() { return heading; }
        public void setHeading(String heading) { this.heading = heading; }
        public String getBadge() { return badge; }
        public void setBadge(String badge) { this.badge = badge; }
        public String getPedagogicalPurpose() { return pedagogicalPurpose; }
        public void setPedagogicalPurpose(String pedagogicalPurpose) { this.pedagogicalPurpose = pedagogicalPurpose; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public List<String> getTargetConcepts() { return targetConcepts; }
        public void setTargetConcepts(List<String> targetConcepts) { this.targetConcepts = targetConcepts; }
    }

    public static class TeachingPlan {
        private String topic;
        private DomainType domain;
        private TopicUnderstandingEngine.UserIntent intent;
        private List<PlannedSection> sections = new ArrayList<>();
        private boolean includeDiagram;
        private String diagramType;
        private String diagramTitle;
        private String diagramCaption;
        private boolean includeFormula;
        private String formulaTitle;
        private boolean includeComparisonTable;
        private String comparisonTableTitle;
        private boolean includeAlgorithm;
        private boolean includePseudocode;
        private boolean includeWorkedExample;
        private String exampleTitle;
        private boolean includeComplexity;
        private boolean includeExamTips;
        private Set<String> excludedBlocks = new HashSet<>();

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public DomainType getDomain() { return domain; }
        public void setDomain(DomainType domain) { this.domain = domain; }
        public TopicUnderstandingEngine.UserIntent getIntent() { return intent; }
        public void setIntent(TopicUnderstandingEngine.UserIntent intent) { this.intent = intent; }
        public List<PlannedSection> getSections() { return sections; }
        public void setSections(List<PlannedSection> sections) { this.sections = sections; }
        public boolean isIncludeDiagram() { return includeDiagram; }
        public void setIncludeDiagram(boolean includeDiagram) { this.includeDiagram = includeDiagram; }
        public String getDiagramType() { return diagramType; }
        public void setDiagramType(String diagramType) { this.diagramType = diagramType; }
        public String getDiagramTitle() { return diagramTitle; }
        public void setDiagramTitle(String diagramTitle) { this.diagramTitle = diagramTitle; }
        public String getDiagramCaption() { return diagramCaption; }
        public void setDiagramCaption(String diagramCaption) { this.diagramCaption = diagramCaption; }
        public boolean isIncludeFormula() { return includeFormula; }
        public void setIncludeFormula(boolean includeFormula) { this.includeFormula = includeFormula; }
        public String getFormulaTitle() { return formulaTitle; }
        public void setFormulaTitle(String formulaTitle) { this.formulaTitle = formulaTitle; }
        public boolean isIncludeComparisonTable() { return includeComparisonTable; }
        public void setIncludeComparisonTable(boolean includeComparisonTable) { this.includeComparisonTable = includeComparisonTable; }
        public String getComparisonTableTitle() { return comparisonTableTitle; }
        public void setComparisonTableTitle(String comparisonTableTitle) { this.comparisonTableTitle = comparisonTableTitle; }
        public boolean isIncludeAlgorithm() { return includeAlgorithm; }
        public void setIncludeAlgorithm(boolean includeAlgorithm) { this.includeAlgorithm = includeAlgorithm; }
        public boolean isIncludePseudocode() { return includePseudocode; }
        public void setIncludePseudocode(boolean includePseudocode) { this.includePseudocode = includePseudocode; }
        public boolean isIncludeWorkedExample() { return includeWorkedExample; }
        public void setIncludeWorkedExample(boolean includeWorkedExample) { this.includeWorkedExample = includeWorkedExample; }
        public String getExampleTitle() { return exampleTitle; }
        public void setExampleTitle(String exampleTitle) { this.exampleTitle = exampleTitle; }
        public boolean isIncludeComplexity() { return includeComplexity; }
        public void setIncludeComplexity(boolean includeComplexity) { this.includeComplexity = includeComplexity; }
        public boolean isIncludeExamTips() { return includeExamTips; }
        public void setIncludeExamTips(boolean includeExamTips) { this.includeExamTips = includeExamTips; }
        public Set<String> getExcludedBlocks() { return excludedBlocks; }
        public void setExcludedBlocks(Set<String> excludedBlocks) { this.excludedBlocks = excludedBlocks; }
    }

    public TeachingPlan createTeachingPlan(TopicUnderstandingEngine.TopicUnderstandingResult understanding) {
        TeachingPlan plan = new TeachingPlan();
        plan.setTopic(understanding.getNormalizedTopic());
        plan.setDomain(understanding.getDomain());
        plan.setIntent(understanding.getIntent());

        // 1. Diagrams
        plan.setIncludeDiagram(understanding.isRequiresDiagram());
        plan.setDiagramType(understanding.getSuggestedDiagramType());
        plan.setDiagramTitle(understanding.getNormalizedTopic() + " Concept Diagram");
        plan.setDiagramCaption("Visual representation of " + understanding.getNormalizedTopic());

        // 2. Mathematical Formulas
        plan.setIncludeFormula(understanding.isRequiresFormula());
        plan.setFormulaTitle("Core Mathematical / Scientific Formulation");

        // 3. Comparison Table
        plan.setIncludeComparisonTable(understanding.isRequiresComparisonTable());
        plan.setComparisonTableTitle("Comparative Analysis: " + understanding.getNormalizedTopic());

        // 4. Algorithm & Complexity
        plan.setIncludeAlgorithm(understanding.isRequiresAlgorithm());
        plan.setIncludePseudocode(understanding.isRequiresPseudocode());
        plan.setIncludeComplexity(understanding.isRequiresAlgorithm() && (understanding.getDomain() == DomainType.ALGORITHMS || understanding.getDomain() == DomainType.DATA_STRUCTURES));

        // 5. Worked Example & Exam Tips
        plan.setIncludeWorkedExample(understanding.isRequiresWorkedExample());
        plan.setExampleTitle("Worked Application & Step-by-Step Example");
        plan.setIncludeExamTips(true);

        // 6. Excluded blocks
        plan.setExcludedBlocks(understanding.getExplicitlyExcludedBlocks());

        // 7. Dynamic Pedagogical Sections Construction
        buildPedagogicalSections(plan, understanding);

        return plan;
    }

    private void buildPedagogicalSections(TeachingPlan plan, TopicUnderstandingEngine.TopicUnderstandingResult u) {
        List<PlannedSection> sections = new ArrayList<>();
        String topic = u.getNormalizedTopic();
        String lower = (topic + " " + u.getRawPrompt()).toLowerCase();
        List<String> scope = u.getRequestedScope();

        // Case 1: Multi-entity comparison (e.g. TCP vs UDP, Stack vs Queue, NFA vs DFA)
        if (u.isMultiEntityComparison() && u.getTargetEntities().size() >= 2) {
            String e1 = u.getTargetEntities().get(0);
            String e2 = u.getTargetEntities().get(1);

            sections.add(new PlannedSection(
                    "1. Conceptual Foundations & Operational Principles",
                    "Architectural Models",
                    "Compare the underlying design philosophy and operational mechanisms of " + e1 + " and " + e2 + ".",
                    "HIGH",
                    List.of(e1 + " operational model", e2 + " operational model", "Key design differences")
            ));
            sections.add(new PlannedSection(
                    "2. Performance Trade-offs & Selection Criteria",
                    "Protocol Comparison",
                    "Evaluate throughput, overhead, reliability, and real-world system applications.",
                    "HIGH",
                    List.of("Performance & overhead", "Reliability guarantees", "Typical industry use cases")
            ));
        }
        // Case 2: Explicit Scope / Classification (e.g. Network Topology & Types, OSI & Layers, Normalization & Forms, Deadlock & Conditions, Mitosis Phases)
        else if (scope != null && !scope.isEmpty() && scope.size() >= 2) {
            sections.add(new PlannedSection(
                    "1. Architectural Overview & Structural Taxonomy",
                    "Classification",
                    "Establish the fundamental classification criteria and structural organization of " + topic + ".",
                    "HIGH",
                    List.of("Core taxonomy", "Classification criteria", "Structural layout")
            ));

            sections.add(new PlannedSection(
                    "2. Deep-Dive: Specific Types & Operational Mechanics",
                    "Detailed Analysis",
                    "Examine each individual variant with its advantages, failure modes, and practical applications.",
                    "HIGH",
                    scope
            ));
        }
        // Case 3: Pure Definition
        else if (u.getIntent() == TopicUnderstandingEngine.UserIntent.DEFINE) {
            sections.add(new PlannedSection(
                    "1. Formal Mathematical & Theoretical Definition",
                    "Formal Definition",
                    "Define " + topic + " rigorously with standard academic notation and state models.",
                    "HIGH",
                    List.of("Formal definition", "Notation & tuples", "Preconditions")
            ));
            sections.add(new PlannedSection(
                    "2. Core Properties & Invariants",
                    "Fundamental Properties",
                    "Key properties, invariants, and operational behavior.",
                    "HIGH",
                    List.of("Properties & rules", "Equivalence criteria", "Systemic boundaries")
            ));
        }
        // Case 4: Physics / Mechanics
        else if (u.getDomain() == DomainType.PHYSICS || u.getCategory() == TopicUnderstandingEngine.TopicCategory.PHYSICAL_LAW) {
            sections.add(new PlannedSection(
                    "1. Physical Statement & Governing Laws",
                    "Physical Law",
                    "State the fundamental physical laws governing " + topic + ".",
                    "HIGH",
                    List.of("Physical law statement", "Sign conventions", "Vector equations")
            ));
            sections.add(new PlannedSection(
                    "2. Dynamics & Mathematical Formulation",
                    "Derivation",
                    "Relate physical variables, forces, or energy mathematically.",
                    "HIGH",
                    List.of("Governing equations", "Physical meaning of variables", "SI units & dimensions")
            ));
        }
        // Case 5: Electronics / Circuits
        else if (u.getDomain() == DomainType.ELECTRONICS) {
            sections.add(new PlannedSection(
                    "1. Device Physics & Operating Regions",
                    "Circuit Analysis",
                    "Analyze the operational states and electrical characteristics of " + topic + ".",
                    "HIGH",
                    List.of("Operating states", "Biasing conditions", "V-I characteristics")
            ));
            sections.add(new PlannedSection(
                    "2. Circuit Configuration & Switching Dynamics",
                    "Switching Action",
                    "Explain circuit schematic behavior, cut-off / saturation transitions, and equations.",
                    "HIGH",
                    List.of("Switching action", "Input/output voltages", "Design equations")
            ));
        }
        // Case 6: Biology / Life Sciences
        else if (u.getDomain() == DomainType.BIOLOGY) {
            sections.add(new PlannedSection(
                    "1. Cellular Mechanism & Biological Pathways",
                    "Pathways",
                    "Detail the cellular organelles, enzymes, and molecular stages involved in " + topic + ".",
                    "HIGH",
                    List.of("Organelle sites", "Molecular inputs/outputs", "Enzymatic regulation")
            ));
            sections.add(new PlannedSection(
                    "2. Sequential Stages & Energy Transformation",
                    "Bioenergetics",
                    "Trace the biochemical transformation and physiological significance.",
                    "HIGH",
                    List.of("Step-by-step phases", "Chemical transformations", "Physiological role")
            ));
        }
        // Case 7: Algorithms & Data Structures
        else if (u.getDomain() == DomainType.ALGORITHMS || u.getDomain() == DomainType.DATA_STRUCTURES || u.getCategory() == TopicUnderstandingEngine.TopicCategory.ALGORITHM_PROCEDURE) {
            sections.add(new PlannedSection(
                    "1. Purpose, Preconditions & Invariants",
                    "Prerequisites",
                    "Explain the problem statement, preconditions, and invariant.",
                    "HIGH",
                    List.of("Problem statement", "Input precondition", "Base assumptions")
            ));
            sections.add(new PlannedSection(
                    "2. Algorithmic Strategy & Procedural Execution",
                    "Core Logic",
                    "Explain the algorithmic mechanics and decision rules.",
                    "HIGH",
                    List.of("Core intuition", "Decision rule", "Branching logic")
            ));
        }
        // Case 8: Object-Oriented Programming & Software Engineering
        else if (u.getDomain() == DomainType.SOFTWARE_ENGINEERING || u.getDomain() == DomainType.PROGRAMMING) {
            sections.add(new PlannedSection(
                    "1. Core Paradigm & Theoretical Concept",
                    "OOP Principles",
                    "Explain the architectural purpose and programming paradigm for " + topic + ".",
                    "HIGH",
                    List.of("Conceptual definition", "Design rationale", "Key mechanisms")
            ));
            sections.add(new PlannedSection(
                    "2. Implementation Mechanics & Code Patterns",
                    "Code Structure",
                    "Show compile-time vs runtime behavior, method signatures, and practical design patterns.",
                    "HIGH",
                    List.of("Method binding", "Class hierarchy", "Best practices")
            ));
        }
        // Case 9: Mathematics
        else if (u.getDomain() == DomainType.MATHEMATICS || u.getIntent() == TopicUnderstandingEngine.UserIntent.DERIVE_FORMULATION) {
            sections.add(new PlannedSection(
                    "1. Statement of Problem & Algebraic Foundation",
                    "Mathematical Formulation",
                    "Establish the initial equations, definitions, and boundary conditions.",
                    "HIGH",
                    List.of("Standard form equation", "Initial assumptions", "Mathematical identity")
            ));
            sections.add(new PlannedSection(
                    "2. Step-by-Step Algebraic Derivation",
                    "Analytical Proof",
                    "Derive the governing formula step-by-step with clear algebraic justification.",
                    "HIGH",
                    List.of("Completing the square / transformation", "Isolating variables", "Final analytical roots")
            ));
        }
        // Default Domain-Specific Concept Breakdown
        else {
            sections.add(new PlannedSection(
                    "1. Fundamental Concepts & Principles",
                    u.getDomain().getDisplayName(),
                    "Explain what " + topic + " is, why it exists, and its core foundational principles.",
                    "HIGH",
                    List.of(topic + " definition", "Core operational rules", "Domain relevance")
            ));
            sections.add(new PlannedSection(
                    "2. Architecture, Operation & Key Dynamics",
                    "System Dynamics",
                    "Explain the internal mechanics, structural flow, and interactions of " + topic + ".",
                    "HIGH",
                    List.of("Internal mechanics", "Operational flow", "Key interactions")
            ));
        }

        plan.setSections(sections);
    }
}
