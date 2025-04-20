package com.advisor.controller.behavior;

import com.advisor.common.Result;
import com.advisor.entity.base.User;
import com.advisor.service.UserService;
import com.advisor.service.userbehavior.BehaviorAnalysisService;
import com.advisor.util.UserUtil;
import com.advisor.vo.userbehavior.BehaviorReportVO;
import com.advisor.vo.userbehavior.FeaturePreferenceVO;
import com.advisor.vo.userbehavior.TimeDistributionVO;
import com.advisor.vo.userbehavior.UsageDurationVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户行为分析控制器
 */
@RestController
@RequestMapping("/api/behavior")
public class BehaviorController {
    
    @Autowired
    private BehaviorAnalysisService behaviorAnalysisService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取用户深夜活动分析
     * @param username 用户名，不传则获取当前用户
     * @param days 天数范围，默认7天
     * @return 深夜活动数据
     */
    @GetMapping("/late-night")
    public Result<Integer> getLateNightActivity(
            @RequestParam String username,
            @RequestParam(defaultValue = "7") Integer days) {
        
        String targetUserId = getUserIdByUsername(username);
        if (targetUserId == null) {
            return Result.fail("无效的用户名或用户不存在");
        }
        
        int lateNightDays = behaviorAnalysisService.getLateNightActivityDays(targetUserId, days);
        return Result.success(lateNightDays);
    }
    
    /**
     * 获取用户使用时长统计
     * @param username 用户名，不传则获取当前用户
     * @param days 天数范围，默认7天
     * @return 使用时长统计数据
     */
    @GetMapping("/usage-duration")
    public Result<UsageDurationVO> getUsageDuration(
            @RequestParam String username,
            @RequestParam(defaultValue = "7") Integer days) {
        
        String targetUserId = getUserIdByUsername(username);
        if (targetUserId == null) {
            return Result.fail("无效的用户名或用户不存在");
        }
        
        UsageDurationVO data = behaviorAnalysisService.getUsageDuration(targetUserId, days);
        return Result.success(data);
    }
    
    /**
     * 获取用户功能偏好分析
     * @param username 用户名，不传则获取当前用户
     * @param days 天数范围，默认7天
     * @return 功能偏好分析数据
     */
    @GetMapping("/feature-preference")
    public Result<FeaturePreferenceVO> getFeaturePreference(
            @RequestParam String username,
            @RequestParam(defaultValue = "7") Integer days) {
        
        String targetUserId = getUserIdByUsername(username);
        if (targetUserId == null) {
            return Result.fail("无效的用户名或用户不存在");
        }
        
        FeaturePreferenceVO data = behaviorAnalysisService.getFeaturePreference(targetUserId, days);
        return Result.success(data);
    }
    
    /**
     * 获取用户使用时间分布
     * @param username 用户名，不传则获取当前用户
     * @param days 天数范围，默认7天
     * @return 使用时间分布数据
     */
    @GetMapping("/time-distribution")
    public Result<TimeDistributionVO> getTimeDistribution(
            @RequestParam String username,
            @RequestParam(defaultValue = "7") Integer days) {
        
        String targetUserId = getUserIdByUsername(username);
        if (targetUserId == null) {
            return Result.fail("无效的用户名或用户不存在");
        }
        
        TimeDistributionVO data = behaviorAnalysisService.getTimeDistribution(targetUserId, days);
        return Result.success(data);
    }
    
    /**
     * 获取用户行为综合分析报告
     * @param username 用户名，不传则获取当前用户
     * @return 行为分析报告
     */
    @GetMapping("/report")
    public Result<BehaviorReportVO> getUserBehaviorReport(
            @RequestParam String username) {
        
        String targetUserId = getUserIdByUsername(username);
        if (targetUserId == null) {
            return Result.fail("无效的用户名或用户不存在");
        }
        
        BehaviorReportVO data = behaviorAnalysisService.getUserBehaviorReport(targetUserId);
        return Result.success(data);
    }
    
    /**
     * 根据传入的 username 获取用户 UUID ID
     * @param username 要查询的用户名
     * @return 用户的 UUID ID，如果 username 为空或用户不存在则返回 null
     */
    private String getUserIdByUsername(String username) {
        // 如果 username 为空，则无法确定用户
        if (username == null || username.isEmpty()) {
            return null;
        }

        // 根据 username 查询 User 对象
        User user = userService.getUserByUsername(username);
        if (user == null) {
            // 用户不存在
            return null;
        }
        
        // 返回用户的 UUID ID
        return user.getId();
    }
} 