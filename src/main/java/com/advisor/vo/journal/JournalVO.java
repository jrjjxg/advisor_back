package com.advisor.vo.journal;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JournalVO {
    private String id;
    private String userId;
    private String title;
    private String content;     // 富文本内容
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String keywords;    // 系统提取的关键词
    private Integer wordCount;  // 字数统计
    private Integer isPrivate;
    private String relatedMoodId;
    private Object relatedMood; // 可以是一个适当的心情VO类型
    
    // 添加主题字段
    private String theme;       // 日记背景主题
    
    // 确保包含这些字段
    private String imageUrls;   // 图片URL列表，JSON数组格式
    private Integer imageCount; // 图片数量
    
    private String aiCompanionResponse; // AI伙伴回复内容
} 