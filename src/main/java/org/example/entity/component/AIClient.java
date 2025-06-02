package org.example.entity.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ai.api.endpoint}")
    private String aiApiEndpoint;

    @Value("${ai.api.key}")
    private String aiApiKey;

    public AIClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String generateTestCases(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", "deepseek-ai/DeepSeek-V3");

            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            requestMap.put("messages", List.of(message));

            String requestBody = objectMapper.writeValueAsString(requestMap);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    aiApiEndpoint, HttpMethod.POST, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("AI API error: " + response.getBody());
            }

            Map<String, Object> responseMap = objectMapper.readValue(
                    response.getBody(), new TypeReference<Map<String, Object>>() {});

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");

            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("AI returned no results");
            }

            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> messageContent = (Map<String, Object>) firstChoice.get("message");
            return (String) messageContent.get("content");

        } catch (Exception e) {
            throw new RuntimeException("Failed to call AI API", e);
        }
    }
}