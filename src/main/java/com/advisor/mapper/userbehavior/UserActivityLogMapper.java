package com.advisor.mapper.userbehavior;

import com.advisor.entity.userbehavior.UserActivityLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 用户活动日志Mapper接口
 */
@Mapper
public interface UserActivityLogMapper extends BaseMapper<UserActivityLog> {
    
    /**
     * 统计用户在指定天数内的活动总次数
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @return 活动次数
     */
    @Select("SELECT COUNT(*) FROM user_activity_log " +
            "WHERE user_id = #{userId} " +
            "AND event_timestamp >= DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int countUserRecentActivity(@Param("userId") String userId, @Param("days") int days);
    
    /**
     * 统计用户夜间活动天数
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @param startHour 夜间开始小时（如22点）
     * @param endHour 夜间结束小时（如6点）
     * @return 夜间活动天数
     */
    @Select("SELECT COUNT(DISTINCT DATE(event_timestamp)) FROM user_activity_log " +
            "WHERE user_id = #{userId} " +
            "AND event_timestamp >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "AND (HOUR(event_timestamp) >= #{startHour} OR HOUR(event_timestamp) < #{endHour})")
    int countUserNightActivity(@Param("userId") String userId, 
                              @Param("days") int days, 
                              @Param("startHour") int startHour, 
                              @Param("endHour") int endHour);
    
    /**
     * 统计用户每日使用时长
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @return 每日使用时长
     */
    @Select("SELECT DATE(event_timestamp) as date, SUM(duration_sec) as duration " +
            "FROM user_activity_log " +
            "WHERE user_id = #{userId} " +
            "AND event_timestamp >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(event_timestamp)")
    List<Object[]> getDailyUsageDuration(@Param("userId") String userId, @Param("days") int days);
    
    /**
     * 统计用户功能使用情况
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @return 功能使用统计
     */
    @Select("SELECT feature_name, COUNT(*) as count, SUM(duration_sec) as duration " +
            "FROM user_activity_log " +
            "WHERE user_id = #{userId} " +
            "AND event_timestamp >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "AND feature_name IS NOT NULL " +
            "GROUP BY feature_name " +
            "ORDER BY count DESC")
    List<Object[]> getFeatureUsageStats(@Param("userId") String userId, @Param("days") int days);
    
    /**
     * 统计用户活动时间分布
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @return 时间分布统计
     */
    @Select("SELECT HOUR(event_timestamp) as hour, COUNT(*) as count " +
            "FROM user_activity_log " +
            "WHERE user_id = #{userId} " +
            "AND event_timestamp >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY HOUR(event_timestamp) " +
            "ORDER BY hour")
    List<Object[]> getHourlyDistribution(@Param("userId") String userId, @Param("days") int days);
} 