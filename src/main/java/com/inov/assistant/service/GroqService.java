package com.inov.assistant.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroqService implements LLMClient {

    @Qualifier("groqRestClient")
    private final RestClient restClient;  // Use the specific bean

    @Value("${groq.api.key}")
    private String apiKey;
    
    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String model;

    @Value("${llm.temperature:0.7}")
    private double temperature;

    @Value("${llm.max-tokens:1000}")
    private int maxTokens;

    @Override
    public String generate(String message) {
        log.debug("Calling Groq API with model: {}", model);
        log.debug("API Key present: {}", apiKey != null && !apiKey.isEmpty() && !apiKey.equals("gsk_vRbl5pEjVVYRYKvelDZbywPJG4d8UCd3qGP9X"));
        
        // Validate API key
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("gsk_vRbl5pEjVVYRYKvelDZbywPJG4d8UCd3qGP9X")) {
            log.error("Invalid or missing Groq API key");
            return "Configuration error: Groq API key is not properly configured.";
        }

        Map<String, Object> body = Map.of(
            "model", model,
            "temperature", temperature,
            "max_tokens", maxTokens,
            "messages", List.of(
                Map.of("role", "user", "content", message)
            )
        );

        try {
            String jsonResponse = restClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

            log.debug("Received response from Groq API");
            return extractContent(jsonResponse);
        } catch (Exception e) {
            log.error("Error calling Groq API: {}", e.getMessage(), e);
            return "Désolé, je rencontre une difficulté technique avec l'API Groq. Veuillez réessayer.";
        }
    }

    private String extractContent(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0)
                    .path("message")
                    .path("content")
                    .asText();
            }
            
            log.error("Unexpected response format: {}", jsonResponse);
            return "Je n'ai pas pu comprendre la réponse de l'API.";
        } catch (JsonProcessingException e) {
            log.error("Error parsing response: {}", e.getMessage());
            log.debug("Raw response: {}", jsonResponse);
            return "Erreur lors du traitement de la réponse.";
        }
    }
}