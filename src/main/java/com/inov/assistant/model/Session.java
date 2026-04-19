package com.inov.assistant.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sessions")
public class Session {
    @Id
    private String id;
    private List<Message> messages = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;
    
}
