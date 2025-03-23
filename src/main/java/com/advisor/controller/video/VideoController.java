package com.advisor.controller.video;
import com.advisor.common.Result;
import com.advisor.entity.video.UserViewHistory;
import com.advisor.entity.video.VideoInfo;
import com.advisor.service.video.UserViewHistoryService;
import com.advisor.service.video.VideoInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 视频信息控制器
@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoInfoService videoInfoService;
    
    @Autowired
    private UserViewHistoryService userViewHistoryService;
    
    // 根据分类获取视频列表
    @GetMapping("/category/{categoryId}")
    public Result getVideosByCategory(@PathVariable String categoryId) {
        List<VideoInfo> videos = videoInfoService.getVideosByCategory(categoryId);
        return Result.success(videos);
    }
    
    // 获取视频详情
    @GetMapping("/{videoId}")
    public Result getVideoDetail(@PathVariable String videoId) {
        VideoInfo video = videoInfoService.getVideoDetail(videoId);
        if (video == null) {
            return Result.error("视频不存在");
        }
        // 增加观看次数
        videoInfoService.incrementViewCount(videoId);
        return Result.success(video);
    }
    
    // 获取视频播放地址
    @GetMapping("/url/{videoId}")
    public Result getVideoUrl(@PathVariable String videoId) {
        VideoInfo video = videoInfoService.getById(videoId);
        if (video == null) {
            return Result.error("视频不存在");
        }
        String playUrl = videoInfoService.getVideoPlayUrl(video.getFileKey());
        return Result.success(playUrl);
    }
    
    // 保存观看进度
    @PostMapping("/progress")
    public Result saveProgress(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        String videoId = params.get("videoId").toString();
        Integer progress = Integer.valueOf(params.get("progress").toString());
        
        userViewHistoryService.saveViewProgress(userId, videoId, progress);
        return Result.success();
    }
    
    // 获取用户观看历史
    @GetMapping("/history/{userId}")
    public Result getUserHistory(@PathVariable String userId) {
        List<UserViewHistory> history = userViewHistoryService.getUserViewHistory(userId);
        return Result.success(history);
    }

// 获取视频列表
@GetMapping("")
public Result getVideos(@RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                       @RequestParam(value = "title", required = false) String title,
                       @RequestParam(value = "categoryId", required = false) String categoryId,
                       @RequestParam(value = "status", required = false) Integer status) {
    
    // 构建查询条件
    QueryWrapper<VideoInfo> queryWrapper = new QueryWrapper<>();
    if (StringUtils.isNotBlank(title)) {
        queryWrapper.like("title", title);
    }
    if (StringUtils.isNotBlank(categoryId)) {
        queryWrapper.eq("category_id", categoryId);
    }
    if (status != null) {
        queryWrapper.eq("status", status);
    }
    
    // 分页查询
    Page<VideoInfo> videoPage = new Page<>(page, pageSize);
    Page<VideoInfo> result = videoInfoService.page(videoPage, queryWrapper);
    
    return Result.success(result);
}
}