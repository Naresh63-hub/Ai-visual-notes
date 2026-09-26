package com.visualnotes.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visualnotes.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class OpenAiProvider implements AIProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAiProvider.class);

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:gpt-4o}")
    private String model;

    private final SemanticEngineAiProvider semanticEngine;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public OpenAiProvider(SemanticEngineAiProvider semanticEngine, ObjectMapper objectMapper) {
        this.semanticEngine = semanticEngine;
        this.objectMapper = objectMapper;
        
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public String getProviderName() {
        return "OpenAI";
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty() && !apiKey.equalsIgnoreCase("none");
    }

    @Override
    public PromptAnalysisResponse analyzePrompt(String prompt, String preferredStyle, Integer requestedPageCount) {
        if (!isConfigured()) {
            return semanticEngine.analyzePrompt(prompt, preferredStyle, requestedPageCount);
        }
        try {
            return semanticEngine.analyzePrompt(prompt, preferredStyle, requestedPageCount);
        } catch (Exception e) {
            log.warn("OpenAI prompt analysis fallback to Semantic Engine: {}", e.getMessage());
            return semanticEngine.analyzePrompt(prompt, preferredStyle, requestedPageCount);
        }
    }

    @Override
    public PagePlanDto planPages(String prompt, int pageCount, String audience, String style, String difficulty) {
        if (!isConfigured()) {
            return semanticEngine.planPages(prompt, pageCount, audience, style, difficulty);
        }
        try {
            return semanticEngine.planPages(prompt, pageCount, audience, style, difficulty);
        } catch (Exception e) {
            log.warn("OpenAI page planning fallback to Semantic Engine: {}", e.getMessage());
            return semanticEngine.planPages(prompt, pageCount, audience, style, difficulty);
        }
    }

    @Override
    public PageContentDto generatePageContent(
            String topic,
            String overallPrompt,
            int pageNumber,
            int totalPages,
            String style,
            String audience,
            String difficulty,
            String focusArea,
            String plannedDiagramType) {

        if (!isConfigured()) {
            return semanticEngine.generatePageContent(
                    topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType
            );
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("response_format", Map.of("type", "json_object"));

            String systemPrompt = "You are an expert pedagogical study-notes generator. " +
                    "Generate clean, highly relevant JSON study notes for the topic: '" + topic + "'. " +
                    "Audience: " + audience + ", Difficulty: " + difficulty + ". " +
                    "Only include educational components genuinely relevant to this topic. " +
                    "Possible fields: topicTitle, definition, simpleExplanation, sections (title, content, bulletPoints), " +
                    "comparisonTable (title, headers, rows, conclusion - for comparisons), formula (title, latex, explanation, variables), " +
                    "algorithm (title, steps), pseudocode, example (title, problem, stepByStepSolution, result), " +
                    "keyPoints (point, whyImportant), examTips (tip, commonMistake, mnemonic). " +
                    "Do NOT include irrelevant components (e.g. no algorithms for pure physics laws, no formulas if purely conceptual, no comparison table if single topic).";

            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", "Generate page " + pageNumber + " of " + totalPages + " for " + topic + " focusing on: " + focusArea)
            ));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity("https://api.openai.com/v1/chat/completions", entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List choices = (List) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map firstChoice = (Map) choices.get(0);
                    Map message = (Map) firstChoice.get("message");
                    String contentStr = (String) message.get("content");
                    PageContentDto parsed = objectMapper.readValue(contentStr, PageContentDto.class);
                    parsed.setPageNumber(pageNumber);
                    parsed.setTotalPages(totalPages);
                    parsed.setDocumentTitle(topic);
                    parsed.setStyleTheme(style);
                    if (parsed.getDiagram() == null) {
                        parsed.setDiagram(semanticEngine.generatePageContent(topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType).getDiagram());
                    }
                    return parsed;
                }
            }
        } catch (Exception e) {
            log.warn("OpenAI API call failed or timed out: {}. Using Semantic Engine fallback.", e.getMessage());
        }

        return semanticEngine.generatePageContent(
                topic, overallPrompt, pageNumber, totalPages, style, audience, difficulty, focusArea, plannedDiagramType
        );
    }

    @Override
    public PageContentDto regeneratePage(PageContentDto currentContent, String instruction, String customModifier, String style) {
        return semanticEngine.regeneratePage(currentContent, instruction, customModifier, style);
    }
}
