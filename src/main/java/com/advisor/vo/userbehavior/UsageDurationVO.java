package com.advisor.vo.userbehavior;

import java.util.Map;

/**
 * 使用时长统计数据
 */
public class UsageDurationVO {
    
    /**
     * 每日使用时长统计（毫秒）
     * 格式：{日期: 使用时长}，例如：{"2023-06-01": 3600000}
     */
    private Map<String, Long> dailyUsage;
    
    /**
     * 总使用时长（毫秒）
     */
    private Long totalDuration;
    
    /**
     * 平均每日使用时长（毫秒）
     */
    private Long avgDailyDuration;

    public Map<String, Long> getDailyUsage() {
        return dailyUsage;
    }

    public void setDailyUsage(Map<String, Long> dailyUsage) {
        this.dailyUsage = dailyUsage;
    }

    public Long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Long getAvgDailyDuration() {
        return avgDailyDuration;
    }

    public void setAvgDailyDuration(Long avgDailyDuration) {
        this.avgDailyDuration = avgDailyDuration;
    }
} 