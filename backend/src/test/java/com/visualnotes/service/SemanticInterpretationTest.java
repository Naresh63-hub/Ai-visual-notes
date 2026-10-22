package com.visualnotes.service;

import com.visualnotes.ai.domain.DomainType;
import com.visualnotes.ai.domain.TopicUnderstandingEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SemanticInterpretationTest {

    private TopicUnderstandingEngine topicEngine;

    @BeforeEach
    void setUp() {
        topicEngine = new TopicUnderstandingEngine();
    }

    @Test
    void testConnectedPhrase_TopologyAndTypes() {
        TopicUnderstandingEngine.TopicUnderstandingResult result =
                topicEngine.understand("Explain network topology and its types", 2);

        assertEquals("Network Topology", result.getNormalizedTopic());
        assertEquals(DomainType.COMPUTER_NETWORKS, result.getDomain());
        assertFalse(result.isMultiEntityComparison(), "Types must NOT be treated as a separate second entity");
        assertTrue(result.getRequestedScope().stream().anyMatch(s -> s.toLowerCase().contains("type")),
                "Scope should capture types");
    }

    @Test
    void testConnectedPhrase_OsiAndLayers() {
        TopicUnderstandingEngine.TopicUnderstandingResult result =
                topicEngine.understand("Explain the OSI model and its seven layers", 2);

        assertTrue(result.getNormalizedTopic().toLowerCase().contains("osi"));
        assertEquals(DomainType.COMPUTER_NETWORKS, result.getDomain());
        assertFalse(result.isMultiEntityComparison());
        assertTrue(result.getRequestedScope().stream().anyMatch(s -> s.toLowerCase().contains("layer")),
                "Scope should capture seven layers");
    }

    @Test
    void testComparison_TcpVsUdp() {
        TopicUnderstandingEngine.TopicUnderstandingResult result =
                topicEngine.understand("Compare TCP and UDP", 2);

        assertTrue(result.isMultiEntityComparison(), "TCP vs UDP must be marked as multi-entity comparison");
        assertEquals(TopicUnderstandingEngine.UserIntent.COMPARE, result.getIntent());
        assertTrue(result.getTargetEntities().stream().anyMatch(e -> e.equalsIgnoreCase("tcp")));
        assertTrue(result.getTargetEntities().stream().anyMatch(e -> e.equalsIgnoreCase("udp")));
    }

    @Test
    void testWorkedExample_BinarySearch() {
        TopicUnderstandingEngine.TopicUnderstandingResult result =
                topicEngine.understand("Explain binary search with a suitable example", 1);

        assertTrue(result.getNormalizedTopic().toLowerCase().contains("binary search"));
        assertEquals(DomainType.ALGORITHMS, result.getDomain());
        assertTrue(result.isRequiresWorkedExample(), "Must require worked example");
        assertTrue(result.isRequiresAlgorithm(), "Must require algorithm steps");
    }

    @Test
    void testAutomata_NfaAndDfa() {
        TopicUnderstandingEngine.TopicUnderstandingResult result =
                topicEngine.understand("Define NFA and DFA", 2);

        assertTrue(result.getNormalizedTopic().toLowerCase().contains("nfa") || result.getNormalizedTopic().toLowerCase().contains("automata"));
        assertEquals(DomainType.THEORY_OF_COMPUTATION, result.getDomain());
        assertTrue(result.isMultiEntityComparison(), "NFA and DFA are dual entities to contrast");
    }

    @Test
    void testScientificDomain_Photosynthesis() {
        TopicUnderstandingEngine.TopicUnderstandingResult result =
                topicEngine.understand("Explain photosynthesis and its stages", 2);

        assertTrue(result.getNormalizedTopic().toLowerCase().contains("photosynthesis"));
        assertEquals(DomainType.BIOLOGY, result.getDomain());
        assertFalse(result.isRequiresAlgorithm(), "Biology topic must not require CS algorithms");
    }

    @Test
    void testPhysicalLaw_NewtonsLaws() {
        TopicUnderstandingEngine.TopicUnderstandingResult result =
                topicEngine.understand("Explain Newton's laws with real-world examples", 2);

        assertTrue(result.getNormalizedTopic().toLowerCase().contains("newton"));
        assertEquals(DomainType.PHYSICS, result.getDomain());
        assertTrue(result.isRequiresWorkedExample(), "Must require real-world examples");
        assertFalse(result.isRequiresAlgorithm(), "Physics law must not require CS algorithms");
    }
}
