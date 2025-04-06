package com.advisor.mapper.journal;

import com.advisor.entity.journal.Journal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 日记Mapper接口
 */
@Mapper
public interface JournalMapper extends BaseMapper<Journal> {
    
    /**
     * 获取用户在指定日期范围内的日记统计数据
     * 注意：此方法使用XML配置的SQL
     */
    List<Map<String, Object>> getJournalStatsByDateRange(
            @Param("userId") String userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 获取用户在指定日期范围内的心情标签统计
     */
    List<Map<String, Object>> getRelatedMoodStatsByDateRange(
            @Param("userId") String userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 获取用户在指定日期范围内的平均字数
     */
    Map<String, Object> getAverageWordCount(
            @Param("userId") String userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 获取用户在指定日期范围内有日记的日期列表
     */
    List<String> getDistinctJournalDates(
            @Param("userId") String userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 获取用户关联心情的统计数据
     */
    List<Map<String, Object>> getRelatedMoodStats(@Param("userId") String userId);
    
    /**
     * 获取用户日记关键词统计
     */
    List<Map<String, Object>> getKeywordsStats(
            @Param("userId") String userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 按日统计日记数据
     */
    List<Map<String, Object>> getDailyJournalStats(
            @Param("userId") String userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 按周统计日记数据
     */
    List<Map<String, Object>> getWeeklyJournalStats(
            @Param("userId") String userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 按月统计日记数据
     */
    List<Map<String, Object>> getMonthlyJournalStats(
            @Param("userId") String userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 根据心情ID查找关联的日记
     */
    List<Journal> findByRelatedMoodId(@Param("moodId") String moodId);

    /**
     * 获取关键词频率
     */
    List<Map<String, Object>> getKeywordFrequency(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}