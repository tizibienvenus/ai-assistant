package com.inov.assistant.dto;


import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String sessionId;
    
    @NotBlank(message = "Message cannot be empty")
    private String message;
}