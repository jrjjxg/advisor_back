package com.advisor.entity.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@TableName("comment")
public class Comment {
    
    /**
     * 评论ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 帖子ID
     */
    private String postId;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 父评论ID
     */
    private String parentId;
    
    /**
     * 回复用户ID
     */
    private String replyUserId;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 状态：0-删除，1-正常
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}