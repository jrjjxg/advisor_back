package com.advisor.entity.userbehavior;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户活动日志实体类
 */
@Data
@TableName("user_activity_log")
public class UserActivityLog {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;
    
    /**
     * 事件类型，如点击、浏览、停留等
     */
    @TableField("event_type")
    private String eventType;
    
    /**
     * 事件发生时间
     */
    @TableField("event_timestamp")
    private LocalDateTime eventTimestamp;
    
    /**
     * 功能名称
     */
    @TableField("feature_name")
    private String featureName;
    
    /**
     * 页面路径 (数据库列名为 page_url)
     */
    @TableField("page_url")
    private String pageUrl;
    
    /**
     * 停留时长（秒）
     */
    @TableField("duration_sec")
    private Integer durationSec;
    
    /**
     * 事件详情，JSON格式
     */
    @TableField("event_details")
    private String eventDetails;
} 