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
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiProvider extends LlmContentProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAiProvider.class);

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:gpt-4o}")
    private String model;

    private final RestTemplate restTemplate;

    public OpenAiProvider(
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
        factory.setReadTimeout(45000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public String getProviderName() {
        return "OpenAI";
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equalsIgnoreCase("none");
    }

    @Override
    protected String callLlm(String systemPrompt, String userPrompt) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("response_format", Map.of("type", "json_object"));
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));
        return callOpenAiChat(requestBody);
    }

    private String callOpenAiChat(Map<String, Object> requestBody) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity("https://api.openai.com/v1/chat/completions", entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List choices = (List) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map firstChoice = (Map) choices.get(0);
                Map message = (Map) firstChoice.get("message");
                return (String) message.get("content");
            }
        }
        throw new IllegalStateException("OpenAI API returned empty or non-200 response: " + response.getStatusCode());
    }
}
