package com.advisor.vo.driftbottle;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DriftBottleVO {
    private String id;
    private String userId;
    private String content;
    private List<String> images;
    private Integer isAnonymous;
    private Integer status;
    private String pickUserId;
    private LocalDateTime pickTime;
    private LocalDateTime createTime;
    
    // 发布者信息
    private UserInfo author;
    
    // 回复列表
    private List<DriftBottleReplyVO> replies;
    
    @Data
    public static class UserInfo {
        private String id;
        private String nickname;
        private String avatar;
    }
} 