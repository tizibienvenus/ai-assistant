package com.inov.assistant.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    
    @Autowired(required = false)
    private MongoTemplate mongoTemplate;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("database", mongoTemplate != null ? "CONNECTED" : "DISCONNECTED");
        status.put("service", "Assistant IA - Inov Consulting");
        
        return ResponseEntity.ok(status);
    }
}