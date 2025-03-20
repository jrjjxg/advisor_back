package com.advisor.vo.community;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDetailVO {
    private String id;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer isAnonymous;
    private LocalDateTime createTime;
    
    // 作者信息
    private AuthorVO author;
    
    // 标签
    private List<String> tags;
    
    // 图片
    private List<String> images;
    
    // 来源信息
    private SourceVO source;
    
    // 当前用户是否点赞
    private Boolean isLiked;
    
    @Data
    public static class AuthorVO {
        private String id;
        private String username;
        private String nickname;
        private String avatar;
        private Boolean isExpert;
        private Boolean isFollowed;
    }
    
    @Data
    public static class SourceVO {
        private String type; // mood, test
        private String id;
        private String title;
        private String content;
    }
}