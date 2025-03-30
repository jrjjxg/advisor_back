package com.advisor.mapper.media;

import com.advisor.entity.media.MediaCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 媒体分类Mapper接口
 */
@Mapper
public interface MediaCategoryMapper extends BaseMapper<MediaCategory> {
    
    /**
     * 根据媒体类型获取分类列表
     * @param mediaType 媒体类型：1=视频，2=音频
     * @return 分类列表
     */
    @Select("SELECT * FROM media_category WHERE media_type = #{mediaType} ORDER BY sort ASC")
    List<MediaCategory> selectListByMediaType(Integer mediaType);
} 