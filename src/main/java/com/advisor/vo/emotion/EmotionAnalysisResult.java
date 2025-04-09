// src/main/java/com/advisor/vo/emotion/EmotionAnalysisResult.java
package com.advisor.vo.emotion;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmotionAnalysisResult {
    private String label;        // 情感类型
    private Double prob;         // 情感概率
    private String subLabel;     // 子情感类型
    private Double subProb;      // 子情感概率
    private String text;         // 分析文本
    private LocalDateTime analysisTime; // 分析时间
}