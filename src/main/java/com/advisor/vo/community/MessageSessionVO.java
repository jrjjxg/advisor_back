package com.advisor.vo.community;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息会话视图对象
 */
@Data
public class MessageSessionVO {
    
    /**
     * 会话用户ID
     */
    private String userId;
    
    /**
     * 会话用户名
     */
    private String username;
    
    /**
     * 会话用户头像
     */
    private String avatar;
    
    /**
     * 最新消息内容
     */
    private String lastMessage;
    
    /**
     * 未读消息数
     */
    private Integer unreadCount;
    
    /**
     * 最后消息时间
     */
    private LocalDateTime lastTime;
}