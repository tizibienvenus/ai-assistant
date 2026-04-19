package com.inov.assistant.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String role; // "user" ou "assistant"
    private String content;
    private String toolUsed;
    private LocalDateTime timestamp;
}