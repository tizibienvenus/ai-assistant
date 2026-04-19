// model/Event.java
package com.inov.assistant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
public class Event {
    @Id
    private String id;
    private String title;
    private LocalDateTime dateTime;
    private List<String> participants;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}