package com.advisor.controller;

import com.advisor.common.Result;
import com.advisor.dto.voice.VoiceAnalysisDTO;
import com.advisor.service.VoiceRecognitionService;
import com.advisor.vo.VoiceAnalysisResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 语音识别控制器
 */
@RestController
@RequestMapping("/api/voice")
@Api(tags = "语音识别接口")
@Slf4j
public class VoiceRecognitionController {

    @Autowired
    private VoiceRecognitionService voiceRecognitionService;

    @PostMapping("/analyze")
    @ApiOperation("上传并分析语音文件")
    public Result<VoiceAnalysisResult> uploadAndAnalyze(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute VoiceAnalysisDTO dto) {
        try {
            log.info("收到语音分析请求，文件大小: {}, 语言代码: {}, 日记ID: {}, 情绪ID: {}",
                    file.getSize(), dto.getLanguageCode(), dto.getJournalId(), dto.getMoodId());

            VoiceAnalysisResult result = voiceRecognitionService.uploadAndAnalyze(file, dto);
            return Result.success(result);
        } catch (Exception e) {
            log.error("语音分析失败：", e);
            return Result.fail("语音分析失败：" + e.getMessage());
        }
    }

    @PostMapping("/analyzeByUrl")
    @ApiOperation("根据音频URL进行分析")
    public Result<VoiceAnalysisResult> analyzeByUrl(@RequestBody VoiceAnalysisUrlDTO dto) {
        try {
            log.info("根据URL分析语音，URL: {}, 语言代码: {}", dto.getAudioUrl(), dto.getLanguageCode());

            VoiceAnalysisResult result = voiceRecognitionService.analyzeByUrl(dto.getAudioUrl(), dto);
            return Result.success(result);
        } catch (Exception e) {
            log.error("语音分析失败：", e);
            return Result.fail("语音分析失败：" + e.getMessage());
        }
    }

    @GetMapping("/result/{taskId}")
    @ApiOperation("根据任务ID获取分析结果")
    public Result<VoiceAnalysisResult> getResult(@PathVariable String taskId) {
        try {
            VoiceAnalysisResult result = voiceRecognitionService.getAnalysisResult(taskId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取语音分析结果失败：", e);
            return Result.fail("获取语音分析结果失败：" + e.getMessage());
        }
    }

    @GetMapping("/journal/{journalId}")
    @ApiOperation("获取日记关联的语音分析结果")
    public Result<List<VoiceAnalysisResult>> getByJournalId(@PathVariable String journalId) {
        try {
            List<VoiceAnalysisResult> results = voiceRecognitionService.getAnalysisByJournalId(journalId);
            return Result.success(results);
        } catch (Exception e) {
            log.error("获取日记语音分析结果失败：", e);
            return Result.fail("获取日记语音分析结果失败：" + e.getMessage());
        }
    }

    @GetMapping("/mood/{moodId}")
    @ApiOperation("获取情绪记录关联的语音分析结果")
    public Result<List<VoiceAnalysisResult>> getByMoodId(@PathVariable String moodId) {
        try {
            List<VoiceAnalysisResult> results = voiceRecognitionService.getAnalysisByMoodId(moodId);
            return Result.success(results);
        } catch (Exception e) {
            log.error("获取情绪记录语音分析结果失败：", e);
            return Result.fail("获取情绪记录语音分析结果失败：" + e.getMessage());
        }
    }
}

/**
 * 语音分析URL请求DTO
 */
class VoiceAnalysisUrlDTO extends VoiceAnalysisDTO {

    /**
     * 音频文件URL
     */
    private String audioUrl;

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
