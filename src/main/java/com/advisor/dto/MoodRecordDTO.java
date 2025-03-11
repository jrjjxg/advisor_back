package com.advisor.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MoodRecordDTO {
    private Long id;
    private String userId;
    private String emotionType;
    private Integer intensity;
    private String note;
    private List<String> tags;
    private LocalDateTime createTime;
}