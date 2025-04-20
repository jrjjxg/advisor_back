package com.advisor.service.impl;

import com.advisor.entity.userbehavior.UserActivityLog;
import com.advisor.mapper.userbehavior.UserActivityLogMapper;
import com.advisor.service.userbehavior.UserActivityLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户活动日志Service实现类
 */
@Service
public class UserActivityLogServiceImpl extends ServiceImpl<UserActivityLogMapper, UserActivityLog> implements UserActivityLogService {

    @Autowired UserActivityLogMapper userActivityLogMapper;
    @Override
    public void recordLoginSuccess(String userId, String ipAddress) {
        UserActivityLog log = new UserActivityLog();
        log.setUserId(userId);
        log.setEventType("LOGIN");
        log.setEventTimestamp(LocalDateTime.now());
        log.setFeatureName("user_login");
        save(log);
    }

    @Override
    public void recordLoginFailed(String userId, String ipAddress, String failReason) {
        UserActivityLog log = new UserActivityLog();
        log.setUserId(userId);
        log.setEventType("LOGIN_FAILED");
        log.setEventTimestamp(LocalDateTime.now());
        log.setFeatureName("user_login");
        log.setEventDetails("{\"failReason\":" + (failReason == null ? "null" : "\"" + failReason + "\"") + "}");
        save(log);
    }

    @Override
    public int getUserRecentActivityCount(String userId, int days) {
        return userActivityLogMapper.countUserRecentActivity(userId, days);
    }

    @Override
    public boolean isUserActiveAtNight(String userId, int days, int startHour, int endHour, int minTimes) {
        int nightActivityCount = userActivityLogMapper.countUserNightActivity(userId, days, startHour, endHour);
        return nightActivityCount >= minTimes;
    }
}