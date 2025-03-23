package com.advisor.service.impl.video;

import com.advisor.entity.video.VideoCategory;
import com.advisor.mapper.video.VideoCategoryMapper;
import com.advisor.service.video.VideoCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

// 视频分类服务实现
@Service
public class VideoCategoryServiceImpl extends ServiceImpl<VideoCategoryMapper, VideoCategory> implements VideoCategoryService {
    @Override
    public List<VideoCategory> listAllCategories() {
        return this.list(new QueryWrapper<VideoCategory>().orderByAsc("sort"));
    }
}