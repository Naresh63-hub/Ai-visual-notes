package com.visualnotes.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visualnotes.ai.domain.*;
import com.visualnotes.diagram.DiagramEngine;
import com.visualnotes.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProviderFailureTest {

    private ContentQualityValidator validator;
    private GeminiProvider geminiProvider;

    @BeforeEach
    void setUp() {
        validator = new ContentQualityValidator();
        geminiProvider = new GeminiProvider(
                mock(SemanticEngineAiProvider.class),
                mock(TopicUnderstandingEngine.class),
                mock(ContentPlanningEngine.class),
                mock(PagePlanningEngine.class),
                validator,
                mock(DiagramEngine.class),
                new ObjectMapper()
        );
    }

    @Test
    void testMissingApiKey_throwsIllegalState() {
        ReflectionTestUtils.setField(geminiProvider, "apiKey", "");
        assertFalse(geminiProvider.isConfigured());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            geminiProvider.generateDocumentContent("Binary Search", "Explain binary search", 1, "Handwritten", "Students", "Standard", null);
        });
        assertTrue(ex.getMessage().contains("not configured"));
    }

    @Test
    void testMalformedJson_stripCodeFencesAndFailsGracefully() {
        ReflectionTestUtils.setField(geminiProvider, "apiKey", "test-key");

        // Test stripCodeFences handles markdown wrapper
        String markdownJson = "```json\n{\"mainTopic\": \"OSI Model\"}\n```";
        String stripped = geminiProvider.stripCodeFences(markdownJson);
        assertEquals("{\"mainTopic\": \"OSI Model\"}", stripped);

        // Test malformed JSON throws exception instead of silently fabricating
        assertThrows(Exception.class, () -> {
            geminiProvider.parseJson("NOT_A_VALID_JSON_STRING", SemanticAnalysisDto.class);
        });
    }

    @Test
    void testValidator_UnbalancedFormulaBracesPurged() {
        SemanticDocumentDto doc = new SemanticDocumentDto();
        doc.setTopicTitle("Quadratic Equation");
        FormulaDto formula = new FormulaDto();
        formula.setLatex("x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}{2a"); // Missing closing brace
        doc.setFormula(formula);

        SemanticAnalysisDto analysis = new SemanticAnalysisDto();
        analysis.setMainTopic("Quadratic Equation");
        analysis.setDomain("MATHEMATICS");

        validator.validateAndSanitize(doc, analysis);
        assertNull(doc.getFormula(), "Malformed formula with unbalanced braces must be deterministically purged");
    }

    @Test
    void testValidator_UnrecognizedDiagramTypeNormalized() {
        SemanticDocumentDto doc = new SemanticDocumentDto();
        doc.setTopicTitle("Network Architecture");
        DiagramDataDto diag = new DiagramDataDto();
        diag.setType("some-hallucinated-crazy-diagram");
        doc.setDiagram(diag);

        SemanticAnalysisDto analysis = new SemanticAnalysisDto();
        analysis.setMainTopic("Network Architecture");
        analysis.setDomain("COMPUTER_NETWORKS");

        validator.validateAndSanitize(doc, analysis);
        assertNotNull(doc.getDiagram());
        assertEquals("concept-map", doc.getDiagram().getType(), "Unrecognized diagram type must normalize to concept-map");
    }

    @Test
    void testValidator_PurgesGenericPlaceholderSentences() {
        SemanticDocumentDto doc = new SemanticDocumentDto();
        doc.setTopicTitle("Computer Networks");
        SectionDto s = new SectionDto();
        s.setHeading("Mechanics");
        s.setContent("This mechanism governs state progression and ensures consistency.");
        doc.setSections(List.of(s));

        SemanticAnalysisDto analysis = new SemanticAnalysisDto();
        analysis.setMainTopic("Computer Networks");
        analysis.setDomain("COMPUTER_NETWORKS");

        validator.validateAndSanitize(doc, analysis);
        assertNull(doc.getSections(), "Section containing placeholder 'governs state progression' must be purged");
    }
}
