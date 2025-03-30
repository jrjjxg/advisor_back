package com.advisor.mapper.media;

import com.advisor.entity.media.MediaResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 媒体资源Mapper接口
 */
@Mapper
public interface MediaResourceMapper extends BaseMapper<MediaResource> {
    
    /**
     * 增加播放次数
     * @param id 媒体ID
     * @return 影响行数
     */
    @Update("UPDATE media_resource SET views = views + 1 WHERE id = #{id}")
    int incrementViews(@Param("id") String id);
    
    /**
     * 增加点赞数
     * @param id 媒体ID
     * @return 影响行数
     */
    @Update("UPDATE media_resource SET likes = likes + 1 WHERE id = #{id}")
    int incrementLikes(@Param("id") String id);
    
    /**
     * 减少点赞数
     * @param id 媒体ID
     * @return 影响行数
     */
    @Update("UPDATE media_resource SET likes = likes - 1 WHERE id = #{id} AND likes > 0")
    int decrementLikes(@Param("id") String id);
} 