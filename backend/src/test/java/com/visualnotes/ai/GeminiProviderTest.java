package com.visualnotes.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visualnotes.ai.domain.*;
import com.visualnotes.diagram.DiagramEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

class GeminiProviderTest {

    private GeminiProvider provider;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        provider = new GeminiProvider(
                mock(SemanticEngineAiProvider.class),
                mock(TopicUnderstandingEngine.class),
                mock(ContentPlanningEngine.class),
                mock(PagePlanningEngine.class),
                mock(ContentQualityValidator.class),
                mock(DiagramEngine.class),
                new ObjectMapper());

        RestTemplateHolder.bind(provider);
        server = RestTemplateHolder.server;
    }

    private void setKey(String key) {
        ReflectionTestUtils.setField(provider, "apiKey", key);
    }

    @Test
    void isConfigured_requiresNonEmptyKey() {
        setKey(null);
        assertFalse(provider.isConfigured());
        setKey("   ");
        assertFalse(provider.isConfigured());
        setKey("none");
        assertFalse(provider.isConfigured());
        setKey("AIza-real-key");
        assertTrue(provider.isConfigured());
    }

    @Test
    void callLlm_sendsGeminiRequestShape_andReturnsCandidateText() throws Exception {
        setKey("AIza-test-key");

        server.expect(requestTo(GeminiProvider.DEFAULT_ENDPOINT + "/gemini-3.8-flash:generateContent"))
                .andExpect(header("x-goog-api-key", "AIza-test-key"))
                .andExpect(jsonPath("$.systemInstruction.parts[0].text").value("SYSTEM PROMPT"))
                .andExpect(jsonPath("$.contents[0].role").value("user"))
                .andExpect(jsonPath("$.contents[0].parts[0].text").value("USER PROMPT"))
                .andExpect(jsonPath("$.generationConfig.responseMimeType").value("application/json"))
                .andRespond(withSuccess(
                        "{\"candidates\":[{\"content\":{\"role\":\"model\",\"parts\":[{\"text\":\"{\\\"mainTopic\\\":\\\"Network Topology\\\"}\"}]}}]}",
                        MediaType.APPLICATION_JSON));

        String reply = provider.callLlm("SYSTEM PROMPT", "USER PROMPT");

        assertEquals("{\"mainTopic\":\"Network Topology\"}", reply);
        server.verify();
    }

    @Test
    void callLlm_joinsMultipleTextParts() throws Exception {
        setKey("AIza-test-key");

        server.expect(requestTo(GeminiProvider.DEFAULT_ENDPOINT + "/gemini-3.8-flash:generateContent"))
                .andRespond(withSuccess(
                        "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"part-one \"},{\"text\":\"part-two\"}]}}]}",
                        MediaType.APPLICATION_JSON));

        assertEquals("part-one part-two", provider.callLlm("sys", "user"));
    }

    @Test
    void callLlm_throwsWhenNoCandidatesReturned() {
        setKey("AIza-test-key");

        server.expect(requestTo(GeminiProvider.DEFAULT_ENDPOINT + "/gemini-3.8-flash:generateContent"))
                .andRespond(withSuccess("{\"promptFeedback\":{\"blockReason\":\"SAFETY\"}}", MediaType.APPLICATION_JSON));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> provider.callLlm("sys", "user"));
        assertTrue(ex.getMessage().contains("no candidate text"));
    }

    @Test
    void callLlm_retriesOnTransient503_thenSucceeds() throws Exception {
        setKey("AIza-test-key");

        server.expect(requestTo(GeminiProvider.DEFAULT_ENDPOINT + "/gemini-3.8-flash:generateContent"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        server.expect(requestTo(GeminiProvider.DEFAULT_ENDPOINT + "/gemini-3.8-flash:generateContent"))
                .andRespond(withSuccess(
                        "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"ok\\\":true}\"}]}}]}",
                        MediaType.APPLICATION_JSON));

        assertEquals("{\"ok\":true}", provider.callLlm("sys", "user"));
        server.verify();
    }

    @Test
    void callLlm_failsOverToNextModelOnPersistent503() throws Exception {
        setKey("AIza-test-key");
        ReflectionTestUtils.setField(provider, "modelsCsv", "gemini-3.8-flash,gemini-3.8-flash-lite");

        // Both attempts on the first model fail transiently...
        server.expect(requestTo(GeminiProvider.DEFAULT_ENDPOINT + "/gemini-3.8-flash:generateContent"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        server.expect(requestTo(GeminiProvider.DEFAULT_ENDPOINT + "/gemini-3.8-flash:generateContent"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        // ...so the provider must fail over to the second model in the list.
        server.expect(requestTo(GeminiProvider.DEFAULT_ENDPOINT + "/gemini-3.8-flash-lite:generateContent"))
                .andRespond(withSuccess(
                        "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"ok\\\":true}\"}]}}]}",
                        MediaType.APPLICATION_JSON));

        assertEquals("{\"ok\":true}", provider.callLlm("sys", "user"));
        server.verify();
    }

    @Test
    void stripCodeFences_removesMarkdownFencing() throws Exception {
        assertEquals("{\"a\":1}", provider.stripCodeFences("```json\n{\"a\":1}\n```"));
        assertEquals("{\"a\":1}", provider.stripCodeFences("  {\"a\":1}  "));
        assertNull(provider.stripCodeFences(null));
    }

    /** Small helper so the private RestTemplate of the provider can be mocked. */
    static final class RestTemplateHolder {
        static MockRestServiceServer server;

        static void bind(GeminiProvider provider) {
            org.springframework.web.client.RestTemplate rt =
                    (org.springframework.web.client.RestTemplate) ReflectionTestUtils.getField(provider, "restTemplate");
            server = MockRestServiceServer.bindTo(rt).build();
        }
    }
}
