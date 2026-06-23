package com.madhuka.tutor_finder_backend.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String role; // "user" or "assistant"
    private String content;
}
