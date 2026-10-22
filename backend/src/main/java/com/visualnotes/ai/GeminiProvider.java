package com.visualnotes.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visualnotes.ai.domain.*;
import com.visualnotes.diagram.DiagramEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Google Gemini (Generative Language API) transport for the shared 3-step
 * LLM pipeline in {@link LlmContentProvider}.
 *
 * Endpoint: POST {ai.gemini-endpoint}/{model}:generateContent
 * Auth:     x-goog-api-key header
 * JSON:     generationConfig.responseMimeType = application/json
 *           (the model reply is text at candidates[0].content.parts[*].text)
 */
@Component
public class GeminiProvider extends LlmContentProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiProvider.class);

    static final String DEFAULT_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models";

    @Value("${ai.gemini-api-key:}")
    private String apiKey;

    /** Ordered failover list: Gemini 503 spikes are per-model, so try the next model when one is overloaded. */
    @Value("${ai.gemini-models:gemini-3.8-flash}")
    private String modelsCsv = "gemini-3.8-flash";

    @Value("${ai.gemini-endpoint:" + DEFAULT_ENDPOINT + "}")
    private String endpoint = DEFAULT_ENDPOINT;

    private final RestTemplate restTemplate;

    /** Transient backend errors are worth retrying within one model (Google returns 503 under demand spikes, 429 on rate limits). */
    private static final int MAX_ATTEMPTS_PER_MODEL = 2;
    private static final long[] BACKOFF_MS = {0L, 1500L};

    public GeminiProvider(
            SemanticEngineAiProvider semanticEngine,
            TopicUnderstandingEngine topicUnderstandingEngine,
            ContentPlanningEngine contentPlanningEngine,
            PagePlanningEngine pagePlanningEngine,
            ContentQualityValidator contentQualityValidator,
            DiagramEngine diagramEngine,
            ObjectMapper objectMapper) {
        super(semanticEngine, topicUnderstandingEngine, contentPlanningEngine,
                pagePlanningEngine, contentQualityValidator, diagramEngine, objectMapper);

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(8000);
        factory.setReadTimeout(60000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public String getProviderName() {
        return "Gemini";
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty() && !apiKey.trim().equalsIgnoreCase("none");
    }

    @Override
    protected String callLlm(String systemPrompt, String userPrompt) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))));
        requestBody.put("contents", List.of(
                Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))
        ));
        // Strict JSON output, same contract as OpenAI's response_format: json_object
        requestBody.put("generationConfig", Map.of("responseMimeType", "application/json"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        RuntimeException lastError = null;
        for (String model : modelList()) {
            String url = endpoint + "/" + model + ":generateContent";

            for (int attempt = 1; attempt <= MAX_ATTEMPTS_PER_MODEL; attempt++) {
                if (BACKOFF_MS[attempt - 1] > 0) {
                    try {
                        Thread.sleep(BACKOFF_MS[attempt - 1]);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Interrupted while waiting to retry Gemini call", ie);
                    }
                }
                try {
                    ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
                    String text = extractCandidateText(response.getBody());
                    if (text == null || text.isBlank()) {
                        throw new IllegalStateException("Gemini API returned no candidate text (blocked or empty) from model " + model + ". Body: " + response.getBody());
                    }
                    return text;
                } catch (RestClientResponseException e) {
                    // 4xx (429 rate limit, 400 API_KEY_INVALID) and 5xx (503 demand spikes) both land here
                    if (!isTransient(e)) {
                        // Non-transient (bad key, disabled model, bad request): failover will not help
                        throw new IllegalStateException("Gemini API call failed [" + e.getStatusCode() + "]: " + e.getResponseBodyAsString(), e);
                    }
                    lastError = e;
                    log.warn("[GEMINI] Transient {} from model {} (attempt {}/{})", e.getStatusCode(), model, attempt, MAX_ATTEMPTS_PER_MODEL);
                } catch (org.springframework.web.client.RestClientException e) {
                    lastError = e;
                    log.warn("[GEMINI] Network error on model {} (attempt {}/{}): {}", model, attempt, MAX_ATTEMPTS_PER_MODEL, e.getMessage());
                }
            }
            log.warn("[GEMINI] Model {} kept failing transiently - trying next model in failover list", model);
        }
        throw new IllegalStateException("Gemini API call failed for all models " + modelList() + ": "
                + (lastError != null ? lastError.getMessage() : "no error recorded"), lastError);
    }

    private List<String> modelList() {
        return Arrays.stream(modelsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /** 503 UNAVAILABLE (demand spikes), 429 RESOURCE_EXHAUSTED (rate limits) and 5xx are transient. */
    private boolean isTransient(RestClientResponseException e) {
        int code = e.getStatusCode().value();
        return code == 429 || code == 500 || code == 502 || code == 503;
    }

    /**
     * Walks candidates[0].content.parts[*].text (a response can split text
     * across several parts) and joins them.
     */
    @SuppressWarnings("unchecked")
    private String extractCandidateText(Map<String, Object> body) {
        Object candidatesObj = body.get("candidates");
        if (!(candidatesObj instanceof List<?> candidates) || candidates.isEmpty()) {
            return null;
        }
        if (!(candidates.get(0) instanceof Map<?, ?> first)) {
            return null;
        }
        if (!(first.get("content") instanceof Map<?, ?> content)) {
            return null;
        }
        if (!(content.get("parts") instanceof List<?> parts)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Object partObj : parts) {
            if (partObj instanceof Map<?, ?> part) {
                // Skip internal reasoning/thought tokens if present
                Object isThought = part.get("thought");
                if (Boolean.TRUE.equals(isThought)) {
                    continue;
                }
                if (part.get("text") instanceof String text) {
                    sb.append(text);
                }
            }
        }
        return sb.toString();
    }
}
