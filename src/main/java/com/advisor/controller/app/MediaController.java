package com.advisor.controller.app; // 或者 com.advisor.controller.app

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
 * 用户端媒体资源接口
 */
@RestController
@RequestMapping("/api/videos") // 与前端 api/video.js 路径保持一致
public class MediaController { // 或 VideoController

    @Autowired
    private MediaResourceService mediaResourceService;

    @Autowired
    private MediaCategoryService mediaCategoryService; // 注入 Category Service

    /**
     * 根据分类获取App端媒体资源列表（主要用于音频）
     *
     * @param categoryId 分类ID
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 分页的媒体资源列表
     */
    @GetMapping("/category")
    public Result getPublishedMediaByCategory(
            @RequestParam String categoryId, // 分类ID是必须的
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // 调用新的 Service 方法，明确获取已发布的音频
            Page<MediaResourceVO> page = mediaResourceService.getPublishedAudiosByCategory(
                    pageNum, pageSize, categoryId);
            return Result.success(page);
        } catch (Exception e) {
            // Log the error e
            return Result.fail("获取资源列表失败");
        }
    }

    /**
     * 获取媒体资源详情 (复用admin的逻辑，但可单独拿出)
     * @param id 资源ID
     * @return 资源详情
     */
    @GetMapping("/{id}")
    public Result getMediaResourceDetail(@PathVariable String id) {
        try {
            // 注意：这里可以增加用户ID参数，用于记录播放历史或个性化推荐
            MediaResourceVO vo = mediaResourceService.getMediaResourceDetail(id, /* 可选：userId */ null);
            if (vo == null || vo.getStatus() != 1) { // 确保只返回已发布的
                return Result.fail("资源不存在或未发布");
            }
            // 可以在这里增加播放次数 (views++) 的逻辑，或者在 getMediaResourceDetail service内部处理
            // mediaResourceService.incrementViewCount(id);
            return Result.success(vo);
        } catch (Exception e) {
            // Log the error e
            return Result.fail("获取资源详情失败");
        }
    }



}
