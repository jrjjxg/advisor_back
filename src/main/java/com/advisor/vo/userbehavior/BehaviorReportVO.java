package com.advisor.vo.userbehavior;

import lombok.Data;
import java.util.Date;

/**
 * 用户行为报告视图对象
 */
@Data
public class BehaviorReportVO {
    
    /**
     * 平均每日使用时长（分钟）
     */
    private Integer avgDailyUsage;
    
    /**
     * 深夜使用天数
     */
    private Integer lateNightDays;
    
    /**
     * 最常用功能
     */
    private String topFeature;
    
    /**
     * 最后活跃时间
     */
    private Date lastActiveTime;
    
    /**
     * 是否需要显示关怀提示
     */
    private Boolean needCarePrompt = false;
    
    /**
     * 关怀提示类型
     * 1: 深夜使用提醒
     * 2: 使用时长提醒
     * 3: 其他类型提醒
     */
    private Integer carePromptType;
    
    /**
     * 关怀提示内容
     */
    private String carePromptContent;
} 