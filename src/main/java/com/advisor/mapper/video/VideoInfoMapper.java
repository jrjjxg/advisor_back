package com.advisor.mapper.video;

import com.advisor.entity.video.VideoInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// 视频信息Mapper
@Mapper
public interface VideoInfoMapper extends BaseMapper<VideoInfo> {
    @Select("SELECT v.*, c.name as categoryName FROM video_info v " +
            "LEFT JOIN video_category c ON v.category_id = c.id " +
            "WHERE v.status = 1 AND v.category_id = #{categoryId} " +
            "ORDER BY v.create_time DESC")
    List<VideoInfo> selectByCategoryId(@Param("categoryId") String categoryId);
}
