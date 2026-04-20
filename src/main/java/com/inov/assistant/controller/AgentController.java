package com.inov.assistant.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inov.assistant.dto.ChatRequest;
import com.inov.assistant.dto.ChatResponse;
import com.inov.assistant.memory.SessionMemoryManager;
import com.inov.assistant.model.Session;
import com.inov.assistant.service.LLMService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agent")
@Tag(name = "Agent IA", description = "Endpoints pour l'assistant intelligent")
public class AgentController {
    
    private final LLMService llmService;
    
    private final SessionMemoryManager memoryManager;
    
    @PostMapping("/chat")
    @Operation(summary = "Point d'entrée principal de l'agent")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String sessionId = request.getSessionId();
        Session session = memoryManager.getOrCreateSession(sessionId);

        Map<String, Object> result = llmService.processMessage(sessionId, request.getMessage());

        Session updatedSession = memoryManager.getSession(sessionId);

        int turn = (updatedSession != null && updatedSession.getMessages() != null)
            ? updatedSession.getMessages().size() / 2
            : 0;
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setSessionId(session.getId());
        chatResponse.setResponse((String) result.get("response"));
        chatResponse.setToolUsed((String) result.get("toolUsed"));
        chatResponse.setTurn(turn);
        
        return ResponseEntity.ok(chatResponse);
    }
    
    @GetMapping("/session/{id}/history")
    @Operation(summary = "Historique complet d'une session")
    public ResponseEntity<Map<String, Object>> getSessionHistory(@PathVariable String id) {
        var session = memoryManager.getSession(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> history = new HashMap<>();
        history.put("session_id", id);
        history.put("messages", session.getMessages());
        history.put("created_at", session.getCreatedAt());
        history.put("last_activity", session.getLastActivityAt());
        
        return ResponseEntity.ok(history);
    }
}