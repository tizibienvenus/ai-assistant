package com.inov.assistant.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroqService {

    private final RestClient restClient;

    @Value("${groq.api.key}")
    private String apiKey;
    
    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String model;

    public String chat(String message, double temperature) {

        Map<String, Object> body = Map.of(
            "model", model,
            "temperature", temperature,
            "messages", List.of(
                Map.of("role", "user", "content", message)
            )
        );

        return restClient.post()
            .uri("/chat/completions")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .body(String.class);
    }
}
