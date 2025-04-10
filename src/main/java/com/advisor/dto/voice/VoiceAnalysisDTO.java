package com.advisor.dto.voice;

import lombok.Data;

/**
 * 语音分析请求DTO
 */
@Data
public class VoiceAnalysisDTO {
    
    /**
     * 关联的日记ID (如果是从日记模块调用)
     */
    private String journalId;
    
    /**
     * 关联的情绪记录ID (如果是从情绪记录模块调用)
     */
    private String moodId;
    
    /**
     * 语言代码（可选，默认zh，即中文）
     */
    private String languageCode = "zh";
} 