package com.advisor.config;

import com.advisor.service.report.UserReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class TaskConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskConfig.class);
    
    @Autowired
    private UserReportService userReportService;
    
    /**
     * 每周一凌晨2点生成用户周报
     * 时区为系统默认时区
     */
    @Scheduled(cron = "0 0 2 ? * MON")
    public void generateWeeklyReports() {
        logger.info("开始生成用户周报...");
        userReportService.generateWeeklyReportsForAllUsers();
        logger.info("用户周报生成完成");
    }
    
    /**
     * 每月1日凌晨3点生成用户月报
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void generateMonthlyReports() {
        logger.info("开始生成用户月报...");
        userReportService.generateMonthlyReportsForAllUsers();
        logger.info("用户月报生成完成");
    }
} 