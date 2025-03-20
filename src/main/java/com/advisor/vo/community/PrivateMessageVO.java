package com.advisor.vo.community;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 私信视图对象
 */
@Data
public class PrivateMessageVO {
    
    /**
     * 消息ID
     */
    private String id;
    
    /**
     * 发送用户ID
     */
    private String fromUserId;
    
    /**
     * 发送用户名
     */
    private String fromUsername;
    
    /**
     * 发送用户头像
     */
    private String fromAvatar;
    
    /**
     * 接收用户ID
     */
    private String toUserId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;
    
    /**
     * 是否是自己发送的
     */
    private Boolean isSelf;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}