package com.advisor.vo.report;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MoodDataVO {
    private double averageMoodScore;
    private List<Map<String, Object>> dailyMoods;
    private List<Map<String, Object>> emotionTypeDistribution;
    private List<Map<String, Object>> commonTags;
} 