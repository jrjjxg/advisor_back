package com.advisor.entity.alert;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 紧急联系人实体
 */
@Data
@TableName("emergency_contact")
public class EmergencyContact {
    @TableId
    private String id;
    
    private String userId;
    
    private String name;
    
    private String relationship;
    
    private String phone;
    
    private String email;
    
    private Integer notifyThreshold; // 通知阈值
    
    private Boolean isPrimary; // 是否主要联系人
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 