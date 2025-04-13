package com.advisor.controller.journal;

import com.advisor.common.Result;
import com.advisor.entity.journal.Journal;
import com.advisor.service.journal.JournalService;
import com.advisor.util.UserUtil;
import com.advisor.vo.journal.JournalRequest;
import com.advisor.vo.journal.JournalVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 日记控制器
 */
@RestController
@RequestMapping("/api/journal")
public class JournalController {

    private static final Logger logger = LoggerFactory.getLogger(JournalController.class);

    @Autowired
    private JournalService journalService;

    /**
     * 保存日记
     */
    @PostMapping("/save")
    public Result<String> saveOrUpdateJournal(@RequestBody JournalRequest journalRequest) {
        logger.info("保存或更新日记: {}", journalRequest);
        String userId = UserUtil.getCurrentUserId();
        String journalId = journalService.saveJournal(journalRequest, userId);
        return Result.success(journalId);
    }
    
    /**
     * 获取用户日记列表
     */
    @GetMapping("/list")
    public Result<Page<JournalVO>> getUserJournalsPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String mood,
            @RequestParam(required = false) Boolean hasImage,
            @RequestParam(required = false) Boolean isPrivate,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        String userId = UserUtil.getCurrentUserId();
        Page<JournalVO> journalsPage = journalService.getUserJournals(userId, pageNum, pageSize, 
                keyword, mood, hasImage, isPrivate,  date, startDate, endDate);
        return Result.success(journalsPage);
    }
    
    /**
     * 获取日记详情
     */
    @GetMapping("/get/{id}")
    public Result<JournalVO> getJournalDetail(@PathVariable String id) {
        String userId = UserUtil.getCurrentUserId();
        Journal journal = journalService.getJournalDetail(id, userId);
        JournalVO vo = new JournalVO();
        BeanUtils.copyProperties(journal, vo);
        
        // 确保图片URL和数量被正确复制
        vo.setImageUrls(journal.getImageUrls());
        vo.setImageCount(journal.getImageCount());
        vo.setAiCompanionResponse(journal.getAiCompanionResponse());
        
        return Result.success(vo);
    }
    
    /**
     * 删除日记
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteJournal(@PathVariable String id) {
        String userId = UserUtil.getCurrentUserId();
        boolean success = journalService.deleteJournal(id, userId);
        return Result.success(success);
    }
    
    /**
     * 获取日记统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getJournalStats() {
        String userId = UserUtil.getCurrentUserId();
        Map<String, Object> stats = journalService.getJournalStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取指定日期范围内的日记日期列表 (供日历使用)
     */
    @GetMapping("/dates")
    public Result<List<String>> getJournalDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        String userId = UserUtil.getCurrentUserId();
        List<String> dates = journalService.getJournalDatesInRange(userId, startDate, endDate);
        return Result.success(dates);
    }

    /**
     * 关联心情
     */
    @PutMapping("/link-mood/{journalId}/{moodId}")
    public Result<Boolean> linkMoodRecord(@PathVariable String journalId, @PathVariable String moodId) {
        String userId = UserUtil.getCurrentUserId();
        boolean success = journalService.linkMoodRecord(journalId, moodId, userId);
        return Result.success(success);
    }

    /**
     * 解除关联
     */
    @PutMapping("/unlink-mood/{journalId}")
    public Result<Boolean> unlinkMoodRecord(@PathVariable String journalId) {
        String userId = UserUtil.getCurrentUserId();
        boolean success = journalService.unlinkMoodRecord(journalId, userId);
        return Result.success(success);
    }

    /**
     * 根据心情ID获取日记
     */
    @GetMapping("/by-mood/{moodId}")
    public Result<List<JournalVO>> getJournalsByMoodId(@PathVariable String moodId) {
        String userId = UserUtil.getCurrentUserId();
        List<JournalVO> journals = journalService.getJournalsByMoodId(moodId, userId);
        return Result.success(journals);
    }

    /**
     * 保存AI伙伴的回复
     */
    @PostMapping("/{id}/save-ai-response")
    public Result<Void> saveAiResponse(@PathVariable String id, @RequestBody Map<String, String> payload) {
        String userId = UserUtil.getCurrentUserId();
        String aiResponse = payload.get("aiResponse");
        if (aiResponse == null) {
            return Result.error("AI response content is required.");
        }
        journalService.saveAiResponse(id, aiResponse, userId);
        return Result.success();
    }
} 