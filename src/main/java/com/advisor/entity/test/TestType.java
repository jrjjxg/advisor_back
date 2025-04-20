package com.advisor.entity.test;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List; // Import for potential JSON handling
import java.util.Map;  // Import for potential JSON handling

@Data
@TableName("test_type")
public class TestType {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String description;
    private String icon;
    private String imageUrl; // 新增字段
    private String category;
    private Integer timeMinutes;
    private Integer questionCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;

    // --- 新增科普字段 ---
    @TableField("theoretical_basis") 
    private String theoreticalBasis;

    @TableField("application_scenarios")
    private String applicationScenarios;

    @TableField("precautions")
    private String precautions;

    @TableField("detailed_interpretation")
    private String detailedInterpretation;

    @TableField("development_history")
    private String developmentHistory;

    @TableField("related_studies")
    private String relatedStudies;

    @TableField("faq")
    private String faq; 
}