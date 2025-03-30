package com.advisor.vo.driftbottle;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DriftBottleReplyVO {
    private String id;
    private String bottleId;
    private String content;
    private LocalDateTime createTime;
    
    // 回复用户信息
    private DriftBottleVO.UserInfo author;
} 