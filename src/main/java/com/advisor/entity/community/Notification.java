package com.advisor.entity.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知实体类
 */
@Data
@TableName("notification")
public class Notification {
    
    /**
     * 通知ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
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