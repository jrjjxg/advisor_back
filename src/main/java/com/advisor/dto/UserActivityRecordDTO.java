package com.advisor.dto;

import lombok.Data;

/**
 * 用户活动记录数据传输对象
 */
@Data
public class UserActivityRecordDTO {
    
    /**
     * 事件类型，如点击、浏览、停留等
     */
    private String eventType;
    
    /**
     * 功能名称
     */
    private String featureName;
    
    /**
     * 页面路径
     */
    private String pagePath;
    
    /**
     * 停留时长（毫秒）
     */
    private Long durationMs;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * 操作系统信息
     */
    private String osInfo;
    
    /**
     * 浏览器信息
     */
    private String browserInfo;
    
    /**
     * 事件详情，JSON格式
     */
    private String eventDetails;
} 