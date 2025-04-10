package com.advisor.service;

import com.advisor.dto.voice.VoiceAnalysisDTO;
import com.advisor.vo.VoiceAnalysisResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 语音识别服务接口
 */
public interface VoiceRecognitionService {
    
    /**
     * 上传语音文件并提交语音分析任务
     * 
     * @param file 语音文件
     * @param dto 分析请求参数
     * @return 任务提交结果
     */
    VoiceAnalysisResult uploadAndAnalyze(MultipartFile file, VoiceAnalysisDTO dto);
    
    /**
     * 根据音频URL提交语音分析任务
     * 
     * @param audioUrl 音频文件URL
     * @param dto 分析请求参数
     * @return 任务提交结果
     */
    VoiceAnalysisResult analyzeByUrl(String audioUrl, VoiceAnalysisDTO dto);
    
    /**
     * 根据任务ID查询语音分析结果
     * 
     * @param taskId 任务ID
     * @return 语音分析结果
     */
    VoiceAnalysisResult getAnalysisResult(String taskId);
    
    /**
     * 根据日记ID获取关联的语音分析结果
     * 
     * @param journalId 日记ID
     * @return 语音分析结果列表
     */
    List<VoiceAnalysisResult> getAnalysisByJournalId(String journalId);
    
    /**
     * 根据情绪记录ID获取关联的语音分析结果
     * 
     * @param moodId 情绪记录ID
     * @return 语音分析结果列表
     */
    List<VoiceAnalysisResult> getAnalysisByMoodId(String moodId);
} 