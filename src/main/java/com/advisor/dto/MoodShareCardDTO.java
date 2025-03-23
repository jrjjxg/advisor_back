package com.advisor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoodShareCardDTO {
    private String userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalRecords;
    private Double averageMoodScore;
    private String dominantEmotion;
    private List<EmotionDistributionDTO> emotionDistribution;
    private List<DailyMoodDTO> moodTrend;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmotionDistributionDTO {
        private String name;
        private Double percentage;
        private String color;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyMoodDTO {
        private LocalDate date;
        private Double value;
    }
}