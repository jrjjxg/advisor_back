package com.advisor.service.userbehavior;

import com.advisor.entity.userbehavior.UserActivityLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户活动日志Service接口
 */
public interface UserActivityLogService extends IService<UserActivityLog> {
    
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

    /**
     * 获取用户近期活跃情况
     * 
     * @param userId 用户ID
     * @param days 天数
     * @return 活跃记录数
     */
    int getUserRecentActivityCount(String userId, int days);
    
    /**
     * 检查用户是否在深夜活跃
     * 
     * @param userId 用户ID
     * @param days 检查的天数范围
     * @param startHour 深夜开始小时
     * @param endHour 深夜结束小时
     * @param minTimes 最少次数
     * @return 是否频繁深夜活跃
     */
    boolean isUserActiveAtNight(String userId, int days, int startHour, int endHour, int minTimes);
}