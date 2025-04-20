package com.advisor.service.impl;

import com.advisor.entity.userbehavior.UserActivityLog;
import com.advisor.mapper.userbehavior.UserActivityLogMapper;
import com.advisor.service.userbehavior.BehaviorAnalysisService;
import com.advisor.vo.userbehavior.BehaviorReportVO;
import com.advisor.vo.userbehavior.FeaturePreferenceVO;
import com.advisor.vo.userbehavior.TimeDistributionVO;
import com.advisor.vo.userbehavior.UsageDurationVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户行为分析服务实现类
 */
@Service
public class BehaviorAnalysisServiceImpl implements BehaviorAnalysisService {
    
    @Autowired
    private UserActivityLogMapper userActivityLogMapper;
    
    /**
     * 获取用户深夜活动天数
     */
    @Override
    public int getLateNightActivityDays(String userId, int days) {
        // 查询用户在过去指定天数内的夜间活动天数
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        
        // 深夜时间范围：22:00-06:00
        return userActivityLogMapper.countUserNightActivity(userId, days, 22, 6);
    }
    
    /**
     * 获取用户使用时长统计
     */
    @Override
    public UsageDurationVO getUsageDuration(String userId, int days) {
        UsageDurationVO vo = new UsageDurationVO();
        Map<String, Long> dailyUsage = new HashMap<>();
        long totalDurationSec = 0;
        
        // 查询过去N天的用户活动记录
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<UserActivityLog> wrapper = Wrappers.<UserActivityLog>lambdaQuery()
                .eq(UserActivityLog::getUserId, userId)
                .ge(UserActivityLog::getEventTimestamp, startTime)
                .isNotNull(UserActivityLog::getDurationSec);
        
        List<UserActivityLog> logs = userActivityLogMapper.selectList(wrapper);
        
        // 按天统计使用时长
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, List<UserActivityLog>> logsByDay = logs.stream()
                .collect(Collectors.groupingBy(log -> 
                    log.getEventTimestamp().format(formatter)));
        
        // 生成过去N天的日期列表，确保每一天都有数据
        for (int i = 0; i < days; i++) {
            String date = LocalDateTime.now().minusDays(i).format(formatter);
            List<UserActivityLog> dayLogs = logsByDay.getOrDefault(date, Collections.emptyList());
            
            // 计算当天总时长 (秒)
            long dayDurationSec = dayLogs.stream()
                    .filter(log -> log.getDurationSec() != null)
                    .mapToLong(UserActivityLog::getDurationSec)
                    .sum();
            
            dailyUsage.put(date, dayDurationSec);
            totalDurationSec += dayDurationSec;
        }
        
        vo.setDailyUsage(dailyUsage);
        vo.setTotalDuration(totalDurationSec);
        vo.setAvgDailyDuration(days > 0 ? totalDurationSec / days : 0);
        
        return vo;
    }
    
    /**
     * 获取用户功能偏好分析
     */
    @Override
    public FeaturePreferenceVO getFeaturePreference(String userId, int days) {
        FeaturePreferenceVO vo = new FeaturePreferenceVO();
        
        // 查询过去N天的用户活动记录
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<UserActivityLog> wrapper = Wrappers.<UserActivityLog>lambdaQuery()
                .eq(UserActivityLog::getUserId, userId)
                .ge(UserActivityLog::getEventTimestamp, startTime)
                .eq(UserActivityLog::getEventType, "PAGE_VIEW")  // 只统计页面访问
                .isNotNull(UserActivityLog::getPageUrl);         // 确保有页面URL
        
        List<UserActivityLog> logs = userActivityLogMapper.selectList(wrapper);
        
        // 统计各功能使用次数和时长
        Map<String, Integer> featureCountMap = new HashMap<>();
        Map<String, Long> featureDurationSecMap = new HashMap<>();
        
        for (UserActivityLog log : logs) {
            String pageUrl = log.getPageUrl();
            if (pageUrl != null && !pageUrl.isEmpty()) {
                // 将页面URL映射到功能模块
                String feature = mapUrlToFeature(pageUrl);
                if (feature != null) {
                    // 计数
                    featureCountMap.put(feature, featureCountMap.getOrDefault(feature, 0) + 1);
                    
                    // 累计时长 (秒)
                    if (log.getDurationSec() != null) {
                        featureDurationSecMap.put(feature, 
                                featureDurationSecMap.getOrDefault(feature, 0L) + log.getDurationSec());
                    }
                }
            }
        }
        
        // 转换为FeatureCount对象列表
        List<FeaturePreferenceVO.FeatureCount> features = new ArrayList<>();
        int totalCount = 0;
        
        for (Map.Entry<String, Integer> entry : featureCountMap.entrySet()) {
            FeaturePreferenceVO.FeatureCount fc = new FeaturePreferenceVO.FeatureCount();
            fc.setName(entry.getKey());
            fc.setCount(entry.getValue());
            fc.setDuration(featureDurationSecMap.getOrDefault(entry.getKey(), 0L));
            
            features.add(fc);
            totalCount += entry.getValue();
        }
        
        // 按使用次数排序
        features.sort((a, b) -> b.getCount() - a.getCount());
        
        vo.setTopFeatures(features);
        vo.setTotalCount(totalCount);
        
        // 设置最常用功能
        if (!features.isEmpty()) {
            vo.setMostUsedFeature(features.get(0).getName());
        }
        
        return vo;
    }
    
    /**
     * 将页面URL映射到功能模块
     * @param pageUrl 页面URL
     * @return 功能模块名称
     */
    private String mapUrlToFeature(String pageUrl) {
        // 移除开头的斜杠
        if (pageUrl.startsWith("/")) {
            pageUrl = pageUrl.substring(1);
        }
        
        // 主要功能模块映射
        if (pageUrl.startsWith("pages/emotion-recognition/")) {
            return "情绪识别";
        } else if (pageUrl.startsWith("pages/psychological-test/") || 
                  pageUrl.startsWith("pages/test-questions/") ||
                  pageUrl.startsWith("pages/test-result/")) {
            return "心理测评";
        } else if (pageUrl.startsWith("pages/mood-tracker/")) {
            return "情绪记录";
        } else if (pageUrl.startsWith("pages/meditation/")) {
            return "冥想放松";
        } else if (pageUrl.startsWith("pages/chatbot/")) {
            return "AI助手";
        } else if (pageUrl.startsWith("pages/journal/")) {
            return "智能日记";
        } else if (pageUrl.startsWith("pages/social-hub/") || 
                  pageUrl.startsWith("pages/community/")) {
            return "社区";
        } else if (pageUrl.startsWith("pages/driftbottle/")) {
            return "漂流瓶";
        } else if (pageUrl.startsWith("pages/reports/")) {
            return "综合报告";
        } else if (pageUrl.equals("pages/toolbox/index")) {
            return "工具箱";
        } else if (pageUrl.equals("pages/index/index")) {
            return "首页";
        } else if (pageUrl.startsWith("pages/profile/")) {
            return "个人中心";
        } else if (pageUrl.startsWith("pages/auth/")) {
            return "用户认证";
        }
        
        // 如果没有匹配到任何功能模块，返回null
        return null;
    }
    
    /**
     * 获取用户使用时间分布
     */
    @Override
    public TimeDistributionVO getTimeDistribution(String userId, int days) {
        TimeDistributionVO vo = new TimeDistributionVO();
        Map<String, Integer> hourlyDistribution = new HashMap<>();
        
        // 初始化24小时数据
        for (int hour = 0; hour < 24; hour++) {
            String hourStr = String.format("%02d:00", hour);
            hourlyDistribution.put(hourStr, 0);
        }
        
        // 查询过去N天的用户活动记录
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<UserActivityLog> wrapper = Wrappers.<UserActivityLog>lambdaQuery()
                .eq(UserActivityLog::getUserId, userId)
                .ge(UserActivityLog::getEventTimestamp, startTime);
        
        List<UserActivityLog> logs = userActivityLogMapper.selectList(wrapper);
        
        // 统计每小时的活动次数
        int lateNightCount = 0;
        int maxCount = 0;
        String peakHour = "00:00";
        
        for (UserActivityLog log : logs) {
            int hour = log.getEventTimestamp().getHour();
            String hourStr = String.format("%02d:00", hour);
            
            // 更新计数
            int count = hourlyDistribution.get(hourStr) + 1;
            hourlyDistribution.put(hourStr, count);
            
            // 检查是否为深夜时段
            if (hour >= 22 || hour < 6) {
                lateNightCount++;
            }
            
            // 更新高峰时段
            if (count > maxCount) {
                maxCount = count;
                peakHour = hourStr;
            }
        }
        
        vo.setHourlyDistribution(hourlyDistribution);
        vo.setPeakHour(peakHour);
        vo.setLateNightCount(lateNightCount);
        
        return vo;
    }
    
    /**
     * 获取用户行为综合分析报告
     */
    @Override
    public BehaviorReportVO getUserBehaviorReport(String userId) {
        BehaviorReportVO vo = new BehaviorReportVO();
        
        // 设置平均每日使用时长（分钟）
        UsageDurationVO usageDuration = getUsageDuration(userId, 7);
        int avgMinutes = (int) (usageDuration.getAvgDailyDuration() / 60);
        vo.setAvgDailyUsage(avgMinutes);
        
        // 设置深夜使用天数
        int lateNightDays = getLateNightActivityDays(userId, 7);
        vo.setLateNightDays(lateNightDays);
        
        // 设置最常用功能
        FeaturePreferenceVO featurePreference = getFeaturePreference(userId, 7);
        vo.setTopFeature(featurePreference.getMostUsedFeature());
        
        // 设置最后活跃时间
        LambdaQueryWrapper<UserActivityLog> wrapper = Wrappers.<UserActivityLog>lambdaQuery()
                .eq(UserActivityLog::getUserId, userId)
                .orderByDesc(UserActivityLog::getEventTimestamp)
                .last("LIMIT 1");
        
        UserActivityLog lastLog = userActivityLogMapper.selectOne(wrapper);
        if (lastLog != null) {
            Date lastActiveTime = Date.from(lastLog.getEventTimestamp().atZone(ZoneId.systemDefault()).toInstant());
            vo.setLastActiveTime(lastActiveTime);
        } else {
            vo.setLastActiveTime(new Date());
        }
        
        // 设置关怀提示
        boolean needCare = lateNightDays > 3 || avgMinutes > 120;
        vo.setNeedCarePrompt(needCare);
        
        if (needCare) {
            if (lateNightDays > 3) {
                vo.setCarePromptType(1); // 深夜使用提醒类型
                vo.setCarePromptContent("我们注意到您最近经常在深夜使用应用，请注意休息，保持良好的作息习惯有利于心理健康。");
            } else {
                vo.setCarePromptType(2); // 使用时长提醒类型
                vo.setCarePromptContent("您的每日使用时长较长，建议适当控制使用时间，多参与户外活动和社交互动。");
            }
        }
        
        return vo;
    }
}