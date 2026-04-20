package com.inov.assistant.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.inov.assistant.memory.SessionMemoryManager;
import com.inov.assistant.model.Event;
import com.inov.assistant.model.Message;
import com.inov.assistant.tool.AgendaTool;
import com.inov.assistant.tool.DocumentSynthesisTool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LLMService {
    private static final int MAX_CONVERSATION_TURNS = 5;
    
    private final GroqService groqService;
    
    private final SessionMemoryManager memoryManager;
    
    private final AgendaTool agendaTool;
    
    private final DocumentSynthesisTool synthesisTool;

    public Map<String, Object> processMessage(String sessionId, String userMessage) {
        // Récupérer l'historique de conversation
        List<Message> history = memoryManager.getConversationHistory(sessionId, MAX_CONVERSATION_TURNS);
        
        // Construire le prompt avec contexte
        String prompt = buildPromptWithContext(history, userMessage);
        
        // Déterminer quel tool utiliser
        String toolToUse = determineTool(userMessage);
        
        String response;
        String toolUsed = null;
        
        if (toolToUse != null) {
            toolUsed = toolToUse;
            response = executeTool(toolToUse, userMessage);
        } else {
            response = generateResponse(prompt);
        }
        
        // Sauvegarder la conversation
        memoryManager.addMessage(sessionId, "user", userMessage, null);
        memoryManager.addMessage(sessionId, "assistant", response, toolUsed);
        
        Map<String, Object> result = new HashMap<>();
        result.put("response", response);
        result.put("toolUsed", toolUsed);

        return result;
    }
    
    private String determineTool(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        // Détection des requêtes agenda
        if (lowerMessage.contains("rendez-vous") || 
            lowerMessage.contains("agenda") ||
            lowerMessage.contains("planifie") ||
            lowerMessage.contains("réunion") ||
            lowerMessage.contains("calendrier") ||
            lowerMessage.matches(".*(demain|aujourd'hui|cette semaine).*")) {
            return "agenda";
        }
        
        // Détection des requêtes de synthèse
        if (lowerMessage.contains("synthèse") || 
            lowerMessage.contains("résume") ||
            lowerMessage.contains("compte-rendu") ||
            lowerMessage.length() > 500) { // Document long
            return "synthesis";
        }
        
        return null;
    }
    
    private String executeTool(String toolName, String userMessage) {
        if ("agenda".equals(toolName)) {
            Map<String, Object> parameters = extractAgendaParameters(userMessage);
            Map<String, Object> result = agendaTool.execute("get_events", parameters);
            return formatAgendaResponse(result);
        } else if ("synthesis".equals(toolName)) {
            Map<String, Object> result = synthesisTool.execute(userMessage);
            return (String) result.get("synthesis");
        }
        
        return generateResponse(userMessage);
    }
    
    private Map<String, Object> extractAgendaParameters(String userMessage) {
        Map<String, Object> params = new HashMap<>();
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("demain")) {
            params.put("date", java.time.LocalDate.now().plusDays(1).toString());
        } else if (lowerMessage.contains("cette semaine")) {
            params.put("range", "week");
        }
        
        return params;
    }
    
    private String formatAgendaResponse(Map<String, Object> result) {
        if (!"success".equals(result.get("status"))) {
            return "Je n'ai pas pu accéder à votre agenda. Veuillez réessayer.";
        }

        @SuppressWarnings("unchecked")
        List<Event> events = (List<Event>) result.get("events");

        if (events == null || events.isEmpty()) {
            return "Vous n'avez aucun rendez-vous.";
        }

        StringBuilder response = new StringBuilder();

        response.append("Voici vos rendez-vous :\n\n");

        for (Event e : events) {
            response.append("- ")
                    .append(e.getTitle())
                    .append(" le ")
                    .append(e.getDateTime().toLocalDate())
                    .append(" à ")
                    .append(e.getDateTime().toLocalTime())
                    .append(" avec ")
                    .append(String.join(", ", e.getParticipants()))
                    .append("\n");
        }

        return response.toString();
    }
    
    private String buildPromptWithContext(List<Message> history, String currentMessage) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Tu es un assistant de direction intelligent. Voici l'historique de la conversation :\n\n");
        
        for (Message msg : history) {
            prompt.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
        }
        
        prompt.append("\nRéponds à ce message de l'utilisateur : ").append(currentMessage);
        prompt.append("\n\nRéponds de manière naturelle, professionnelle et utile.");
        
        return prompt.toString();
    }
    
    public String generateResponse(String prompt) {
        try {
            return groqService.generate(prompt);
        } catch (Exception e) {
            log.error("Error calling Groq API", e);
            return "Désolé, je rencontre une difficulté technique. Veuillez réessayer.";
        }
    }

}