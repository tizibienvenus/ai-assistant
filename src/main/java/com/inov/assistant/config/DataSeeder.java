package com.inov.assistant.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.inov.assistant.model.Event;
import com.inov.assistant.repository.EventRepository;

@Component
public class DataSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    
    @Autowired
    private EventRepository eventRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (eventRepository.count() == 0) {
            logger.info("Seeding initial events...");
            
            LocalDate today = LocalDate.now();
            
            List<Event> events = Arrays.asList(
                Event.builder()
                    .title("Comité de direction")
                    .dateTime(LocalDateTime.of(today.plusDays(1), LocalTime.of(9, 0)))
                    .participants(Arrays.asList("DG", "DAF", "DSI"))
                    .notes("Budget Q2 à valider")
                    .createdAt(LocalDateTime.of(today.plusDays(1), LocalTime.of(7, 0)))
                    .updatedAt(LocalDateTime.of(today.plusDays(1), LocalTime.of(7, 0)))
                    .build(),
                    
                Event.builder()
                    .title("Réunion équipe Tech")
                    .dateTime(LocalDateTime.of(today.plusDays(1), LocalTime.of(14, 30)))
                    .participants(Arrays.asList("Lead Dev", "DevOps"))
                    .notes("Point sprint en cours")
                    .createdAt(LocalDateTime.of(today.plusDays(1), LocalTime.of(7, 0)))
                    .updatedAt(LocalDateTime.of(today.plusDays(1), LocalTime.of(7, 0)))
                    .build(),
                    
                Event.builder()
                    .title("Call client Ministère")
                    .dateTime(LocalDateTime.of(today.plusDays(2), LocalTime.of(11, 0)))
                    .participants(Arrays.asList("Client", "Chef de projet"))
                    .notes("Revue livrables phase 2")
                    .createdAt(LocalDateTime.of(today.plusDays(2), LocalTime.of(7, 0)))
                    .updatedAt(LocalDateTime.of(today.plusDays(2), LocalTime.of(7, 0)))
                    .build(),
                    
                Event.builder()
                    .title("Déjeuner partenaire")
                    .dateTime(LocalDateTime.of(today.plusDays(3), LocalTime.of(12, 30)))
                    .participants(Arrays.asList("Partenaire externe"))
                    .notes("Hôtel Hilton Yaoundé")
                    .createdAt(LocalDateTime.of(today.plusDays(3), LocalTime.of(7, 0)))
                    .updatedAt(LocalDateTime.of(today.plusDays(3), LocalTime.of(7, 0)))

                    .build(),
                    
                Event.builder()
                    .title("Revue RH mensuelle")
                    .dateTime(LocalDateTime.of(today.plusDays(4), LocalTime.of(10, 0)))
                    .participants(Arrays.asList("DRH", "Managers"))
                    .notes("Évaluations semestrielles")
                    .createdAt(LocalDateTime.of(today.plusDays(4), LocalTime.of(7, 0)))
                    .updatedAt(LocalDateTime.of(today.plusDays(4), LocalTime.of(7, 0)))
                    .build()
            );
            
            eventRepository.saveAll(events);
            logger.info("Seeded {} events", events.size());
        }
    }
}