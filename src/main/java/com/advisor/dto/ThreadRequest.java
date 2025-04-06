package com.advisor.dto;

import lombok.Data;

@Data
public class ThreadRequest {
    private String id;
    private String title;
    private String createdAt;
    private String lastMessagePreview;
} 