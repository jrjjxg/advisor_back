package com.advisor.controller.app;

import com.advisor.common.Result;
import com.advisor.service.media.MediaCategoryService;
import com.advisor.service.media.MediaResourceService;
import com.advisor.service.media.UserMediaFavoriteService;
import com.advisor.service.media.UserMediaHistoryService;
import com.advisor.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 媒体资源前端API控制器
 */
@RestController
@RequestMapping("/api/media")
public class MediaController {
    
    @Autowired
    private MediaCategoryService mediaCategoryService;
    
    @Autowired
    private MediaResourceService mediaResourceService;
    
    @Autowired
    private UserMediaHistoryService userMediaHistoryService;
    
    @Autowired
    private UserMediaFavoriteService userMediaFavoriteService;
    
    /**
     * 获取媒体分类列表
     */
    @GetMapping("/categories")
    public Result getCategories(@RequestParam Integer mediaType) {
        return Result.success(mediaCategoryService.getCategoryListByType(mediaType));
    }
    
    /**
     * 获取媒体资源列表
     */
    @GetMapping("/resources")
    public Result getMediaResources(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Integer mediaType) {
        // TODO: 实现获取媒体资源列表逻辑
        return Result.success();
    }
    
    /**
     * 获取媒体资源详情
     */
    @GetMapping("/resource/{id}")
    public Result getMediaResource(@PathVariable String id) {
        // TODO: 实现获取媒体资源详情逻辑
        return Result.success();
    }
    
    /**
     * 收藏/取消收藏媒体资源
     */
    @PostMapping("/favorite/{id}")
    public Result toggleFavorite(@PathVariable String id) {
        String userId = UserUtil.getCurrentUserId();
        // TODO: 实现收藏/取消收藏逻辑
        return Result.success();
    }
    
    /**
     * 检查是否收藏
     */
    @GetMapping("/favorite/check/{id}")
    public Result checkFavorite(@PathVariable String id) {
        String userId = UserUtil.getCurrentUserId();
        // TODO: 实现检查是否收藏逻辑
        return Result.success();
    }
    
    /**
     * 获取收藏列表
     */
    @GetMapping("/favorites")
    public Result getFavorites(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserUtil.getCurrentUserId();
        // TODO: 实现获取收藏列表逻辑
        return Result.success();
    }
    
    /**
     * 记录播放进度
     */
    @PostMapping("/history/progress")
    public Result recordProgress(
            @RequestParam String mediaId,
            @RequestParam Integer progress,
            @RequestParam(defaultValue = "0") Integer isCompleted) {
        String userId = UserUtil.getCurrentUserId();
        // TODO: 实现记录播放进度逻辑
        return Result.success();
    }
    
    /**
     * 获取播放历史
     */
    @GetMapping("/history")
    public Result getHistory(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserUtil.getCurrentUserId();
        // TODO: 实现获取播放历史逻辑
        return Result.success();
    }
    
    /**
     * 获取推荐媒体
     */
    @GetMapping("/recommendations")
    public Result getRecommendations(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Integer mediaType) {
        // TODO: 实现获取推荐媒体逻辑
        return Result.success();
    }
} 