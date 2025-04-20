package com.advisor.service.userbehavior;

import com.advisor.vo.userbehavior.BehaviorReportVO;
import com.advisor.vo.userbehavior.FeaturePreferenceVO;
import com.advisor.vo.userbehavior.TimeDistributionVO;
import com.advisor.vo.userbehavior.UsageDurationVO;

/**
 * 用户行为分析服务接口
 */
public interface BehaviorAnalysisService {
    
    /**
     * 获取用户深夜活动天数
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @return 深夜活动天数
     */
    int getLateNightActivityDays(String userId, int days);
    
    /**
     * 获取用户使用时长统计
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @return 使用时长数据
     */
    UsageDurationVO getUsageDuration(String userId, int days);
    
    /**
     * 获取用户功能偏好分析
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @return 功能偏好数据
     */
    FeaturePreferenceVO getFeaturePreference(String userId, int days);
    
    /**
     * 获取用户使用时间分布
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @return 时间分布数据
     */
    TimeDistributionVO getTimeDistribution(String userId, int days);
    
    /**
     * 获取用户行为综合分析报告
     * 
     * @param userId 用户ID
     * @return 行为分析报告
     */
    BehaviorReportVO getUserBehaviorReport(String userId);
} 