package com.inov.assistant.controller;

import com.inov.assistant.dto.ChatRequest;
import com.inov.assistant.dto.ChatResponse;
import com.inov.assistant.memory.SessionMemoryManager;
import com.inov.assistant.model.Session;
import com.inov.assistant.service.LLMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
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

        
        String response = llmService.processMessage(sessionId, request.getMessage());
        
        int turn = memoryManager.getSession(sessionId).getMessages().size() / 2;
        
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setSessionId(sessionId);
        chatResponse.setResponse(response);
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