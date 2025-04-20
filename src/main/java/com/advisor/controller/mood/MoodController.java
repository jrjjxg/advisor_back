package com.advisor.controller.mood;

import com.advisor.common.Result;
import com.advisor.dto.MoodRecordDTO;
import com.advisor.dto.MoodShareCardDTO;
import com.advisor.dto.TagStatDTO;
import com.advisor.service.mood.MoodService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moods")
public class MoodController {
    
    
    @Autowired
    private MoodService moodService;
    

    @PostMapping
    public Result<MoodRecordDTO> createMoodRecord(
            @RequestHeader("userId") String userId,
            @RequestBody MoodRecordDTO moodDTO) {
        moodDTO.setUserId(userId);
        MoodRecordDTO created = moodService.createMoodRecord(moodDTO);
        return Result.success(created);
    }
    
    @GetMapping("/{id}")
    public Result<MoodRecordDTO> getMoodRecord(
            @RequestHeader(value = "userId", required = false) String userId,
            @PathVariable Long id) {
     
        MoodRecordDTO mood = moodService.getMoodRecord(id);
        if (mood == null) {
            return Result.fail("情绪记录不存在");
        }
        return Result.success(mood);
    }
    
    @GetMapping("/history")
    public Result<Page<MoodRecordDTO>> getUserMoodHistory(
            @RequestHeader("userId") String userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<MoodRecordDTO> page = moodService.getUserMoodHistory(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/date-range")
    public Result<List<MoodRecordDTO>> getUserMoodByDateRange(
            @RequestHeader("userId") String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<MoodRecordDTO> moods = moodService.getUserMoodByDateRange(userId, startDate, endDate);
        return Result.success(moods);
    }
    
    @GetMapping("/analytics")
    public Result<Map<String, Object>> getMoodAnalytics(
            @RequestHeader("userId") String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        
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
    
    @GetMapping("/share-card/generate")
    public Result<MoodShareCardDTO> generateMoodShareCard(
            @RequestHeader(value = "userId", required = false) String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        MoodShareCardDTO shareCard = moodService.generateMoodShareCard(userId, startDate, endDate);
        return Result.success(shareCard);
    }
    
    @GetMapping("/weekly-mood")
    public Result<Map<String, Object>> getWeeklyMoodStats(@RequestHeader(value = "userId", required = false) String userId) {
        Map<String, Object> weeklyStats = moodService.getWeeklyMoodStats(userId);
        return Result.success(weeklyStats);
    }

    @GetMapping("/tag-stats")
    public Result getMoodTagStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestHeader("userId") String userId
    ) {
        // 1. 参数校验
        if (!StringUtils.hasText(userId)) {
            return Result.error("用户ID不能为空");
        }
        
        // 2. 日期处理
        LocalDate startLocalDate = null;
        LocalDate endLocalDate = null;
        if (StringUtils.hasText(startDate)) {
            startLocalDate = LocalDate.parse(startDate);
        }
        if (StringUtils.hasText(endDate)) {
            endLocalDate = LocalDate.parse(endDate);
        }
        
        // 3. 查询标签统计数据
        List<TagStatDTO> tagStats = moodService.getTagStats(userId, startLocalDate, endLocalDate);
        
        return Result.success(tagStats);
    }
}