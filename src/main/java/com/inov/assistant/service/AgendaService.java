package com.inov.assistant.service;

import com.inov.assistant.dto.EventRequest;
import com.inov.assistant.model.Event;
import com.inov.assistant.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendaService {
    private static final Logger logger = LoggerFactory.getLogger(AgendaService.class);
    
    @Autowired
    private EventRepository eventRepository;
    
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Event> getEventsByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return eventRepository.findByDateTimeBetween(start, end);
    }
    
    @Transactional(readOnly = true)
    public List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findEventsInRange(start, end);
    }
    
    @Transactional
    public Event createEvent(EventRequest request) {
        Event event = Event.builder().
            title(request.getTitle())
            .dateTime(request.getDateTime())
            .participants(request.getParticipants())
            .notes(request.getNotes())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    
        
        Event savedEvent = eventRepository.save(event);
        logger.info("Event created: {} at {}", savedEvent.getTitle(), savedEvent.getDateTime());
        
        return savedEvent;
    }
    
    @Transactional
    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
        logger.info("Event deleted: {}", id);
    }
    
    @Transactional
    public Event updateEvent(String id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        
        event.setTitle(request.getTitle());
        event.setDateTime(request.getDateTime());
        event.setParticipants(request.getParticipants());
        event.setNotes(request.getNotes());
        event.setUpdatedAt(LocalDateTime.now());
        
        return eventRepository.save(event);
    }
}