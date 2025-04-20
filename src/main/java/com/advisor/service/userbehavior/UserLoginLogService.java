package com.advisor.service.userbehavior;

import com.advisor.entity.userbehavior.UserLoginLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户登录日志Service接口
 */
public interface UserLoginLogService extends IService<UserLoginLog> {
    
    /**
     * 记录登录成功日志
     * 
     * @param userId 用户ID
     * @param ipAddress IP地址
     */
    void recordLoginSuccess(String userId, String ipAddress);
    
    /**
     * 记录登录失败日志
     * 
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @param failReason 失败原因
     */
    void recordLoginFailed(String userId, String ipAddress, String failReason);
} 