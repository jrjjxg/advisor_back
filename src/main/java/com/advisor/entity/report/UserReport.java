package com.advisor.entity.report;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_report")
public class UserReport {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String userId;
    private String reportType;  // monthly, weekly, yearly
    private String period;      // 格式：2023-05 (月报) 或 2023-W20 (周报) 或 2023 (年报)
    private String reportTitle;
    private String reportContent; // JSON存储报告内容
    private String keyInsights;   // 关键洞察点
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createTime;
    private Boolean isRead;       // 用户是否已阅读
} 