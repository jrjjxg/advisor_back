package com.advisor.vo.community;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论视图对象
 */
@Data
public class CommentVO {
    
    /**
     * 评论ID
     */
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
     * 用户名
     */
    private String username;
    
    /**
     * 用户头像
     */
    private String avatar;
    
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
     * 回复用户名
     */
    private String replyUsername;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 是否已点赞
     */
    private Boolean isLiked;
    
    /**
     * 子评论列表
     */
    private List<CommentVO> children;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    // 评论用户信息
    private String nickname;
    private Boolean isExpert;
    
    // 回复信息
    private String replyNickname;
    
    // 回复列表
    private List<CommentVO> replies;
    private Integer replyCount;
    private Boolean hasMoreReplies;
}