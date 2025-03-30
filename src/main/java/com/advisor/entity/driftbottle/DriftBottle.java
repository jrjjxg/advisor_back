package com.advisor.entity.driftbottle;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("drift_bottle")
public class DriftBottle {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String userId;
    private String content;
    private String images;
    private Integer isAnonymous;
    private Integer status;
    private String auditUserId;
    private LocalDateTime auditTime;
    private String auditReason;
    private String pickUserId;
    private LocalDateTime pickTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 