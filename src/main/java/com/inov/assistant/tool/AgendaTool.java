// tool/AgendaTool.java
package com.inov.assistant.tool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.inov.assistant.dto.EventRequest;
import com.inov.assistant.model.Event;
import com.inov.assistant.service.AgendaService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaTool {
    private static final Logger logger = LoggerFactory.getLogger(AgendaTool.class);

    private final AgendaService agendaService;
    
    public Map<String, Object> execute(String action, Map<String, Object> parameters) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            switch (action) {
                case "get_events":
                    result.put("events", getEvents(parameters));
                    result.put("status", "success");
                    break;
                case "create_event":
                    result.put("event", createEvent(parameters));
                    result.put("status", "success");
                    break;
                default:
                    result.put("status", "error");
                    result.put("message", "Unknown action: " + action);
            }
        } catch (Exception e) {
            logger.error("Error executing agenda tool", e);
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        
        return result;
    }
    
    private List<Event> getEvents(Map<String, Object> parameters) {
        String dateFilter = (String) parameters.get("date");
        String range = (String) parameters.get("range");
        
        if (range != null && range.equals("week")) {
            LocalDateTime start = LocalDate.now().atStartOfDay();
            LocalDateTime end = LocalDate.now().plusDays(7).atStartOfDay();
            return agendaService.getEventsByDateRange(start, end);
        } else if (dateFilter != null) {
            LocalDate date = LocalDate.parse(dateFilter);
            return agendaService.getEventsByDate(date);
        }
        
        return agendaService.getAllEvents();
    }
    
    private Event createEvent(Map<String, Object> parameters) {
        EventRequest request = new EventRequest();
        request.setTitle((String) parameters.get("title"));
        request.setDateTime(LocalDateTime.parse((String) parameters.get("dateTime")));
        request.setParticipants((List<String>) parameters.get("participants"));
        request.setNotes((String) parameters.get("notes"));
        
        return agendaService.createEvent(request);
    }
    
    public String getToolDefinition() {
        return """
        {
            "name": "agenda_manager",
            "description": "Gère l'agenda du directeur. Permet de consulter et créer des rendez-vous",
            "parameters": {
                "type": "object",
                "properties": {
                    "action": {
                        "type": "string",
                        "enum": ["get_events", "create_event"],
                        "description": "L'action à effectuer sur l'agenda"
                    },
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "date": {
                                "type": "string",
                                "description": "Date au format YYYY-MM-DD pour filtrer les événements"
                            },
                            "range": {
                                "type": "string",
                                "enum": ["week", "month"],
                                "description": "Plage de dates"
                            },
                            "title": {
                                "type": "string",
                                "description": "Titre de l'événement à créer"
                            },
                            "dateTime": {
                                "type": "string",
                                "description": "Date et heure de l'événement au format ISO"
                            },
                            "participants": {
                                "type": "array",
                                "items": {"type": "string"},
                                "description": "Liste des participants"
                            },
                            "notes": {
                                "type": "string",
                                "description": "Notes ou description de l'événement"
                            }
                        }
                    }
                },
                "required": ["action"]
            }
        }
        """;
    }
}