package com.advisor.controller.video;

import com.advisor.common.Result;
import com.advisor.entity.video.VideoCategory;
import com.advisor.service.video.VideoCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 视频分类控制器
@RestController
@RequestMapping("/api/videos/categories")
public class VideoCategoryController {
    @Autowired
    private VideoCategoryService videoCategoryService;
    
    @GetMapping
    public Result getCategories() {
        List<VideoCategory> categories = videoCategoryService.listAllCategories();
        return Result.success(categories);
    }
}