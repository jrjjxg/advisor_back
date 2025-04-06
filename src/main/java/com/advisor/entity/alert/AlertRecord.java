package com.advisor.entity.alert;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预警记录实体
 */
@Data
@TableName("mental_health_alert")
public class AlertRecord {
    @TableId
    private String id;
    
    private String userId;
    
    private String dataSourceId;
    
    private String dataSourceType;
    
    private Integer riskLevel;
    
    private String ruleId;
    
    private String ruleName;
    
    private String content;
    
    private String suggestion;
    
    private Boolean isNotified;
    
    private Boolean isEmergencyNotified;
    
    private String handledBy;
    
    private Integer status; // 0-未处理，1-已通知，2-已干预，3-已解决
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 