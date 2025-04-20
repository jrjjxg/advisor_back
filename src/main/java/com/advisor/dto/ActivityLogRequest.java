package com.advisor.dto;

import lombok.Data;

/**
 * 活动日志请求数据传输对象
 */
@Data
public class ActivityLogRequest {
    
    /**
     * 事件类型
     */
    private String event_type;
    
    /**
     * 页面URL
     */
    private String page_url;
    
    /**
     * 功能名称
     */
    private String feature_name;
    
    /**
     * 停留时长（秒）
     */
    private Integer duration_sec;
    
    /**
     * 事件详情，JSON格式
     */
    private String event_details;
} 