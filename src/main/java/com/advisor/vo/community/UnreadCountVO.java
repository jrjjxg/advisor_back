package com.advisor.vo.community;

import lombok.Data;

/**
 * 未读消息数量视图对象
 */
@Data
public class UnreadCountVO {
    
    /**
     * 点赞未读数
     */
    private Integer likeUnread;
    
    /**
     * 评论未读数
     */
    private Integer commentUnread;
    
    /**
     * 私信未读数
     */
    private Integer letterUnread;
    
    /**
     * 通知未读数
     */
    private Integer notificationUnread;
    
    /**
     * 总未读数
     */
    private Integer totalUnread;
}