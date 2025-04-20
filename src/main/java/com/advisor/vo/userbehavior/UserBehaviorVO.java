package com.advisor.vo.userbehavior;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户行为分析VO类
 */
@Data
public class UserBehaviorVO {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 最近登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 最近活跃时间
     */
    private LocalDateTime lastActiveTime;
    
    /**
     * 深夜活跃天数（最近7天）
     */
    private Integer lateNightDays;
    
    /**
     * 平均每日使用时长（分钟）
     */
    private Double avgDailyUsage;
    
    /**
     * 常用功能TOP1
     */
    private String topFeature;
    
    /**
     * 是否需要关怀提示
     */
    private Boolean needCarePrompt;
    
    /**
     * 关怀提示内容
     */
    private String carePromptContent;
    
    /**
     * 关怀提示类型: 1-深夜使用提醒, 2-活跃度异常提醒, 3-功能使用异常提醒
     */
    private Integer carePromptType;
}