package com.advisor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("psychological_profile")
public class PsychologicalProfile {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String userId;
    private Integer emotionalStability;
    private Integer anxietyLevel;
    private Integer depressionLevel;
    private Integer stressLevel;
    private Integer socialAdaptability;
    private String summary;
    private String riskFactors;
    private String strengths;
    private LocalDateTime updateTime;
}