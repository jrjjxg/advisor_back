package com.advisor.vo.report;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserReportVO {
    private String id;
    private String userId;
    private String reportType;
    private String period;
    private String reportTitle;
    private String keyInsights;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createTime;
    private Boolean isRead;
} 