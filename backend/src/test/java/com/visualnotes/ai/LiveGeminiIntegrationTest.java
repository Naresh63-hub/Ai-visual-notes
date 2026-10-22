package com.visualnotes.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visualnotes.ai.domain.*;
import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.SemanticDocumentDto;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class LiveGeminiIntegrationTest {

    private GeminiProvider geminiProvider;
    private String apiKey;

    @BeforeEach
    void setUp() {
        apiKey = System.getenv("GEMINI_API_KEY");
        String runLive = System.getProperty("runLiveTests");
        String testTarget = System.getProperty("test");
        boolean isExplicit = "true".equalsIgnoreCase(runLive)
                || (testTarget != null && testTarget.contains("LiveGeminiIntegrationTest"));

        Assumptions.assumeTrue(isExplicit,
                "Live Gemini tests are opt-in. Run with -DrunLiveTests=true or -Dtest=LiveGeminiIntegrationTest to execute.");
        Assumptions.assumeTrue(apiKey != null && !apiKey.isBlank() && !apiKey.equalsIgnoreCase("none"),
                "GEMINI_API_KEY is not available in environment; skipping live integration test.");

        ObjectMapper objectMapper = new ObjectMapper();
        TopicUnderstandingEngine topicEngine = new TopicUnderstandingEngine();
        ContentPlanningEngine planningEngine = new ContentPlanningEngine();
        DiagramEngine diagramEngine = new DiagramEngine();
        PagePlanningEngine pagePlanningEngine = new PagePlanningEngine();
        ContentQualityValidator validator = new ContentQualityValidator();
        ContentSynthesisEngine synthesisEngine = new ContentSynthesisEngine(diagramEngine);
        SemanticEngineAiProvider offlineProvider = new SemanticEngineAiProvider(
                topicEngine, planningEngine, synthesisEngine, validator, pagePlanningEngine
        );

        geminiProvider = new GeminiProvider(
                offlineProvider,
                topicEngine,
                planningEngine,
                pagePlanningEngine,
                validator,
                diagramEngine,
                objectMapper
        );

        ReflectionTestUtils.setField(geminiProvider, "apiKey", apiKey);
        ReflectionTestUtils.setField(geminiProvider, "modelsCsv", "gemini-3.8-flash");
    }

    @Test
    void testLiveGeneration_BinarySearch() {
        SemanticDocumentDto doc = geminiProvider.generateDocumentContent(
                "Binary Search",
                "Explain binary search with a suitable example",
                1,
                "Handwritten",
                "B.Tech / College",
                "Intermediate",
                null
        );

        assertNotNull(doc, "Generated document must not be null");
        assertNotNull(doc.getTopicTitle(), "Document topicTitle must not be null");
        assertTrue(doc.getTopicTitle().toLowerCase().contains("binary search"),
                "Document title should mention Binary Search: " + doc.getTopicTitle());

        assertNotNull(doc.getSections(), "Sections must not be null");
        assertFalse(doc.getSections().isEmpty(), "Document must have generated sections");

        // Validate that algorithm steps or example are present for Binary Search
        boolean hasAlgorithm = doc.getAlgorithm() != null && !doc.getAlgorithm().isEmpty();
        boolean hasExample = doc.getExample() != null && doc.getExample().getStepByStep() != null && !doc.getExample().getStepByStep().isEmpty();
        assertTrue(hasAlgorithm || hasExample, "Binary Search must contain either algorithm steps or a worked example");

        // Verify absence of generic filler boilerplate
        for (var sec : doc.getSections()) {
            String text = (sec.getTitle() + " " + sec.getContent()).toLowerCase();
            assertFalse(text.contains("governs state progression"), "Must not contain filler boilerplate");
            assertFalse(text.contains("streamlined method to solve specific computational"), "Must not contain filler boilerplate");
        }
    }
}
