package com.advisor.dto.driftbottle;

import lombok.Data;

import java.util.List;

@Data
public class BottleCreateRequest {
    private String content;
    private List<String> images;
    private Integer isAnonymous;
} 