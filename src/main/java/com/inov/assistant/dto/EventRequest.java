package com.inov.assistant.dto;

import java.time.LocalDateTime;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    private String title;
    private LocalDateTime dateTime; 
    private List<String> participants;
    private String notes;
}
