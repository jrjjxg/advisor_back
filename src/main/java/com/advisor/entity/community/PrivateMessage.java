package com.advisor.entity.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 私信实体类
 */
@Data
@TableName("private_message")
public class PrivateMessage {
    
    /**
     * 私信ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 发送用户ID
     */
    private String fromUserId;
    
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
     * 创建时间
     */
    private LocalDateTime createTime;
}