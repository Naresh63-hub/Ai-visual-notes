package com.visualnotes.ai.domain;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ContentPlanningEngine {

    public static class TopicPlan {
        private String topic;
        private DomainType domain;
        private String intent; // "DEFINITION", "COMPARISON", "ALGORITHM", "MECHANISM", "FORMULATION", "SYSTEM_DESIGN"
        private Set<String> requiredBlocks;
        private Set<String> excludedBlocks;
        private String diagramType;
        private boolean needsComparisonTable;
        private boolean needsAlgorithm;
        private boolean needsPseudocode;
        private boolean needsFormula;
        private boolean needsExample;
        private boolean needsComplexity;
        private boolean needsExamTips;

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public DomainType getDomain() { return domain; }
        public void setDomain(DomainType domain) { this.domain = domain; }
        public String getIntent() { return intent; }
        public void setIntent(String intent) { this.intent = intent; }
        public Set<String> getRequiredBlocks() { return requiredBlocks; }
        public void setRequiredBlocks(Set<String> requiredBlocks) { this.requiredBlocks = requiredBlocks; }
        public Set<String> getExcludedBlocks() { return excludedBlocks; }
        public void setExcludedBlocks(Set<String> excludedBlocks) { this.excludedBlocks = excludedBlocks; }
        public String getDiagramType() { return diagramType; }
        public void setDiagramType(String diagramType) { this.diagramType = diagramType; }
        public boolean isNeedsComparisonTable() { return needsComparisonTable; }
        public void setNeedsComparisonTable(boolean needsComparisonTable) { this.needsComparisonTable = needsComparisonTable; }
        public boolean isNeedsAlgorithm() { return needsAlgorithm; }
        public void setNeedsAlgorithm(boolean needsAlgorithm) { this.needsAlgorithm = needsAlgorithm; }
        public boolean isNeedsPseudocode() { return needsPseudocode; }
        public void setNeedsPseudocode(boolean needsPseudocode) { this.needsPseudocode = needsPseudocode; }
        public boolean isNeedsFormula() { return needsFormula; }
        public void setNeedsFormula(boolean needsFormula) { this.needsFormula = needsFormula; }
        public boolean isNeedsExample() { return needsExample; }
        public void setNeedsExample(boolean needsExample) { this.needsExample = needsExample; }
        public boolean isNeedsComplexity() { return needsComplexity; }
        public void setNeedsComplexity(boolean needsComplexity) { this.needsComplexity = needsComplexity; }
        public boolean isNeedsExamTips() { return needsExamTips; }
        public void setNeedsExamTips(boolean needsExamTips) { this.needsExamTips = needsExamTips; }
    }

    public TopicPlan plan(String rawPrompt, DomainType domain) {
        TopicPlan plan = new TopicPlan();
        plan.setTopic(rawPrompt);
        plan.setDomain(domain);

        String lower = rawPrompt.toLowerCase().trim();
        Set<String> required = new HashSet<>();
        Set<String> excluded = new HashSet<>();

        // 1. Detect Intent & Structure
        if ((lower.contains("nfa") && lower.contains("dfa")) || lower.contains("finite automata") || lower.contains("automata")) {
            plan.setIntent("COMPARISON_AND_FORMAL_DEFINITION");
            plan.setDiagramType("automata-state-transition");
            plan.setNeedsComparisonTable(true);
            plan.setNeedsFormula(true); // 5-tuple formal definition: M = (Q, Sigma, delta, q0, F)
            plan.setNeedsExample(true);
            plan.setNeedsExamTips(true);
            
            // Explicitly exclude unnecessary sections
            plan.setNeedsComplexity(false);
            plan.setNeedsAlgorithm(false);
            plan.setNeedsPseudocode(false);
            excluded.addAll(List.of("complexity", "algorithm", "pseudocode", "advantages", "limitations", "code"));
            required.addAll(List.of("definition", "formal_notation", "comparison_table", "state_diagram", "exam_takeaways"));

        } else if (lower.contains("binary search") || lower.contains("linear search")) {
            plan.setIntent("ALGORITHM");
            plan.setDiagramType("binary-search-array");
            plan.setNeedsAlgorithm(true);
            plan.setNeedsPseudocode(true);
            plan.setNeedsComplexity(true);
            plan.setNeedsExample(true);
            plan.setNeedsExamTips(true);
            excluded.addAll(List.of("advantages", "limitations"));
            required.addAll(List.of("definition", "condition", "algorithm", "trace_example", "complexity", "diagram"));

        } else if (lower.contains("newton") || lower.contains("laws of motion")) {
            plan.setIntent("PHYSICAL_LAW");
            plan.setDiagramType("physics-diagram");
            plan.setNeedsFormula(true);
            plan.setNeedsExample(true);
            plan.setNeedsExamTips(true);
            plan.setNeedsComplexity(false);
            plan.setNeedsAlgorithm(false);
            plan.setNeedsPseudocode(false);
            excluded.addAll(List.of("complexity", "algorithm", "pseudocode"));
            required.addAll(List.of("three_laws", "vector_formulas", "fbd_diagram", "real_world_examples", "exam_tips"));

        } else if (lower.contains("sql") && (lower.contains("join") || lower.contains("joins") || lower.contains("inner") || lower.contains("left"))) {
            plan.setIntent("DATABASE_OPERATIONS");
            plan.setDiagramType("sql-join-venn");
            plan.setNeedsComparisonTable(true);
            plan.setNeedsExample(true);
            plan.setNeedsExamTips(true);
            plan.setNeedsComplexity(false);
            excluded.addAll(List.of("complexity", "pseudocode", "advantages"));
            required.addAll(List.of("definition", "join_types", "venn_diagram", "sql_syntax_example", "comparison_table"));

        } else if (lower.contains("photosynthesis")) {
            plan.setIntent("BIOLOGICAL_CYCLE");
            plan.setDiagramType("science-reaction");
            plan.setNeedsFormula(true); // Biochemical chemical equation
            plan.setNeedsExamTips(true);
            plan.setNeedsComplexity(false);
            plan.setNeedsAlgorithm(false);
            plan.setNeedsPseudocode(false);
            excluded.addAll(List.of("complexity", "algorithm", "pseudocode", "advantages"));
            required.addAll(List.of("definition", "chemical_equation", "light_reaction", "calvin_cycle", "chloroplast_diagram"));

        } else if (lower.contains("ohm") || lower.contains("kirchhoff") || lower.contains("resistor")) {
            plan.setIntent("CIRCUIT_LAW");
            plan.setDiagramType("circuit-schematic");
            plan.setNeedsFormula(true); // V = IR, P = VI
            plan.setNeedsExample(true);
            plan.setNeedsExamTips(true);
            plan.setNeedsComplexity(false);
            plan.setNeedsAlgorithm(false);
            plan.setNeedsPseudocode(false);
            excluded.addAll(List.of("complexity", "algorithm", "pseudocode"));
            required.addAll(List.of("statement", "formula", "circuit_diagram", "i_v_curve", "series_parallel_rules"));

        } else if (lower.contains("scheduling") || lower.contains("fcfs") || lower.contains("round robin") || lower.contains("sjf")) {
            plan.setIntent("OS_ALGORITHM");
            plan.setDiagramType("os-gantt-chart");
            plan.setNeedsFormula(true); // TAT = CT - AT, WT = TAT - BT
            plan.setNeedsExample(true);
            plan.setNeedsComparisonTable(true);
            plan.setNeedsExamTips(true);
            excluded.addAll(List.of("advantages", "limitations"));
            required.addAll(List.of("definition", "scheduling_criteria", "gantt_chart", "numerical_example", "formulas"));

        } else if (lower.contains("normalization") || lower.contains("1nf") || lower.contains("2nf") || lower.contains("3nf") || lower.contains("bcnf")) {
            plan.setIntent("DATABASE_NORMALIZATION");
            plan.setDiagramType("dbms-normalization");
            plan.setNeedsComparisonTable(true);
            plan.setNeedsExample(true);
            plan.setNeedsExamTips(true);
            plan.setNeedsComplexity(false);
            plan.setNeedsAlgorithm(false);
            excluded.addAll(List.of("complexity", "algorithm", "pseudocode"));
            required.addAll(List.of("definition", "anomalies", "normal_forms_rules", "decomposition_example", "diagram"));

        } else if (lower.contains("vs") || lower.contains("versus") || lower.contains("difference between") || lower.contains("compare")) {
            plan.setIntent("COMPARISON");
            plan.setDiagramType("concept-map");
            plan.setNeedsComparisonTable(true);
            plan.setNeedsExamTips(true);
            plan.setNeedsComplexity(false);
            excluded.addAll(List.of("complexity", "advantages", "limitations"));
            required.addAll(List.of("definitions", "comparison_table", "key_differences", "exam_takeaways"));

        } else if (domain == DomainType.ALGORITHMS || domain == DomainType.DATA_STRUCTURES) {
            plan.setIntent("ALGORITHM");
            plan.setDiagramType("sorting-partition");
            plan.setNeedsAlgorithm(true);
            plan.setNeedsComplexity(true);
            plan.setNeedsExample(true);
            plan.setNeedsExamTips(true);
            excluded.addAll(List.of("advantages", "limitations"));
            required.addAll(List.of("definition", "algorithm", "example", "complexity", "diagram"));

        } else {
            plan.setIntent("CONCEPT_EXPLANATION");
            plan.setDiagramType("concept-map");
            plan.setNeedsExample(true);
            plan.setNeedsExamTips(true);
            plan.setNeedsComplexity(false);
            plan.setNeedsAlgorithm(false);
            plan.setNeedsPseudocode(false);
            excluded.addAll(List.of("complexity", "algorithm", "pseudocode"));
            required.addAll(List.of("definition", "core_concepts", "worked_example", "diagram", "key_points"));
        }

        plan.setRequiredBlocks(required);
        plan.setExcludedBlocks(excluded);
        return plan;
    }
}
