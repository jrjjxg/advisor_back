package com.advisor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 语音分析结果实体类
 */
@Data
@TableName("voice_analysis")
public class VoiceAnalysis {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

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
     * 检测到的音频事件JSON(Map<String, Integer>的序列化)
     */
    private String audioEventsJson;

    /**
     * 语音时长（毫秒）
     */
    private Long duration;

    /**
     * 语言代码 (zh, en 等)
     */
    private String languageCode;

    /**
     * 原始结果JSON
     */
    private String rawResultJson;

    /**
     * 任务ID (阿里云返回)
     */
    private String taskId;
    
    /**
     * 关联的日记ID (如果有)
     */
    private String journalId;
    
    /**
     * 关联的情绪记录ID (如果有)
     */
    private String moodId;
    
    /**
     * 分句信息JSON (List<SentenceInfo>的序列化)
     */
    private String sentencesJson;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志 (0: 未删除, 1: 已删除)
     */
    @TableLogic
    private Integer deleted;
} 