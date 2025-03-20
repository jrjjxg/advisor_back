package com.advisor.entity.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子实体类
 */
@Data
@TableName("post")
public class Post {
    
    /**
     * 帖子ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 帖子内容
     */
    private String content;
    
    /**
     * 图片URL，多个用逗号分隔
     */
    private String images;
    
    /**
     * 位置信息
     */
    private String location;
    
    /**
     * 情绪记录ID
     */
    private Long moodRecordId;
    
    /**
     * 测试结果ID
     */
    private String testResultId;
    
    /**
     * 浏览数
     */
    private Integer viewCount;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 评论数
     */
    private Integer commentCount;
    
    /**
     * 是否匿名：0-否，1-是
     */
    private Integer isAnonymous;
    
    /**
     * 状态：0-删除，1-正常
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}