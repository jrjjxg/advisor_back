package com.advisor.dto.driftbottle;

import lombok.Data;

@Data
public class BottleAuditRequest {
    private String bottleId;
    private Integer status; // 1-通过，3-拒绝
    private String reason;  // 拒绝原因
} 