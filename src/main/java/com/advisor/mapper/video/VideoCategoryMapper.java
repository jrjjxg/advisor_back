package com.advisor.mapper.video;

import com.advisor.entity.video.VideoCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

// 视频分类Mapper
@Mapper
public interface VideoCategoryMapper extends BaseMapper<VideoCategory> {
}