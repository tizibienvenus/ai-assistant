package com.inov.assistant.tool;

import com.inov.assistant.service.LLMClient;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentSynthesisTool {

    private final LLMClient llmClient;
    
    public Map<String, Object> execute(String content) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String synthesis = generateSynthesis(content);
            result.put("synthesis", synthesis);
            result.put("status", "success");
        } catch (Exception e) {
            log.error("Error generating synthesis", e);
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        
        return result;
    }
    
    private String generateSynthesis(String content) {
        String prompt = """
        Tu es un assistant expert en synthèse de documents. Voici un document à synthétiser :
        
        %s
        
        Génère une synthèse structurée avec :
        1. 3 à 5 points clés
        2. Décisions prises (si applicable)
        3. Actions à suivre avec responsables (si mentionnés)
        
        Format de réponse : texte clair et structuré avec des titres.
        """.formatted(content);
        
        return llmClient.generate(prompt);
    }
    
    public String getToolDefinition() {
        return """
        {
            "name": "document_synthesizer",
            "description": "Synthétise des documents textes (comptes-rendus, rapports, emails) en extrayant les points clés, décisions et actions",
            "parameters": {
                "type": "object",
                "properties": {
                    "content": {
                        "type": "string",
                        "description": "Le contenu du document à synthétiser"
                    }
                },
                "required": ["content"]
            }
        }
        """;
    }
}