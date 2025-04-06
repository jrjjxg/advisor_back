package com.advisor.mapper.notification;

import com.advisor.entity.notification.SystemNotification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 系统通知Mapper接口
 */
@Mapper
public interface SystemNotificationMapper extends BaseMapper<SystemNotification> {
    
    /**
     * 标记用户所有通知为已读
     * @param userId 用户ID
     * @return 更新的记录数
     */
    @Update("UPDATE system_notification SET is_read = 1 WHERE user_id = #{userId} AND is_read = 0")
    int markAllAsRead(@Param("userId") String userId);
} 