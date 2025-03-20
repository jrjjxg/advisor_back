package com.advisor.controller.mood;

import com.advisor.common.Result;
import com.advisor.dto.MoodRecordDTO;
import com.advisor.service.mood.MoodService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moods")
public class MoodController {
    
    private static final String DEFAULT_USER_ID = "test_user_001";
    
    @Autowired
    private MoodService moodService;
    
    // 获取有效的用户ID，如果为空则使用默认值
    private String getEffectiveUserId(String userId) {
        return userId != null ? userId : DEFAULT_USER_ID;
    }
    
    @PostMapping
    public Result<MoodRecordDTO> createMoodRecord(
            @RequestHeader(value = "userId", required = false) String userId,
            @RequestBody MoodRecordDTO moodDTO) {
        userId = getEffectiveUserId(userId);
        moodDTO.setUserId(userId);
        MoodRecordDTO created = moodService.createMoodRecord(moodDTO);
        return Result.success(created);
    }
    
    @GetMapping("/{id}")
    public Result<MoodRecordDTO> getMoodRecord(
            @RequestHeader(value = "userId", required = false) String userId,
            @PathVariable Long id) {
        userId = getEffectiveUserId(userId);
        MoodRecordDTO mood = moodService.getMoodRecord(id);
        if (mood == null) {
            return Result.fail("情绪记录不存在");
        }
        return Result.success(mood);
    }
    
    @GetMapping("/history")
    public Result<Page<MoodRecordDTO>> getUserMoodHistory(
            @RequestHeader(value = "userId", required = false) String userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        userId = getEffectiveUserId(userId);
        Page<MoodRecordDTO> page = moodService.getUserMoodHistory(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/date-range")
    public Result<List<MoodRecordDTO>> getUserMoodByDateRange(
            @RequestHeader(value = "userId", required = false) String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        userId = getEffectiveUserId(userId);
        List<MoodRecordDTO> moods = moodService.getUserMoodByDateRange(userId, startDate, endDate);
        return Result.success(moods);
    }
    
    @GetMapping("/analytics")  // 改回GET请求
    public Result<Map<String, Object>> getMoodAnalytics(
            @RequestHeader(value = "userId", required = false) String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        userId = getEffectiveUserId(userId);
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Map<String, Object> analytics = moodService.getMoodAnalytics(userId, startDate, endDate);
        return Result.success(analytics);
    }
    
    @GetMapping("/emotion-types")
    public Result<List<String>> getAllEmotionTypes(
            @RequestHeader(value = "userId", required = false) String userId) {
        // 情绪类型不需要用户ID，但为了接口一致性，仍然接收这个参数
        List<String> emotionTypes = moodService.getAllEmotionTypes();
        return Result.success(emotionTypes);
    }
    
    @GetMapping("/tags")
    public Result<List<String>> getAllTags(
            @RequestHeader(value = "userId", required = false) String userId) {
        // 标签不需要用户ID，但为了接口一致性，仍然接收这个参数
        List<String> tags = moodService.getAllTags();
        return Result.success(tags);
    }
}