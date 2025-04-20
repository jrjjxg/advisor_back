package com.advisor.entity.userbehavior;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户登录日志实体类
 */
@Data
@TableName("user_login_log")
public class UserLoginLog {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 登录IP
     */
    private String ipAddress;
    
    /**
     * 登录时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime loginTime;
    
    /**
     * 登录状态：1-成功，0-失败
     */
    private Integer loginStatus;
    
    /**
     * 失败原因
     */
    private String failReason;
} 