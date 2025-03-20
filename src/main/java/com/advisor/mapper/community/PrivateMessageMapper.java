package com.advisor.mapper.community;

import com.advisor.entity.community.PrivateMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 私信Mapper
 */
@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessage> {
    
    /**
     * 将消息标记为已读
     */
    @Update("UPDATE private_message SET is_read = 1 WHERE id = #{messageId} AND to_user_id = #{userId}")
    int markAsRead(@Param("messageId") String messageId, @Param("userId") String userId);
    
    /**
     * 将与指定用户的所有消息标记为已读
     */
    @Update("UPDATE private_message SET is_read = 1 WHERE to_user_id = #{userId} AND from_user_id = #{fromUserId} AND is_read = 0")
    int markAllAsRead(@Param("userId") String userId, @Param("fromUserId") String fromUserId);
}