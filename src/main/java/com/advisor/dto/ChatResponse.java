package com.advisor.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatResponse {
    private String id;
    private String type = "text";
    private String message;
    private String timestamp;
    private String role; // "user", "assistant", "system" (卡片用system)
    private String diaryId;
    private String diaryTitle;
    private String diaryDate;
    private String filename;
    private String fileType;
} 