package com.advisor.controller.journal;

import com.advisor.common.Result;
import com.advisor.service.journal.JournalService;
import com.advisor.util.UserUtil;
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