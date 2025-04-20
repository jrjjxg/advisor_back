package com.advisor.service.impl;

import com.advisor.entity.userbehavior.UserLoginLog;
import com.advisor.mapper.userbehavior.UserLoginLogMapper;
import com.advisor.service.userbehavior.UserLoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户登录日志Service实现类
 */
@Service
public class UserLoginLogServiceImpl extends ServiceImpl<UserLoginLogMapper, UserLoginLog> implements UserLoginLogService {

    @Override
    public void recordLoginSuccess(String userId, String ipAddress) {
        UserLoginLog log = new UserLoginLog();
        log.setUserId(userId);
        log.setIpAddress(ipAddress);
        log.setLoginTime(LocalDateTime.now());
        log.setLoginStatus(1); // 成功
        save(log);
    }

    @Override
    public void recordLoginFailed(String userId, String ipAddress, String failReason) {
        UserLoginLog log = new UserLoginLog();
        log.setUserId(userId);
        log.setIpAddress(ipAddress);
        log.setLoginTime(LocalDateTime.now());
        log.setLoginStatus(0); // 失败
        log.setFailReason(failReason);
        save(log);
    }
} 