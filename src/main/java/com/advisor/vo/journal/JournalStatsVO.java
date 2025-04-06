package com.advisor.vo.journal;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 日记统计数据VO
 */
@Data
public class JournalStatsVO {
    
    /**
     * 总日记数
     */
    private Integer totalJournals;
    
    /**
     * 总字数
     */
    private Integer totalWordCount;
    
    /**
     * 平均字数
     */
    private Integer averageWordCount;
    
    /**
     * 最长日记字数
     */
    private Integer maxWordCount;
    
    /**
     * 最短日记字数（不包括空日记）
     */
    private Integer minWordCount;
    
    /**
     * 日记写作天数
     */
    private Integer totalDays;
    
    /**
     * 连续写作最长天数
     */
    private Integer maxStreakDays;
    
    /**
     * 当前连续写作天数
     */
    private Integer currentStreakDays;
    
    /**
     * 日记按日期统计数据
     */
    private List<Map<String, Object>> dailyStats;
    
    /**
     * 心情标签统计
     */
    private List<Map<String, Object>> moodTagStats;
    
    /**
     * 关键词统计
     */
    private List<Map<String, Object>> keywordStats;
    
    /**
     * 私密日记比例
     */
    private Double privateJournalRatio;
    
    /**
     * 带图片日记比例
     */
    private Double imageJournalRatio;
} 