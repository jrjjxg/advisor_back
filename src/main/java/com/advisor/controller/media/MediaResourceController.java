package com.advisor.controller.media;

import com.advisor.common.Result;

import com.advisor.service.media.MediaCategoryService;
import com.advisor.service.media.MediaResourceService;
import com.advisor.vo.media.MediaCategoryVO;
import com.advisor.vo.media.MediaResourceVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 媒体资源控制器
 */
@RestController
@RequestMapping("/api/media")
public class MediaResourceController {

    @Autowired
    private MediaCategoryService mediaCategoryService;

    @Autowired
    private MediaResourceService mediaResourceService;

    /**
     * 获取所有媒体分类
     * @param mediaType 媒体类型：1=视频，2=音频
     * @return 分类列表
     */
    @GetMapping("/categories")
    public Result<List<MediaCategoryVO>> getCategories(@RequestParam(required = false) Integer mediaType) {
        List<MediaCategoryVO> categories = mediaCategoryService.getCategoryListByType(mediaType);
        return Result.success(categories);
    }

    /**
     * 获取单个分类详情
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/categories/{id}")
    public Result<MediaCategoryVO> getCategoryDetail(@PathVariable String id) {
        MediaCategoryVO category = mediaCategoryService.getCategoryDetail(id);
        return Result.success(category);
    }

    /**
     * 获取媒体资源列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param categoryId 分类ID
     * @param mediaType 媒体类型：1=视频，2=音频
     * @param title 标题关键词
     * @return 分页资源列表
     */
    @GetMapping("/resources")
    public Result<Page<MediaResourceVO>> getResources(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Integer mediaType,
            @RequestParam(required = false) String title) {
        Page<MediaResourceVO> resources = mediaResourceService.getMediaResourceList(
                pageNum, pageSize, title, categoryId, mediaType, 1, null);
        return Result.success(resources);
    }

    /**
     * 获取单个媒体资源详情
     * @param id 资源ID
     * @return 资源详情
     */
    @GetMapping("/resources/{id}")
    public Result<MediaResourceVO> getResourceDetail(@PathVariable String id) {
        MediaResourceVO resource = mediaResourceService.getMediaResourceDetail(id, null);
        return Result.success(resource);
    }

    /**
     * 增加资源播放次数
     * @param id 资源ID
     * @return 结果
     */
    @PostMapping("/resources/{id}/view")
    public Result<Void> incrementResourceViews(@PathVariable String id) {
        mediaResourceService.incrementViews(id);
        return Result.success();
    }
} 