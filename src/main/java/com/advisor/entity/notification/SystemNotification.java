package com.advisor.entity.notification;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统通知实体
 */
@Data
@TableName("system_notification")
public class SystemNotification {
    
    @TableId
    private String id;
    
    /**
     * 接收用户ID
     */
    private String userId;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 通知类型：SYSTEM, ALERT, INFO, etc.
     */
    private String type;
    
    /**
     * 附加数据（JSON格式）
     */
    private String data;
    
    /**
     * 是否已读
     */
    private Boolean isRead;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 