package com.advisor.entity.journal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("user_journal")
public class Journal {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String userId;
    private String title;
    private String content;     // 纯文本内容
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean isDeleted;  // 逻辑删除
    private String keywords;    // 系统提取的关键词
    private Integer wordCount;  // 字数统计
    private Integer isPrivate;  // 是否私密
    private String relatedMoodId; // 关联的心情记录ID
    // 新增字段
    private String theme;       // 日记背景主题
    private String imageUrls;   // 图片URL列表，JSON数组格式
    private Integer imageCount; // 图片数量
    private String emotionType; // 情感类型
    private Double emotionProb; // 情感概率
    private String emotionSubtype; // 子情感类型
    private Double emotionSubtypeProb; // 子情感概率
    private LocalDateTime emotionAnalysisTime; // 情感分析时间
    private String emotionAnalysisResult; // 情感分析结果
    private String aiCompanionResponse; // AI伙伴回复内容
} 