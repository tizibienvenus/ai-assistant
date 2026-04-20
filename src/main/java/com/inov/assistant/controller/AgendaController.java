package com.inov.assistant.controller;

import com.inov.assistant.dto.EventRequest;
import com.inov.assistant.model.Event;
import com.inov.assistant.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda")
public class AgendaController {
    
    private final AgendaService agendaService;
    
    @GetMapping
    @Operation(summary = "Liste les événements")
    public ResponseEntity<List<Event>> getEvents(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(required = false) String range
    ) {
        
        List<Event> events;
        if (date != null) {
            events = agendaService.getEventsByDate(date);
        } else if ("week".equals(range)) {
            events = agendaService.getEventsByDateRange(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusWeeks(1).atStartOfDay()
            );
        } else {
            events = agendaService.getAllEvents();
        }
        
        return ResponseEntity.ok(events);
    }
    
    @PostMapping
    @Operation(summary = "Crée un événement")
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventRequest request) {
        Event event = agendaService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un événement")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        agendaService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}")
    @Operation(summary = "Modifie un événement")
    public ResponseEntity<Event> updateEvent(@PathVariable String id, @Valid @RequestBody EventRequest request) {
        Event event = agendaService.updateEvent(id, request);
        return ResponseEntity.ok(event);
    }
}