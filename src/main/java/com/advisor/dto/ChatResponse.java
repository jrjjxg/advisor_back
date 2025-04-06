package com.advisor.dto;

import lombok.Data;

@Data
public class ChatResponse {
    private String message;
    private String timestamp;
    private String role; // "user" 或 "assistant"
} 