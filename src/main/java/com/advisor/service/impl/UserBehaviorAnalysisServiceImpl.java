package com.advisor.service.impl;

import com.advisor.entity.userbehavior.UserActivityLog;
import com.advisor.entity.base.User;
import com.advisor.mapper.userbehavior.UserActivityLogMapper;
import com.advisor.service.userbehavior.UserActivityLogService;
import com.advisor.service.userbehavior.UserBehaviorAnalysisService;
import com.advisor.service.UserService;
import com.advisor.vo.userbehavior.UserBehaviorVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户行为分析服务实现类
 */
@Service
public class UserBehaviorAnalysisServiceImpl implements UserBehaviorAnalysisService {

    @Autowired
    private UserActivityLogService userActivityLogService;
    
    @Autowired
    private UserActivityLogMapper userActivityLogMapper;
    
    @Autowired
    private UserService userService;
    
    @Override
    public Map<String, Object> analyzeLateNightActivity(String userId, int days) {
        Map<String, Object> result = new HashMap<>();
        
        // 定义深夜时间范围（22:00 - 06:00）
        int startHour = 22;
        int endHour = 6;
        
        // 查询在指定天数内用户深夜活动情况
        int nightActivityDays = userActivityLogMapper.countUserNightActivity(userId, days, startHour, endHour);
        
        // 查询每个小时的活动次数
        Map<Integer, Integer> hourlyActivity = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            hourlyActivity.put(i, 0);
        }
        
        // 查询最近n天的深夜活动日志
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<UserActivityLog> wrapper = Wrappers.<UserActivityLog>lambdaQuery()
                .eq(UserActivityLog::getUserId, userId)
                .ge(UserActivityLog::getEventTimestamp, startTime);
        
        List<UserActivityLog> logs = userActivityLogMapper.selectList(wrapper);
        
        // 统计每小时活动次数
        for (UserActivityLog log : logs) {
            int hour = log.getEventTimestamp().getHour();
            hourlyActivity.put(hour, hourlyActivity.get(hour) + 1);
        }
        
        // 计算深夜活动比例
        int totalActivities = logs.size();
        int nightActivities = 0;
        for (int i = 0; i < 24; i++) {
            if (i >= startHour || i < endHour) {
                nightActivities += hourlyActivity.get(i);
            }
        }
        
        double nightActivityRatio = totalActivities > 0 ? (double) nightActivities / totalActivities : 0;
        
        // 组装结果
        result.put("nightActivityDays", nightActivityDays);
        result.put("totalDays", days);
        result.put("nightActivityRatio", nightActivityRatio);
        result.put("hourlyActivity", hourlyActivity);
        result.put("isFrequentNightUser", nightActivityDays >= 3); // 如果7天内有3天或以上深夜活动，认为是频繁深夜用户
        
        return result;
    }

    @Override
    public Map<String, Object> getUserUsageDuration(String userId, int days) {
        Map<String, Object> result = new HashMap<>();
        
        // 查询最近n天的活动日志
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<UserActivityLog> wrapper = Wrappers.<UserActivityLog>lambdaQuery()
                .eq(UserActivityLog::getUserId, userId)
                .ge(UserActivityLog::getEventTimestamp, startTime)
                .isNotNull(UserActivityLog::getDurationSec);
        
        List<UserActivityLog> logs = userActivityLogMapper.selectList(wrapper);
        
        // 计算总使用时长
        long totalDurationMs = logs.stream()
                .filter(log -> log.getDurationSec() != null)
                .mapToLong(UserActivityLog::getDurationSec)
                .sum();
        
        // 按天统计使用时长
        Map<String, Long> dailyUsage = new HashMap<>();
        for (UserActivityLog log : logs) {
            if (log.getDurationSec() != null) {
                String day = log.getEventTimestamp().toLocalDate().toString();
                dailyUsage.put(day, dailyUsage.getOrDefault(day, 0L) + log.getDurationSec());
            }
        }
        
        // 计算平均每日使用时长
        double avgDailyUsageMs = days > 0 ? (double) totalDurationMs / days : 0;
        
        // 转换为分钟
        double totalDurationMinutes = totalDurationMs / (1000.0 * 60);
        double avgDailyUsageMinutes = avgDailyUsageMs / (1000.0 * 60);
        
        // 组装结果
        result.put("totalDurationMs", totalDurationMs);
        result.put("totalDurationMinutes", totalDurationMinutes);
        result.put("avgDailyUsageMs", avgDailyUsageMs);
        result.put("avgDailyUsageMinutes", avgDailyUsageMinutes);
        result.put("dailyUsage", dailyUsage);
        
        return result;
    }

    @Override
    public Map<String, Object> getUserFeaturePreference(String userId, int days) {
        Map<String, Object> result = new HashMap<>();
        
        // 查询最近n天的活动日志
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<UserActivityLog> wrapper = Wrappers.<UserActivityLog>lambdaQuery()
                .eq(UserActivityLog::getUserId, userId)
                .ge(UserActivityLog::getEventTimestamp, startTime)
                .isNotNull(UserActivityLog::getFeatureName);
        
        List<UserActivityLog> logs = userActivityLogMapper.selectList(wrapper);
        
        // 统计各功能使用次数
        Map<String, Integer> featureUsageCount = new HashMap<>();
        Map<String, Long> featureUsageDuration = new HashMap<>();
        
        for (UserActivityLog log : logs) {
            if (log.getFeatureName() != null) {
                // 统计使用次数
                featureUsageCount.put(log.getFeatureName(), featureUsageCount.getOrDefault(log.getFeatureName(), 0) + 1);
                
                // 统计使用时长
                if (log.getDurationSec() != null) {
                    featureUsageDuration.put(log.getFeatureName(), 
                            featureUsageDuration.getOrDefault(log.getFeatureName(), 0L) + log.getDurationSec());
                }
            }
        }
        
        // 按使用次数排序
        List<Map.Entry<String, Integer>> sortedFeatures = featureUsageCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
        
        // 提取前5个最常用功能
        List<Map<String, Object>> topFeatures = new ArrayList<>();
        for (int i = 0; i < Math.min(5, sortedFeatures.size()); i++) {
            Map.Entry<String, Integer> entry = sortedFeatures.get(i);
            Map<String, Object> featureData = new HashMap<>();
            featureData.put("name", entry.getKey());
            featureData.put("count", entry.getValue());
            featureData.put("duration", featureUsageDuration.getOrDefault(entry.getKey(), 0L));
            topFeatures.add(featureData);
        }
        
        // 计算功能偏好比例
        int totalCount = logs.size();
        Map<String, Double> featureUsageRatio = new HashMap<>();
        for (Map.Entry<String, Integer> entry : featureUsageCount.entrySet()) {
            if (totalCount > 0) {
                featureUsageRatio.put(entry.getKey(), (double) entry.getValue() / totalCount);
            }
        }
        
        // 组装结果
        result.put("totalFeatureCount", featureUsageCount.size());
        result.put("topFeatures", topFeatures);
        result.put("featureUsageCount", featureUsageCount);
        result.put("featureUsageDuration", featureUsageDuration);
        result.put("featureUsageRatio", featureUsageRatio);
        
        return result;
    }

    @Override
    public Map<String, Integer> getUserTimeDistribution(String userId, int days) {
        // 查询最近n天的活动日志
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<UserActivityLog> wrapper = Wrappers.<UserActivityLog>lambdaQuery()
                .eq(UserActivityLog::getUserId, userId)
                .ge(UserActivityLog::getEventTimestamp, startTime);
        
        List<UserActivityLog> logs = userActivityLogMapper.selectList(wrapper);
        
        // 按小时分组统计活动次数
        Map<String, Integer> hourlyDistribution = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            String hourKey = String.format("%02d:00", i);
            hourlyDistribution.put(hourKey, 0);
        }
        
        for (UserActivityLog log : logs) {
            int hour = log.getEventTimestamp().getHour();
            String hourKey = String.format("%02d:00", hour);
            hourlyDistribution.put(hourKey, hourlyDistribution.get(hourKey) + 1);
        }
        
        return hourlyDistribution;
    }

    @Override
    public List<UserBehaviorVO> checkAndTriggerCarePrompt() {
        List<UserBehaviorVO> careUsers = new ArrayList<>();
        
        // 获取所有用户
        List<User> allUsers = userService.list();
        
        // 遍历用户，检查是否需要触发关怀提示
        for (User user : allUsers) {
            String userId = user.getId();
            
            // 分析用户深夜活动情况
            Map<String, Object> nightActivity = analyzeLateNightActivity(userId, 7);
            boolean isFrequentNightUser = (boolean) nightActivity.get("isFrequentNightUser");
            
            // 如果是频繁深夜用户，触发关怀提示
            if (isFrequentNightUser) {
                UserBehaviorVO behaviorVO = new UserBehaviorVO();
                behaviorVO.setUserId(userId);
                behaviorVO.setUsername(user.getUsername());
                behaviorVO.setLastLoginTime(user.getLastLoginTime());
                
                // 获取最近一次活动时间
                LambdaQueryWrapper<UserActivityLog> wrapper = Wrappers.<UserActivityLog>lambdaQuery()
                        .eq(UserActivityLog::getUserId, userId)
                        .orderByDesc(UserActivityLog::getEventTimestamp)
                        .last("LIMIT 1");
                UserActivityLog lastLog = userActivityLogMapper.selectOne(wrapper);
                if (lastLog != null) {
                    behaviorVO.setLastActiveTime(lastLog.getEventTimestamp());
                }
                
                behaviorVO.setLateNightDays((Integer) nightActivity.get("nightActivityDays"));
                
                // 获取平均每日使用时长
                Map<String, Object> usageDuration = getUserUsageDuration(userId, 7);
                behaviorVO.setAvgDailyUsage((Double) usageDuration.get("avgDailyUsageMinutes"));
                
                // 获取最常用功能
                Map<String, Object> featurePreference = getUserFeaturePreference(userId, 7);
                List<Map<String, Object>> topFeatures = (List<Map<String, Object>>) featurePreference.get("topFeatures");
                if (topFeatures != null && !topFeatures.isEmpty()) {
                    behaviorVO.setTopFeature((String) topFeatures.get(0).get("name"));
                }
                
                // 设置关怀提示
                behaviorVO.setNeedCarePrompt(true);
                behaviorVO.setCarePromptType(1); // 1-深夜使用提醒
                behaviorVO.setCarePromptContent("我们注意到您最近一周内有多次深夜使用记录，为了您的健康，建议保持良好的作息习惯。");
                
                careUsers.add(behaviorVO);
            }
            
            // 这里可以添加其他类型的关怀提示检查，如活跃度异常、功能使用异常等
        }
        
        return careUsers;
    }
} 