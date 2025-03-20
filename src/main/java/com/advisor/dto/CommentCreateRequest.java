package com.advisor.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 创建评论请求
 */
@Data
public class CommentCreateRequest {
    
    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private String postId;
    
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500字")
    private String content;
    
    /**
     * 父评论ID
     */
    private String parentId;
    
    /**
     * 回复用户ID
     */
    private String replyUserId;
}