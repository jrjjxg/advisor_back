package com.advisor.controller.journal;

import com.advisor.common.Result;
import com.advisor.service.journal.JournalService;
import com.advisor.util.UserUtil;
import com.advisor.vo.journal.JournalStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 日记统计控制器
 */
@RestController
@RequestMapping("/api/journal/stats")
public class JournalStatsController {
    
    @Autowired
    private JournalService journalService;
    
    /**
     * 获取用户日记统计数据
     */
    @GetMapping("/overview")
    public Result<JournalStatsVO> getJournalStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        String userId = UserUtil.getCurrentUserId();
        
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        
        JournalStatsVO stats = journalService.getUserJournalStats(userId, startDateTime, endDateTime);
        return Result.success(stats);
    }
    
    /**
     * 获取用户日记写作趋势
     */
    @GetMapping("/trends")
    public Result<Map<String, Object>> getJournalTrends(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        String userId = UserUtil.getCurrentUserId();
        
        // 如果没有指定开始日期，默认为30天前
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        
        // 如果没有指定结束日期，默认为今天
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        Map<String, Object> trends = journalService.getJournalTrends(userId, period, startDateTime, endDateTime);
        return Result.success(trends);
    }
    
    /**
     * 获取用户日记内容分析
     */
    @GetMapping("/content-analysis")
    public Result<Map<String, Object>> getContentAnalysis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        String userId = UserUtil.getCurrentUserId();
        
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        
        Map<String, Object> analysis = journalService.getJournalContentAnalysis(userId, startDateTime, endDateTime);
        return Result.success(analysis);
    }
    
    /**
     * 获取用户在指定日期范围内有日记的日期列表
     */
    @GetMapping("/dates")
    public Result<List<String>> getJournalDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        String userId = UserUtil.getCurrentUserId();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<String> dates = journalService.getJournalDatesInRange(userId, startDateTime, endDateTime);
        return Result.success(dates);
    }
    
    /**
     * 获取用户日记关键词云数据
     */
    @GetMapping("/keyword-cloud")
    public Result<List<Map<String, Object>>> getKeywordCloudData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "30") Integer limit) {
        
        String userId = UserUtil.getCurrentUserId();
        
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        
        List<Map<String, Object>> keywordData = journalService.getKeywordCloudData(userId, startDateTime, endDateTime, limit);
        return Result.success(keywordData);
    }
} 