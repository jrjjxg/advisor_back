package com.advisor.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 语音分析结果VO
 */
@Data
@NoArgsConstructor
public class VoiceAnalysisResult {

    /**
     * 语音识别ID
     */
    private String id;

    /**
     * 录音文件URL
     */
    private String audioUrl;

    /**
     * 识别的原始文本内容(包含情感和事件标记)
     */
    private String rawText;
    
    /**
     * 识别的纯文本内容(已去除标记)
     */
    private String transcription;

    /**
     * 主要情感类型(ANGRY、HAPPY、SAD、NEUTRAL中的一种)
     */
    private String dominantEmotion;
    
    /**
     * 检测到的音频事件
     * key: 事件类型(Applause、BGM、Laughter、Speech)
     * value: 在文本中出现的次数
     */
    private Map<String, Integer> audioEvents;

    /**
     * 语音时长（毫秒）
     */
    private Long duration;

    /**
     * 语言代码 (zh, en 等)
     */
    private String languageCode;

    /**
     * 分析完成时间
     */
    private LocalDateTime analysisTime;

    /**
     * 任务ID (阿里云SenseVoice返回的任务ID)
     */
    private String taskId;
    
    /**
     * 任务状态 (PENDING、RUNNING、SUCCEEDED、FAILED)
     */
    private String taskStatus;
    
    /**
     * 关联的日记ID (如果有)
     */
    private String journalId;
    
    /**
     * 关联的情绪记录ID (如果有)
     */
    private String moodId;

    /**
     * 识别的分句内容
     */
    private List<SentenceInfo> sentences;

    /**
     * 分句信息
     */
    @Data
    @NoArgsConstructor
    public static class SentenceInfo {
        /**
         * 句子原始文本(包含标记)
         */
        private String rawText;
        
        /**
         * 句子纯文本(去除标记)
         */
        private String text;
        
        /**
         * 开始时间（毫秒）
         */
        private Long beginTime;
        
        /**
         * 结束时间（毫秒）
         */
        private Long endTime;
        
        /**
         * 句子中检测到的情感(ANGRY、HAPPY、SAD、NEUTRAL中的一种)
         */
        private String emotion;
        
        /**
         * 句子中检测到的事件
         * 可能的值: Applause(掌声)、BGM(背景音乐)、Laughter(笑声)、Speech(说话声)
         */
        private List<String> events;
    }
} 