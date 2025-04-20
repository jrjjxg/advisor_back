package com.advisor.service.userbehavior;

import com.advisor.vo.userbehavior.UserBehaviorVO;

import java.util.List;
import java.util.Map;

/**
 * 用户行为分析服务接口
 */
public interface UserBehaviorAnalysisService {
    
    /**
     * 分析用户深夜活动情况
     * 
     * @param userId 用户ID
     * @param days 分析的天数范围
     * @return 深夜活动分析结果
     */
    Map<String, Object> analyzeLateNightActivity(String userId, int days);
    
    /**
     * 获取用户使用时长统计
     * 
     * @param userId 用户ID
     * @param days 分析的天数范围
     * @return 使用时长统计结果
     */
    Map<String, Object> getUserUsageDuration(String userId, int days);
    
    /**
     * 获取用户功能偏好分析
     * 
     * @param userId 用户ID
     * @param days 分析的天数范围
     * @return 功能偏好分析结果
     */
    Map<String, Object> getUserFeaturePreference(String userId, int days);
    
    /**
     * 获取用户使用时间段分布
     * 
     * @param userId 用户ID
     * @param days 分析的天数范围
     * @return 时间段分布结果
     */
    Map<String, Integer> getUserTimeDistribution(String userId, int days);
    
    /**
     * 检查并触发关怀提示
     * 检测用户行为异常，如频繁深夜使用，触发关怀提示
     * 
     * @return 需要关怀提示的用户列表
     */
    List<UserBehaviorVO> checkAndTriggerCarePrompt();
}