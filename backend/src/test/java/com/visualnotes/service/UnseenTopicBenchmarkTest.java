package com.visualnotes.service;

import com.visualnotes.ai.SemanticEngineAiProvider;
import com.visualnotes.ai.domain.*;
import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.PageContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnseenTopicBenchmarkTest {

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
    void testUnseen_BlockchainConsensus() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Blockchain Consensus Mechanisms", "Explain blockchain consensus mechanisms", 1, 1,
                "Handwritten", "Students", "Standard", "Consensus", "concept-map"
        );
        assertNotNull(content);
        assertNotNull(content.getTopicTitle());
        assertNotNull(content.getDefinition());
        assertNotNull(content.getSections());
        assertFalse(content.getSections().isEmpty());
        assertNoGenericBoilerplate(content);
    }

    @Test
    void testUnseen_RestApiAndHttpMethods() {
        PageContentDto content = semanticEngine.generatePageContent(
                "REST API and HTTP Methods", "Explain REST API and HTTP methods", 1, 1,
                "Handwritten", "Software Engineering Students", "Standard", "REST & HTTP", "concept-map"
        );
        assertNotNull(content);
        assertNotNull(content.getTopicTitle());
        assertNotNull(content.getSections());
        assertNoGenericBoilerplate(content);
    }

    @Test
    void testUnseen_FourierTransform() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Fourier Transform", "Explain Fourier transform", 1, 1,
                "Handwritten", "Mathematics / Engineering Students", "Standard", "Frequency Domain", "formula-math"
        );
        assertNotNull(content);
        assertNotNull(content.getTopicTitle());
        assertNotNull(content.getDefinition());
        assertNoGenericBoilerplate(content);
    }

    @Test
    void testUnseen_CloudComputingServiceModels() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Cloud Computing Service Models", "Explain cloud computing service models", 1, 1,
                "Handwritten", "Students", "Standard", "IaaS PaaS SaaS", "layer-stack"
        );
        assertNotNull(content);
        assertNotNull(content.getTopicTitle());
        assertNotNull(content.getSections());
        assertNoGenericBoilerplate(content);
    }

    @Test
    void testUnseen_ProbabilityDistributions() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Probability Distributions", "Explain probability distributions", 1, 1,
                "Handwritten", "Students", "Standard", "Probability Theory", "formula-math"
        );
        assertNotNull(content);
        assertNotNull(content.getTopicTitle());
        assertNoGenericBoilerplate(content);
    }

    @Test
    void testUnseen_SemiconductorDiode() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Semiconductor Diode Characteristics", "Explain semiconductor diode characteristics", 1, 1,
                "Handwritten", "Physics / Electronics Students", "Standard", "PN Junction", "circuit-diagram"
        );
        assertNotNull(content);
        assertNotNull(content.getTopicTitle());
        assertNull(content.getAlgorithm(), "Diode must not have CS algorithm steps");
        assertNoGenericBoilerplate(content);
    }

    @Test
    void testUnseen_CompilerPhases() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Compiler Phases", "Explain compiler phases", 1, 1,
                "Handwritten", "CS Students", "Standard", "Lexical, Syntax, Semantic, CodeGen", "flowchart"
        );
        assertNotNull(content);
        assertNotNull(content.getTopicTitle());
        assertNotNull(content.getSections());
        assertNoGenericBoilerplate(content);
    }

    @Test
    void testUnseen_CrisprMechanism() {
        PageContentDto content = semanticEngine.generatePageContent(
                "CRISPR-Cas9 Mechanism", "Explain CRISPR and its basic mechanism", 1, 1,
                "Handwritten", "Biology Students", "Standard", "Gene Editing", "concept-map"
        );
        assertNotNull(content);
        assertNotNull(content.getTopicTitle());
        assertNull(content.getAlgorithm(), "CRISPR must not have CS algorithm steps");
        assertNull(content.getComplexity(), "CRISPR must not have Big-O complexity");
        assertNoGenericBoilerplate(content);
    }

    @Test
    void testUnseen_ThermodynamicsLaws() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Laws of Thermodynamics", "Explain thermodynamics laws", 1, 1,
                "Handwritten", "Physics Students", "Standard", "Zeroth, First, Second, Third Laws", "formula-math"
        );
        assertNotNull(content);
        assertNotNull(content.getTopicTitle());
        assertNull(content.getAlgorithm(), "Thermodynamics must not have CS algorithm");
        assertNoGenericBoilerplate(content);
    }

    @Test
    void testUnseen_Ipv4VsIpv6() {
        PageContentDto content = semanticEngine.generatePageContent(
                "IPv4 vs IPv6", "Compare IPv4 and IPv6", 1, 1,
                "Handwritten", "Networking Students", "Standard", "Protocol Comparison", "protocol-comparison"
        );
        assertNotNull(content);
        assertNotNull(content.getTopicTitle());
        assertNotNull(content.getComparisonTable(), "IPv4 vs IPv6 must include a comparison table");
        assertNoGenericBoilerplate(content);
    }

    @Test
    void testUnseen_GraphTraversal() {
        PageContentDto content = semanticEngine.generatePageContent(
                "Graph Traversal", "Explain graph traversal", 1, 1,
                "Handwritten", "CS Students", "Standard", "BFS and DFS", "tree-traversal"
        );
        assertNotNull(content);
        assertNotNull(content.getTopicTitle());
        assertNotNull(content.getSections());
        assertNoGenericBoilerplate(content);
    }

    private void assertNoGenericBoilerplate(PageContentDto content) {
        String serialized = content.toString().toLowerCase();
        assertFalse(serialized.contains("governs state progression"), "Must NOT contain generic filler 'governs state progression'");
        assertFalse(serialized.contains("system stabilizes"), "Must NOT contain generic filler 'system stabilizes'");
        assertFalse(serialized.contains("general academic theory"), "Must NOT have generic category badge 'General Academic Theory'");
    }
}
