package com.inov.assistant.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inov.assistant.dto.EventRequest;
import com.inov.assistant.model.Event;
import com.inov.assistant.model.RangeType;
import com.inov.assistant.service.AgendaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda")
public class AgendaController {
    
    private final AgendaService agendaService;
    
    @GetMapping
    @Operation(summary = "Liste les événements")
    public ResponseEntity<List<Event>> getEvents(
        @Parameter(description = "Filtre par date (format ISO: yyyy-MM-dd)", example = "2026-04-20")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,

        @Parameter(description = "Plage de temps (DAY, WEEK, MONTH)", example = "WEEK")
        @RequestParam(required = false)
        RangeType range
    ) {

        List<Event> events;

        if (date != null) {
            events = agendaService.getEventsByDate(date);
            return ResponseEntity.ok(events);
        }

        LocalDate start = LocalDate.now();

        switch (range != null ? range : RangeType.DAY) {

            case DAY -> {
                events = agendaService.getEventsByDateRange(
                    start.atStartOfDay(),
                    start.plusDays(1).atStartOfDay()
                );
            }

            case WEEK -> {
                events = agendaService.getEventsByDateRange(
                    start.atStartOfDay(),
                    start.plusWeeks(1).atStartOfDay()
                );
            }

            case MONTH -> {
                events = agendaService.getEventsByDateRange(
                    start.atStartOfDay(),
                    start.plusMonths(1).atStartOfDay()
                );
            }

            default -> events = agendaService.getAllEvents();
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