package com.advisor.entity.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点赞记录实体类
 */
@Data
@TableName("like_record")
public class LikeRecord {
    
    /**
     * 点赞记录ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 目标ID
     */
    private String targetId;
    
    /**
     * 类型：1-帖子，2-评论
     */
    private Integer type;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}