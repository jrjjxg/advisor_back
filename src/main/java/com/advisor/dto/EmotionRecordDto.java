package com.advisor.dto;

import lombok.Data;
import java.util.Map;

@Data
public class EmotionRecordDto {
    private String userId;
    private String predictedEmotion;
    private Double probability;
    private Map<String, Double> allProbabilities;
    private String imageUrl;
    private boolean saveImage;
} 