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
        plan.setFormulaTitle("Core Mathematical Formulation");

        // 3. Comparison Table
        plan.setIncludeComparisonTable(understanding.isRequiresComparisonTable());
        plan.setComparisonTableTitle("Comparative Analysis: " + understanding.getNormalizedTopic());

        // 4. Algorithm & Complexity
        plan.setIncludeAlgorithm(understanding.isRequiresAlgorithm());
        plan.setIncludePseudocode(understanding.isRequiresPseudocode());
        plan.setIncludeComplexity(understanding.isRequiresAlgorithm() && (understanding.getDomain() == DomainType.ALGORITHMS || understanding.getDomain() == DomainType.DATA_STRUCTURES));

        // 5. Worked Example & Exam Tips
        plan.setIncludeWorkedExample(understanding.isRequiresWorkedExample());
        plan.setExampleTitle("Worked Example & Step-by-Step Application");
        plan.setIncludeExamTips(true);

        // 6. Excluded blocks
        plan.setExcludedBlocks(understanding.getExplicitlyExcludedBlocks());

        // 7. Dynamic Pedagogical Sections Construction (No Generic Templates!)
        buildPedagogicalSections(plan, understanding);

        return plan;
    }

    private void buildPedagogicalSections(TeachingPlan plan, TopicUnderstandingEngine.TopicUnderstandingResult u) {
        List<PlannedSection> sections = new ArrayList<>();
        List<String> subtopics = u.getPrimarySubtopics();

        if (u.getIntent() == TopicUnderstandingEngine.UserIntent.COMPARE || subtopics.size() > 1) {
            // Comparative or multi-topic intent
            for (int i = 0; i < subtopics.size(); i++) {
                String sub = subtopics.get(i);
                sections.add(new PlannedSection(
                        (i + 1) + ". " + sub + " (Definition & Formal Properties)",
                        "Core Concept",
                        "Define " + sub + " with precise theoretical rules and characteristics.",
                        "HIGH",
                        List.of(sub + " definition", sub + " transition/mechanics", sub + " properties")
                ));
            }
        } else if (u.getIntent() == TopicUnderstandingEngine.UserIntent.DEFINE) {
            // Pure definition intent
            sections.add(new PlannedSection(
                    "1. Formal Definition & Theoretical Foundation",
                    "Formal Definition",
                    "Define " + u.getNormalizedTopic() + " rigorously.",
                    "HIGH",
                    List.of("Formal definition", "Notation", "Preconditions")
            ));
            sections.add(new PlannedSection(
                    "2. Core Properties & Invariants",
                    "Fundamental Properties",
                    "Key properties, rules, and mathematical behaviors.",
                    "HIGH",
                    List.of("Properties", "Operational behavior", "Invariants")
            ));
        } else if (u.getDomain() == DomainType.PHYSICS || u.getCategory() == TopicUnderstandingEngine.TopicCategory.PHYSICAL_LAW) {
            // Physics / physical law
            sections.add(new PlannedSection(
                    "1. Physical Statement & Governing Principle",
                    "Physical Law",
                    "State the fundamental physical laws governing " + u.getNormalizedTopic() + ".",
                    "HIGH",
                    List.of("Physical law statement", "Sign conventions", "Vector equations")
            ));
            sections.add(new PlannedSection(
                    "2. Dynamics & Mathematical Formulations",
                    "Derivations",
                    "Relate forces, energy, or field variables mathematically.",
                    "HIGH",
                    List.of("Governing equations", "Physical meaning of variables", "Units & dimensions")
            ));
        } else if (u.getDomain() == DomainType.ALGORITHMS || u.getCategory() == TopicUnderstandingEngine.TopicCategory.ALGORITHM_PROCEDURE) {
            // Algorithm / procedure
            sections.add(new PlannedSection(
                    "1. Purpose & Preconditions",
                    "Prerequisites",
                    "Explain the problem statement, preconditions, and invariant.",
                    "HIGH",
                    List.of("Problem statement", "Input precondition", "Base assumptions")
            ));
            sections.add(new PlannedSection(
                    "2. Algorithmic Strategy (Divide & Conquer / Greedy / Iterative)",
                    "Core Logic",
                    "Explain the algorithmic mechanics and decision rules.",
                    "HIGH",
                    List.of("Core intuition", "Decision rule", "Branching logic")
            ));
        } else if (u.getDomain() == DomainType.DBMS) {
            // Database systems
            sections.add(new PlannedSection(
                    "1. Foundations & Anomaly Prevention",
                    "Relational Rules",
                    "Explain why " + u.getNormalizedTopic() + " is required and what anomalies it prevents.",
                    "HIGH",
                    List.of("Functional dependencies", "Redundancy anomalies", "Relational rules")
            ));
            sections.add(new PlannedSection(
                    "2. Step-by-Step Rules & Normal Forms",
                    "Decomposition",
                    "Detailed decomposition rules and candidate key criteria.",
                    "HIGH",
                    List.of("Normalization progression", "Lossless decomposition", "Dependency preservation")
            ));
        } else {
            // General topic: Concept + Working Mechanism
            sections.add(new PlannedSection(
                    "1. Core Principle & Intuition",
                    "Foundations",
                    "Explain what " + u.getNormalizedTopic() + " is and why it exists.",
                    "HIGH",
                    List.of("Definition", "Purpose", "Core intuition")
            ));
            sections.add(new PlannedSection(
                    "2. Mechanism & Structural Flow",
                    "Internal Mechanics",
                    "How " + u.getNormalizedTopic() + " operates internally.",
                    "HIGH",
                    List.of("Mechanism", "Step-by-step process", "Key interactions")
            ));
        }

        plan.setSections(sections);
    }
}
