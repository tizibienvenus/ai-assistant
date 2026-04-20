package com.inov.assistant.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RestClientConfig {
    
    @Value("${groq.api.url:https://api.groq.com/openai/v1}")
    private String groqApiUrl;
    
    @Bean
    public RestClient groqRestClient() {
        log.info("Configuring Groq RestClient with URL: {}", groqApiUrl);
        
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
        
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(60));
        
        return RestClient.builder()
            .baseUrl(groqApiUrl)
            .requestFactory(requestFactory)
            .build();
    }
}