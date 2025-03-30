package com.advisor.mapper.media;

import com.advisor.entity.media.UserMediaFavorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户媒体收藏Mapper接口
 */
@Mapper
public interface UserMediaFavoriteMapper extends BaseMapper<UserMediaFavorite> {
    
    /**
     * 查询用户是否收藏了指定媒体
     * @param userId 用户ID
     * @param mediaId 媒体ID
     * @return 收藏记录
     */
    @Select("SELECT * FROM user_media_favorite WHERE user_id = #{userId} AND media_id = #{mediaId} LIMIT 1")
    UserMediaFavorite selectByUserIdAndMediaId(@Param("userId") String userId, @Param("mediaId") String mediaId);
} 