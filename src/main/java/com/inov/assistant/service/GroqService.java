package com.inov.assistant.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroqService implements LLMClient{

    private final RestClient restClient;

    @Value("${groq.api.key}")
    private String apiKey;
    
    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String model;

    @Value("${llm.temperature:0.7}")
    private double temperature;

    public String generate(String message) {

        Map<String, Object> body = Map.of(
            "model", model,
            "temperature", temperature,
            "messages", List.of(
                Map.of("role", "user", "content", message)
            )
        );

        String jsonResponse = restClient.post()
            .uri("/chat/completions")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .body(String.class);

        return extractContent(jsonResponse);
    }

    private String extractContent(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            return root
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
        } catch (Exception e) {
            log.error("Error parsing response", e);
            return "Erreur lors du traitement de la réponse.";
        }
    }
}
