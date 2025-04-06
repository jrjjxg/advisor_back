package com.advisor.controller.report;

import com.advisor.common.Result;
import com.advisor.entity.report.UserReport;
import com.advisor.service.report.UserReportService;
import com.advisor.vo.report.UserReportVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class UserReportController {

    @Autowired
    private UserReportService userReportService;
    
    @PostMapping("/weekly")
    public Result<String> createWeeklyReport(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime weekStart) {
        String reportId = userReportService.createWeeklyReport(userId, weekStart);
        return Result.success(reportId);
    }
    
    @PostMapping("/monthly")
    public Result<String> createMonthlyReport(
            @RequestParam String userId,
            @RequestParam int year,
            @RequestParam int month) {
        String reportId = userReportService.createMonthlyReport(userId, year, month);
        return Result.success(reportId);
    }
    
    @PostMapping("/yearly")
    public Result<String> createYearlyReport(
            @RequestParam String userId,
            @RequestParam int year) {
        String reportId = userReportService.createYearlyReport(userId, year);
        return Result.success(reportId);
    }
    
    /**
     * 创建自定义时间段的用户报告
     * @param requestBody 包含用户ID、开始日期和结束日期的请求体
     * @return 包含生成报告ID的Result对象
     */
    @PostMapping("/custom")
    public Result<String> createCustomReport(@RequestBody Map<String, Object> requestBody) {
        String userId = (String) requestBody.get("userId");
        LocalDateTime startDate = LocalDateTime.parse((String) requestBody.get("startDate"));
        LocalDateTime endDate = LocalDateTime.parse((String) requestBody.get("endDate"));
        
        // 参数验证
        if (userId == null || userId.isEmpty()) {
            return Result.fail("用户ID不能为空");
        }
        if (startDate == null || endDate == null) {
            return Result.fail("日期参数无效");
        }
        
        // 调用服务层方法生成自定义报告
        String reportId = userReportService.createCustomReport(userId, startDate, endDate);
        return Result.success(reportId);
    }
    
    @GetMapping("/user/{userId}")
    public Result<Page<UserReportVO>> getUserReports(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<UserReportVO> reports = userReportService.getUserReports(userId, pageNum, pageSize);
        return Result.success(reports);
    }
    
    @GetMapping("/{reportId}")
    public Result<UserReport> getReportById(@PathVariable String reportId) {
        UserReport report = userReportService.getReportById(reportId);
        // 可以在这里添加逻辑，如果报告未读，则标记为已读
        // userReportService.markReportAsReadIfNotRead(reportId); // 示例调用
        return Result.success(report);
    }
    
    @PutMapping("/{reportId}/read")
    public Result<Void> markReportAsRead(@PathVariable String reportId) {
        userReportService.markReportAsRead(reportId);
        return Result.success();
    }
} 