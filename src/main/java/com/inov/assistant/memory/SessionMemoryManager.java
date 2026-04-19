// memory/SessionMemoryManager.java
package com.inov.assistant.memory;

import com.inov.assistant.model.Message;
import com.inov.assistant.model.Session;
import com.inov.assistant.repository.SessionRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class SessionMemoryManager {
    private static final int MAX_MESSAGES = 10; // Garder 5 échanges (10 messages)
    
    @Autowired
    private SessionRepository sessionRepository;
    
    public String createSession() {
        Session session = new Session();
        session.setId(UUID.randomUUID().toString());
        sessionRepository.save(session);
        log.info("Created new session: {}", session.getId());
        return session.getId();
    }
    
    public Session getOrCreateSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            return sessionRepository.findById(sessionId)
                    .orElseGet(() -> {
                        log.warn("Session {} not found, creating new one", sessionId);
                        return createAndSaveSession();
                    });
        }
        return createAndSaveSession();
    }
    
    private Session createAndSaveSession() {
        Session session = new Session();
        session.setId(UUID.randomUUID().toString());
        return sessionRepository.save(session);
    }
    
    public void addMessage(String sessionId, String role, String content, String toolUsed) {
        Session session = getOrCreateSession(sessionId);
        
        Message message = new Message();
        message.setRole(role);
        message.setContent(content);
        message.setToolUsed(toolUsed);
        message.setTimestamp(LocalDateTime.now());
        
        session.getMessages().add(message);
        
        // Garder seulement les derniers messages
        if (session.getMessages().size() > MAX_MESSAGES) {
            session.getMessages().subList(0, session.getMessages().size() - MAX_MESSAGES).clear();
        }
        
        session.setLastActivityAt(LocalDateTime.now());
        sessionRepository.save(session);
    }
    
    public List<Message> getConversationHistory(String sessionId, int turns) {
        Session session = getOrCreateSession(sessionId);
        List<Message> messages = session.getMessages();
        int totalMessages = turns * 2; // user + assistant par turn
        if (messages.size() > totalMessages) {
            return messages.subList(messages.size() - totalMessages, messages.size());
        }
        return messages;
    }
    
    public Session getSession(String sessionId) {
        return sessionRepository.findById(sessionId).orElse(null);
    }
}