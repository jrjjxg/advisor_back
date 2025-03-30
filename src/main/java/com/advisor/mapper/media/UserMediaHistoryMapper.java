package com.advisor.mapper.media;

import com.advisor.entity.media.UserMediaHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户媒体历史Mapper接口
 */
@Mapper
public interface UserMediaHistoryMapper extends BaseMapper<UserMediaHistory> {
    
    /**
     * 查询用户是否有指定媒体的历史记录
     * @param userId 用户ID
     * @param mediaId 媒体ID
     * @return 历史记录
     */
    @Select("SELECT * FROM user_media_history WHERE user_id = #{userId} AND media_id = #{mediaId} LIMIT 1")
    UserMediaHistory selectByUserIdAndMediaId(@Param("userId") String userId, @Param("mediaId") String mediaId);
} 