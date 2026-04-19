package com.inov.assistant.mocks;

import com.inov.assistant.dto.EventRequest;
import com.inov.assistant.model.Event;
import com.inov.assistant.repository.EventRepository;
import com.inov.assistant.service.AgendaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {
    
    @Mock
    private EventRepository eventRepository;
    
    @InjectMocks
    private AgendaService agendaService;
    
    private Event testEvent;
    private EventRequest testRequest;
    
    @BeforeEach
    void setUp() {
        testEvent = Event.builder()
            .id("123")
            .title("Test Meeting")
            .dateTime(LocalDateTime.now())
            .participants(List.of("User1"))
            .notes("Test notes")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        testEvent.setId("123");
        
        testRequest = new EventRequest();
        testRequest.setTitle("Test Meeting");
        testRequest.setDateTime(LocalDateTime.now());
        testRequest.setParticipants(List.of("User1"));
        testRequest.setNotes("Test notes");
    }
    
    @Test
    void getAllEvents_ShouldReturnAllEvents() {
        when(eventRepository.findAll()).thenReturn(Arrays.asList(testEvent));
        
        List<Event> events = agendaService.getAllEvents();
        
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getTitle()).isEqualTo("Test Meeting");
        verify(eventRepository, times(1)).findAll();
    }
    
    @Test
    void createEvent_ShouldSaveAndReturnEvent() {
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        
        Event created = agendaService.createEvent(testRequest);
        
        assertThat(created).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Test Meeting");
        verify(eventRepository, times(1)).save(any(Event.class));
    }
    
    @Test
    void deleteEvent_WhenEventExists_ShouldDelete() {
        when(eventRepository.existsById("123")).thenReturn(true);
        doNothing().when(eventRepository).deleteById("123");
        
        agendaService.deleteEvent("123");
        
        verify(eventRepository, times(1)).deleteById("123");
    }
    
    @Test
    void deleteEvent_WhenEventNotExists_ShouldThrowException() {
        when(eventRepository.existsById("999")).thenReturn(false);
        
        assertThatThrownBy(() -> agendaService.deleteEvent("999"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Event not found");
    }
}