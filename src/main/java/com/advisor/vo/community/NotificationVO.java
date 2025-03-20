package com.advisor.vo.community;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知视图对象
 */
@Data
public class NotificationVO {
    
    /**
     * 通知ID
     */
    private String id;
    
    /**
     * 接收用户ID
     */
    private String userId;
    
    /**
     * 发送用户ID
     */
    private String senderId;
    
    /**
     * 发送用户名
     */
    private String senderName;
    
    /**
     * 发送用户头像
     */
    private String senderAvatar;
    
    /**
     * 类型：like/comment/follow/system
     */
    private String type;
    
    /**
     * 目标ID
     */
    private String targetId;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}