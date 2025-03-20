package com.advisor.entity.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 关注关系实体类
 */
@Data
@TableName("follow_relation")
public class FollowRelation {
    
    /**
     * 关注关系ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 关注的用户ID
     */
    private String followUserId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}