package com.visualnotes.service;

import com.visualnotes.ai.SemanticEngineAiProvider;
import com.visualnotes.ai.domain.*;
import com.visualnotes.ai.strategy.*;
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
                "Define NFA and DFA", "Define NFA and DFA with 5-tuple and comparison", 1, 1,
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
