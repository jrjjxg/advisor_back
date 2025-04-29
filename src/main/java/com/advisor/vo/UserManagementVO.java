package com.advisor.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户管理列表视图对象
 */
@Data
public class UserManagementVO {
    private String id;
    private String username;
    private String nickname;
    private String email;
    private Integer status; // 0-禁用, 1-正常
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;
    private String avatar; // 添加头像字段
} 