package com.visualnotes.service;

import com.visualnotes.ai.SemanticEngineAiProvider;
import com.visualnotes.ai.domain.*;
import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.PageContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BenchmarkTopicGenerationTest {

    private SemanticEngineAiProvider semanticEngine;

    @BeforeEach
    void setUp() {
        DiagramEngine diagramEngine = new DiagramEngine();
        TopicUnderstandingEngine topicEngine = new TopicUnderstandingEngine();
        ContentPlanningEngine planningEngine = new ContentPlanningEngine();
        ContentSynthesisEngine synthesisEngine = new ContentSynthesisEngine(diagramEngine);
        ContentQualityValidator validator = new ContentQualityValidator();
        PagePlanningEngine pageEngine = new PagePlanningEngine();

        semanticEngine = new SemanticEngineAiProvider(
                topicEngine,
                planningEngine,
                synthesisEngine,
                validator,
                pageEngine
        );
    }

    @Test
    void testBenchmark_TopologyAndTypes() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Network Topology", "Explain topology and types", 1, 1,
                "Handwritten", "Computer Science Students", "Standard", "Topologies & Types", "concept-map"
        );
        assertNotNull(content);
        assertEquals("Network Topology", content.getTopicTitle());
        assertEquals("Computer Networks & Protocols", content.getCategoryBadge());
        assertNotNull(content.getComparisonTable(), "Topology and types must generate comparison table of topologies");
        assertTrue(content.getComparisonTable().getRows().size() >= 4);
        assertNull(content.getAlgorithm(), "Network Topology must NOT have dummy CS algorithm");
        assertNull(content.getComplexity(), "Network Topology must NOT have Big-O complexity");

        // Verify absence of generic placeholder strings
        String serialized = content.toString().toLowerCase();
        assertFalse(serialized.contains("governs state progression"), "Must NOT contain placeholder 'governs state progression'");
        assertFalse(serialized.contains("system stabilizes"), "Must NOT contain placeholder 'system stabilizes'");
        assertFalse(serialized.contains("general academic theory"), "Must NOT have category 'General Academic Theory'");
    }

    @Test
    void testBenchmark_TcpVsUdp() {
        PageContentDto content = semanticEngine.generatePageContent(
                "TCP vs UDP", "Compare TCP and UDP", 1, 1,
                "Handwritten", "Networking Students", "Standard", "Protocol Comparison", "protocol-comparison"
        );
        assertNotNull(content);
        assertEquals("TCP vs UDP", content.getTopicTitle());
        assertEquals("Computer Networks & Protocols", content.getCategoryBadge());
        assertNotNull(content.getComparisonTable());
        assertNull(content.getAlgorithm());
    }

    @Test
    void testBenchmark_OsiModelAndLayers() {
        PageContentDto content = semanticEngine.generatePageContent(
                "OSI 7-Layer Reference Model", "Explain OSI model and its layers", 1, 1,
                "Handwritten", "Networking Students", "Standard", "7 Layers & PDUs", "layer-stack"
        );
        assertNotNull(content);
        assertTrue(content.getTopicTitle().contains("OSI"));
        assertEquals("Computer Networks & Protocols", content.getCategoryBadge());
        assertNotNull(content.getFormula(), "OSI model should include PDU encapsulation relation");
    }

    @Test
    void testBenchmark_StackVsQueue() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Stack vs Queue", "Compare stack and queue", 1, 1,
                "Handwritten", "CS Students", "Standard", "LIFO vs FIFO", "concept-map"
        );
        assertNotNull(content);
        assertEquals("Stack vs Queue", content.getTopicTitle());
        assertEquals("Data Structures", content.getCategoryBadge());
        assertNotNull(content.getComparisonTable());
    }

    @Test
    void testBenchmark_RecursionWithExample() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Recursion", "Explain recursion with an example", 1, 1,
                "Handwritten", "CS Students", "Standard", "Base Case & Stack Frames", "concept-map"
        );
        assertNotNull(content);
        assertEquals("Recursion", content.getTopicTitle());
        assertNotNull(content.getExample());
        assertTrue(content.getExample().getTitle().contains("Recursion") || content.getExample().getTitle().contains("Factorial"));
    }

    @Test
    void testBenchmark_PolymorphismWithExample() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Polymorphism", "Define polymorphism with example", 1, 1,
                "Handwritten", "OOP Students", "Standard", "Overloading vs Overriding", "concept-map"
        );
        assertNotNull(content);
        assertTrue(content.getTopicTitle().contains("Polymorphism"));
        assertNotNull(content.getExample());
    }

    @Test
    void testBenchmark_DeriveQuadraticFormula() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Quadratic Formula Derivation", "Derive the quadratic formula", 1, 1,
                "Handwritten", "Math Students", "Standard", "Completing the Square", "concept-map"
        );
        assertNotNull(content);
        assertEquals("Mathematics & Statistics", content.getCategoryBadge());
        assertNotNull(content.getFormula());
        assertTrue(content.getFormula().getExpression().contains("b^2 - 4ac") || content.getFormula().getExpression().contains("2a"));
        assertNull(content.getAlgorithm());
    }

    @Test
    void testBenchmark_DeadlockConditions() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Deadlock in Operating Systems", "Explain deadlock conditions", 1, 1,
                "Handwritten", "OS Students", "Standard", "Coffman Conditions", "concept-map"
        );
        assertNotNull(content);
        assertEquals("Operating Systems", content.getCategoryBadge());
        assertNull(content.getAlgorithm());
    }

    @Test
    void testBenchmark_TransistorAsSwitch() {
        PageContentDto content = semanticEngine.generatePageContent(
                "BJT Transistor as a Switch", "Explain transistor as a switch", 1, 1,
                "Handwritten", "Electronics Students", "Standard", "Cut-off & Saturation", "circuit-schematic"
        );
        assertNotNull(content);
        assertEquals("Electronics & Digital Logic", content.getCategoryBadge());
        assertNotNull(content.getFormula());
        assertNull(content.getAlgorithm());
    }

    @Test
    void testBenchmark_Mitosis() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Mitosis & Cell Division", "Explain mitosis", 1, 1,
                "Handwritten", "Biology Students", "Standard", "Phases of Mitosis", "science-reaction"
        );
        assertNotNull(content);
        assertEquals("Biology & Life Sciences", content.getCategoryBadge());
        assertNull(content.getAlgorithm());
    }

    @Test
    void testBenchmark1_BinarySearch() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Binary Search", "Explain Binary Search with trace and complexity", 1, 1,
                "Handwritten", "Engineering Students", "Intermediate", "Core Trace", "binary-search-array"
        );
        assertNotNull(content);
        assertEquals("Binary Search", content.getTopicTitle());
        assertNotNull(content.getAlgorithm());
        assertFalse(content.getAlgorithm().isEmpty());
        assertNotNull(content.getComplexity());
        assertEquals("O(1)", content.getComplexity().getTimeBest());
        assertEquals("O(log n)", content.getComplexity().getTimeWorst());
        assertNotNull(content.getDiagram());
        assertEquals("binary-search-array", content.getDiagram().getType());
    }

    @Test
    void testBenchmark2_NfaAndDfa() {
        PageContentDto content = semanticEngine.generatePageContent(
                "NFA vs DFA", "Define NFA and DFA with 5-tuple and comparison", 1, 1,
                "Handwritten", "University Exam", "Standard", "Formal Definition & Comparison", "automata-state-transition"
        );
        assertNotNull(content);
        assertTrue(content.getTopicTitle().contains("DFA") || content.getTopicTitle().contains("NFA"));
        assertNotNull(content.getComparisonTable(), "NFA vs DFA should produce a structured comparison table");
        assertTrue(content.getComparisonTable().getHeaders().size() >= 3);
        assertTrue(content.getComparisonTable().getRows().size() >= 4);
        assertNull(content.getAlgorithm(), "NFA vs DFA theoretical note should NOT force dummy algorithm");
        assertNotNull(content.getDiagram());
    }

    @Test
    void testBenchmark3_NewtonsLawsOfMotion() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Newton's Laws of Motion", "Newton's Laws of Motion with formulas and everyday examples", 1, 1,
                "Handwritten", "High School / College", "Fundamental", "3 Laws & Equations", "physics-diagram"
        );
        assertNotNull(content);
        assertNotNull(content.getFormula());
        assertTrue(content.getFormula().getExpression().contains("F") || content.getFormula().getExpression().contains("m"));
        assertNotNull(content.getExample());
        assertNull(content.getAlgorithm(), "Newton's laws should NOT contain algorithms");
        assertNotNull(content.getDiagram());
    }

    @Test
    void testBenchmark4_SqlJoin() {
        PageContentDto content = semanticEngine.generatePageContent(
                "SQL JOIN Types", "Explain SQL INNER, LEFT, RIGHT, FULL OUTER JOIN with Venn and table traces", 1, 1,
                "Handwritten", "Software Engineering Students", "Intermediate", "Join Types & Venn Sets", "sql-join-venn"
        );
        assertNotNull(content);
        assertNotNull(content.getComparisonTable(), "SQL JOIN should have a comparison table of join types");
        assertNotNull(content.getDiagram());
        assertEquals("sql-join-venn", content.getDiagram().getType());
    }

    @Test
    void testBenchmark5_Photosynthesis() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Photosynthesis", "Explain photosynthesis light and Calvin cycle reactions", 1, 1,
                "Handwritten", "Biology Students", "Standard", "Chemical Pathways", "science-reaction"
        );
        assertNotNull(content);
        assertNotNull(content.getFormula(), "Photosynthesis should have overall chemical reaction equation");
        assertTrue(content.getFormula().getExpression().contains("CO") || content.getFormula().getExpression().contains("Light") || content.getFormula().getExpression().contains("Chlorophyll"));
        assertNotNull(content.getDiagram());
    }

    @Test
    void testBenchmark6_OhmsLaw() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Ohm's Law", "Explain Ohm's Law with circuit diagram and IV characteristics", 1, 1,
                "Handwritten", "Physics & Electronics Students", "Foundational", "V = I * R and Linear Slope", "circuit-schematic"
        );
        assertNotNull(content);
        assertNotNull(content.getFormula());
        assertTrue(content.getFormula().getExpression().contains("V = I") || content.getFormula().getExpression().contains("I · R"));
        assertNotNull(content.getDiagram());
    }

    @Test
    void testBenchmark7_OsScheduling() {
        PageContentDto content = semanticEngine.generatePageContent(
                "CPU Scheduling Algorithms", "Explain FCFS, SJF, Round Robin with Gantt Chart and TAT/WT", 1, 1,
                "Handwritten", "Operating Systems Students", "Exam Ready", "Gantt Chart & Metrics", "os-gantt-chart"
        );
        assertNotNull(content);
        assertNotNull(content.getComparisonTable(), "OS CPU scheduling should have comparison table of policies");
        assertNotNull(content.getFormula(), "CPU scheduling should have TAT and WT formulas");
        assertNotNull(content.getDiagram());
        assertEquals("os-gantt-chart", content.getDiagram().getType());
    }

    @Test
    void testBenchmark8_DbmsNormalization() {
        PageContentDto content = semanticEngine.generatePageContent(
                "DBMS Normalization", "Explain 1NF, 2NF, 3NF, BCNF with functional dependencies", 1, 1,
                "Handwritten", "Database Students", "Standard", "1NF to BCNF Rules", "dbms-normalization"
        );
        assertNotNull(content);
        assertNotNull(content.getComparisonTable());
        assertNotNull(content.getDiagram());
    }
}
