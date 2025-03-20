package com.advisor.mapper.community;

import com.advisor.entity.community.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 通知Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    /**
     * 将通知标记为已读
     */
    @Update("UPDATE notification SET is_read = 1 WHERE id = #{notificationId} AND user_id = #{userId}")
    int markAsRead(@Param("notificationId") String notificationId, @Param("userId") String userId);
    
    /**
     * 将所有通知标记为已读
     */
    @Update("UPDATE notification SET is_read = 1 WHERE user_id = #{userId} AND is_read = 0")
    int markAllAsRead(@Param("userId") String userId);
}