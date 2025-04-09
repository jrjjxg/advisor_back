// src/main/java/com/advisor/controller/emotion/EmotionAnalysisController.java
package com.advisor.controller.emotion;

import com.advisor.common.Result;
import com.advisor.entity.journal.Journal;
import com.advisor.mapper.journal.JournalMapper;
import com.advisor.service.emotion.BaiduEmotionService;
import com.advisor.vo.emotion.EmotionAnalysisResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emotion")
public class EmotionAnalysisController {
    
    @Autowired
    private BaiduEmotionService emotionService;
    @Autowired
    JournalMapper journalMapper;
    
    @PostMapping("/analyze")
    public Result<EmotionAnalysisResult> analyzeText(@RequestBody String text) {
        try {
            EmotionAnalysisResult result = emotionService.analyzeText(text);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("情感分析失败：" + e.getMessage());
        }
    }
    

    @GetMapping("/analysis/{journalId}")
    public Result<EmotionAnalysisResult> getEmotionAnalysis(@PathVariable String journalId) {
        try {
            EmotionAnalysisResult result = emotionService.getEmotionAnalysis(journalId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取情感分析结果失败：" + e.getMessage());
        }
    }
}