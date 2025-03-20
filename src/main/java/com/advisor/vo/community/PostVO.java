package com.advisor.vo.community;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子视图对象
 */
@Data
public class PostVO {
    
    /**
     * 帖子ID
     */
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 帖子内容
     */
    private String content;
    
    /**
     * 图片URL列表
     */
    private List<String> images;
    
    /**
     * 位置信息
     */
    private String location;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
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
     * 是否已点赞
     */
    private Boolean isLiked;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}