package com.advisor.service.video;

import com.advisor.entity.video.VideoCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

// 视频分类服务接口
public interface VideoCategoryService extends IService<VideoCategory> {
    List<VideoCategory> listAllCategories();
}
