package com.advisor.vo.report;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JournalDataVO {
    private int totalJournals;
    private double averageMoodScore;
    private double averageWordCount;
    private List<Map<String, Object>> dailyStats;
    private List<Map<String, Object>> moodTagStats;
} 