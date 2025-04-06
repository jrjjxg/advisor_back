package com.advisor.entity.alert;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户预警设置
 */
@Data
@TableName("alert_setting")
public class UserAlertSetting {
    @TableId
    private String userId;
    
    private Boolean enableSelfAlert; // 启用自我预警
    
    private Boolean enableEmergencyAlert; // 启用紧急联系人预警
    
    private Integer emergencyThreshold; // 紧急通知阈值
    
    private LocalDateTime consentTime; // 同意时间
    
    private String consentIp; // 同意IP
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 