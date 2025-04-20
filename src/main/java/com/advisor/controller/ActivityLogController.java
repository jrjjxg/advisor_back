package com.advisor.controller;

import com.advisor.common.Result;
import com.advisor.dto.ActivityLogRequest;
import com.advisor.entity.userbehavior.UserActivityLog;
import com.advisor.service.userbehavior.UserActivityLogService;
import com.advisor.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 用户活动日志控制器
 */
@RestController
@RequestMapping("/api/activity")
public class ActivityLogController {

    @Autowired
    private UserActivityLogService userActivityLogService;

    /**
     * 上报用户活动日志
     */
    @PostMapping("/log")
    public Result<?> logActivity(@RequestBody ActivityLogRequest request, HttpServletRequest servletRequest) {
        // 获取当前用户ID
        String userId = UserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "未登录");
        }

        // 创建活动日志对象
        UserActivityLog log = new UserActivityLog();
        log.setUserId(userId);
        log.setEventType(request.getEvent_type());
        log.setEventTimestamp(LocalDateTime.now());
        log.setPageUrl(request.getPage_url());
        log.setDurationSec(request.getDuration_sec());
        log.setFeatureName(request.getFeature_name());
        log.setEventDetails(request.getEvent_details());

        // 保存日志
        userActivityLogService.save(log);
        
        return Result.success(null);
    }
    
} 