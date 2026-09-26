package com.visualnotes.service;

import com.visualnotes.ai.domain.DomainType;
import com.visualnotes.ai.domain.PromptUnderstandingEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PromptUnderstandingEngineTest {

    private PromptUnderstandingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new PromptUnderstandingEngine();
    }

    @Test
    void testAlgorithmDomainClassification() {
        PromptUnderstandingEngine.PromptAnalysisResult response = engine.analyze(
                "Explain Binary Search with algorithm and complexity", "Handwritten", null);
        assertNotNull(response);
        assertEquals(DomainType.ALGORITHMS, response.getDomain());
        assertTrue(response.getDetectedTopics().size() >= 1);
    }

    @Test
    void testPhysicsDomainClassification() {
        PromptUnderstandingEngine.PromptAnalysisResult response = engine.analyze(
                "Newton's 3 Laws of Motion with physical vector equations and everyday examples", "Exam Notes", null);
        assertNotNull(response);
        assertEquals(DomainType.PHYSICS, response.getDomain());
    }

    @Test
    void testBiologyDomainClassification() {
        PromptUnderstandingEngine.PromptAnalysisResult response = engine.analyze(
                "Explain photosynthesis light and dark reactions with labeled diagram", "Clean Digital", null);
        assertNotNull(response);
        assertEquals(DomainType.BIOLOGY, response.getDomain());
    }

    @Test
    void testDatabaseDomainClassification() {
        PromptUnderstandingEngine.PromptAnalysisResult response = engine.analyze(
                "Explain normalization in DBMS: 1NF, 2NF, 3NF, BCNF with table anomalies", "Handwritten", null);
        assertNotNull(response);
        assertEquals(DomainType.DBMS, response.getDomain());
        assertTrue(response.getDetectedTopics().size() >= 1);
    }

    @Test
    void testNetworkingDomainClassification() {
        PromptUnderstandingEngine.PromptAnalysisResult response = engine.analyze(
                "Explain OSI 7 Layer Architecture with packet encapsulation", "Handwritten", null);
        assertNotNull(response);
        assertEquals(DomainType.COMPUTER_NETWORKS, response.getDomain());
    }

    @Test
    void testMultiUnitSyllabusDecomposition() {
        String syllabus = "Unit 1: Introduction to Algorithms, Asymptotic Notation. Unit 2: Divide and Conquer, Merge Sort, Quick Sort. Unit 3: Dynamic Programming, 0/1 Knapsack.";
        PromptUnderstandingEngine.PromptAnalysisResult response = engine.analyze(syllabus, "Exam Notes", null);
        assertNotNull(response);
        assertTrue(response.getDetectedTopics().size() >= 3);
        assertTrue(response.getSuggestedPageCount() >= 3);
    }

    @Test
    void testExplicitPageCountExtraction() {
        PromptUnderstandingEngine.PromptAnalysisResult response = engine.analyze(
                "Explain Quick Sort in 4 pages with complete trace", "Handwritten", null);
        assertNotNull(response);
        assertEquals(4, response.getRequestedPageCount());
    }

    @Test
    void testAutomataDomainClassification() {
        PromptUnderstandingEngine.PromptAnalysisResult response = engine.analyze(
                "Define NFA and DFA with formal 5-tuple and state transition diagram", "Handwritten", null);
        assertNotNull(response);
        assertEquals(DomainType.THEORY_OF_COMPUTATION, response.getDomain());
    }

    @Test
    void testOperatingSystemsDomainClassification() {
        PromptUnderstandingEngine.PromptAnalysisResult response = engine.analyze(
                "Explain CPU Scheduling Algorithms FCFS, SJF, Round Robin with Gantt Chart", "Handwritten", null);
        assertNotNull(response);
        assertEquals(DomainType.OPERATING_SYSTEMS, response.getDomain());
    }

    @Test
    void testElectronicsDomainClassification() {
        PromptUnderstandingEngine.PromptAnalysisResult response = engine.analyze(
                "Explain Ohm's Law with circuit diagram and IV characteristics", "Handwritten", null);
        assertNotNull(response);
        assertEquals(DomainType.ELECTRONICS, response.getDomain());
    }
}
