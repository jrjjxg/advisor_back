package com.advisor.entity.driftbottle;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("drift_bottle_reply")
public class DriftBottleReply {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String bottleId;
    private String userId;
    private String content;
    private Integer status;
    private LocalDateTime createTime;
} 