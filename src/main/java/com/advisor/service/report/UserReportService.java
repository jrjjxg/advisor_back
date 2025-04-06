package com.advisor.service.report;

import com.advisor.entity.report.UserReport;
import com.advisor.vo.report.UserReportVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface UserReportService {
    /**
     * 创建周报告
     */
    String createWeeklyReport(String userId, LocalDateTime weekStart);
    
    /**
     * 创建月报告
     */
    String createMonthlyReport(String userId, int year, int month);
    
    /**
     * 创建年报告
     */
    String createYearlyReport(String userId, int year);
    
    /**
     * 创建自定义时间段的报告
     * @param userId 用户ID
     * @param startDate 报告开始日期时间
     * @param endDate 报告结束日期时间
     * @return 生成的报告ID
     */
    String createCustomReport(String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取用户的所有报告
     */
    Page<UserReportVO> getUserReports(String userId, int pageNum, int pageSize);
    
    /**
     * 根据ID获取报告
     */
    UserReport getReportById(String reportId);
    
    /**
     * 标记报告为已读
     */
    void markReportAsRead(String reportId);
    
    /**
     * 检查并创建所有用户的周报告（定时任务使用）
     */
    void generateWeeklyReportsForAllUsers();
    
    /**
     * 检查并创建所有用户的月报告（定时任务使用）
     */
    void generateMonthlyReportsForAllUsers();
} 