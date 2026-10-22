package com.visualnotes.ai;

import com.visualnotes.dto.PromptAnalysisResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AiServiceTest {

    private OpenAiProvider openAiProvider;
    private GeminiProvider geminiProvider;
    private SemanticEngineAiProvider semanticEngine;
    private AiService aiService;

    @BeforeEach
    void setUp() {
        openAiProvider = mock(OpenAiProvider.class);
        geminiProvider = mock(GeminiProvider.class);
        semanticEngine = mock(SemanticEngineAiProvider.class);
        when(openAiProvider.getProviderName()).thenReturn("OpenAI");
        when(geminiProvider.getProviderName()).thenReturn("Gemini");
        when(semanticEngine.getProviderName()).thenReturn("SemanticEngine");
        aiService = new AiService(openAiProvider, geminiProvider, semanticEngine);
    }

    private void configure(String provider) {
        ReflectionTestUtils.setField(aiService, "configuredProvider", provider);
    }

    private PromptAnalysisResponse stubbedResponse() {
        return PromptAnalysisResponse.builder()
                .mainSubject("Network Topology")
                .detectedTopics(List.of("Network Topology"))
                .suggestedPageCount(1)
                .build();
    }

    @Test
    void usesGeminiWhenConfigured() {
        configure("gemini");
        when(geminiProvider.isConfigured()).thenReturn(true);
        when(geminiProvider.analyzePrompt(any(), any(), any())).thenReturn(stubbedResponse());

        PromptAnalysisResponse response = aiService.analyzePrompt("network topology", null, 1);

        assertEquals("Network Topology", response.getMainSubject());
        verify(geminiProvider).analyzePrompt("network topology", null, 1);
        verifyNoInteractions(openAiProvider, semanticEngine);
    }

    @Test
    void fallsBackToSemanticWhenGeminiKeyMissing() {
        configure("gemini");
        when(geminiProvider.isConfigured()).thenReturn(false);
        when(semanticEngine.analyzePrompt(any(), any(), any())).thenReturn(stubbedResponse());

        aiService.analyzePrompt("network topology", null, 1);

        verify(semanticEngine).analyzePrompt("network topology", null, 1);
        verify(geminiProvider, never()).analyzePrompt(any(), any(), any());
    }

    @Test
    void usesOpenAiWhenConfigured() {
        configure("openai");
        when(openAiProvider.isConfigured()).thenReturn(true);
        when(openAiProvider.analyzePrompt(any(), any(), any())).thenReturn(stubbedResponse());

        aiService.analyzePrompt("network topology", null, 1);

        verify(openAiProvider).analyzePrompt("network topology", null, 1);
        verifyNoInteractions(geminiProvider, semanticEngine);
    }

    @Test
    void fallsBackToSemanticWhenOpenAiKeyMissing() {
        configure("openai");
        when(openAiProvider.isConfigured()).thenReturn(false);
        when(semanticEngine.analyzePrompt(any(), any(), any())).thenReturn(stubbedResponse());

        aiService.analyzePrompt("network topology", null, 1);

        verify(semanticEngine).analyzePrompt("network topology", null, 1);
    }

    @Test
    void unknownProviderNameFallsBackToSemantic() {
        configure("some-unknown-llm");
        when(semanticEngine.analyzePrompt(any(), any(), any())).thenReturn(stubbedResponse());

        aiService.analyzePrompt("network topology", null, 1);

        verify(semanticEngine).analyzePrompt("network topology", null, 1);
        verifyNoInteractions(openAiProvider, geminiProvider);
    }
}
