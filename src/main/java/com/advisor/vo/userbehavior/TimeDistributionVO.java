package com.advisor.vo.userbehavior;

import java.util.Map;

/**
 * 使用时间分布数据
 */
public class TimeDistributionVO {
    
    /**
     * 各时段使用次数
     * 格式：{时段: 次数}，例如：{"00:00": 5, "01:00": 2}
     */
    private Map<String, Integer> hourlyDistribution;
    
    /**
     * 高峰使用时段
     */
    private String peakHour;
    
    /**
     * 深夜使用次数（23:00-06:00）
     */
    private Integer lateNightCount;

    public Map<String, Integer> getHourlyDistribution() {
        return hourlyDistribution;
    }

    public void setHourlyDistribution(Map<String, Integer> hourlyDistribution) {
        this.hourlyDistribution = hourlyDistribution;
    }

    public String getPeakHour() {
        return peakHour;
    }

    public void setPeakHour(String peakHour) {
        this.peakHour = peakHour;
    }

    public Integer getLateNightCount() {
        return lateNightCount;
    }

    public void setLateNightCount(Integer lateNightCount) {
        this.lateNightCount = lateNightCount;
    }
} 