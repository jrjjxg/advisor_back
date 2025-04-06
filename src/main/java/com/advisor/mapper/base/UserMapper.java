package com.advisor.mapper.base;

import com.advisor.entity.base.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 更新用户帖子数量
     *
     * @param userId 用户ID
     * @param count  增加的数量，可以为负数
     * @return 影响的行数
     */
    @Update("UPDATE user SET post_count = post_count + #{count} WHERE id = #{userId}")
    int updatePostCount(@Param("userId") String userId, @Param("count") int count);
    
    /**
     * 更新用户关注数量
     *
     * @param userId 用户ID
     * @param count  增加的数量，可以为负数
     * @return 影响的行数
     */
    @Update("UPDATE user SET follow_count = follow_count + #{count} WHERE id = #{userId}")
    int updateFollowCount(@Param("userId") String userId, @Param("count") int count);
    
    /**
     * 更新用户粉丝数量
     *
     * @param userId 用户ID
     * @param count  增加的数量，可以为负数
     * @return 影响的行数
     */
    @Update("UPDATE user SET fans_count = fans_count + #{count} WHERE id = #{userId}")
    int updateFansCount(@Param("userId") String userId, @Param("count") int count);
    
    /**
     * 更新用户点赞未读数量
     *
     * @param userId 用户ID
     * @param count  增加的数量，可以为负数
     * @return 影响的行数
     */
    @Update("UPDATE user SET like_unread = like_unread + #{count} WHERE id = #{userId}")
    int updateLikeUnread(@Param("userId") String userId, @Param("count") int count);
    
    /**
     * 更新用户评论未读数量
     *
     * @param userId 用户ID
     * @param count  增加的数量，可以为负数
     * @return 影响的行数
     */
    @Update("UPDATE user SET comment_unread = comment_unread + #{count} WHERE id = #{userId}")
    int updateCommentUnread(@Param("userId") String userId, @Param("count") int count);
    
    /**
     * 更新用户私信未读数量
     *
     * @param userId 用户ID
     * @param count  增加的数量，可以为负数
     * @return 影响的行数
     */
    @Update("UPDATE user SET letter_unread = letter_unread + #{count} WHERE id = #{userId}")
    int updateLetterUnread(@Param("userId") String userId, @Param("count") int count);
    
    /**
     * 更新用户通知未读数量
     *
     * @param userId 用户ID
     * @param count  增加的数量，可以为负数
     * @return 影响的行数
     */
    @Update("UPDATE user SET notification_unread = notification_unread + #{count} WHERE id = #{userId}")
    int updateNotificationUnread(@Param("userId") String userId, @Param("count") int count);
    
    /**
     * 获取活跃用户列表
     * 
     * @param startDate 起始日期
     * @return 活跃用户列表
     */
    @Select("SELECT DISTINCT u.* FROM user u " +
            "LEFT JOIN mood_record mr ON u.id = mr.user_id " +
            "LEFT JOIN user_journal j ON u.id = j.user_id " +
            "LEFT JOIN test_result tr ON u.id = tr.user_id " +
            "WHERE mr.create_time >= #{startDate} OR j.create_time >= #{startDate} OR tr.create_time >= #{startDate}")
    List<User> getActiveUsers(@Param("startDate") LocalDateTime startDate);
}