package com.advisor.dto.community;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentCreateRequest {
    
    @NotBlank(message = "帖子ID不能为空")
    private String postId;
    
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500个字符")
    private String content;
    
    private String parentId;
    
    private String replyUserId;
}