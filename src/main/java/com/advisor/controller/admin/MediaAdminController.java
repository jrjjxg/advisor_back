package com.advisor.controller.admin;

import com.advisor.common.Result;
import com.advisor.dto.media.MediaCategoryDTO;
import com.advisor.dto.media.MediaResourceDTO;
import com.advisor.service.media.MediaCategoryService;
import com.advisor.service.media.MediaResourceService;
import com.advisor.vo.media.MediaResourceVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 媒体资源后台管理控制器
 */
@RestController
@RequestMapping("/api/admin/media")
public class MediaAdminController {
    
    @Autowired
    private MediaCategoryService mediaCategoryService;
    
    @Autowired
    private MediaResourceService mediaResourceService;
    
    /**
     * 创建媒体分类
     */
    @PostMapping("/category")
    public Result createCategory(@RequestBody MediaCategoryDTO dto) {
        String id = mediaCategoryService.createCategory(dto);
        return Result.success(id);
    }
    
    /**
     * 更新媒体分类
     */
    @PutMapping("/category/{id}")
    public Result updateCategory(@PathVariable String id, @RequestBody MediaCategoryDTO dto) {
        dto.setId(id);
        mediaCategoryService.updateCategory(dto);
        return Result.success();
    }
    
    /**
     * 删除媒体分类
     */
    @DeleteMapping("/category/{id}")
    public Result deleteCategory(@PathVariable String id) {
        mediaCategoryService.deleteCategory(id);
        return Result.success();
    }
    
    /**
     * 获取媒体分类详情
     */
    @GetMapping("/category/{id}")
    public Result getCategoryDetail(@PathVariable String id) {
        return Result.success(mediaCategoryService.getCategoryDetail(id));
    }
    
    /**
     * 获取媒体分类列表
     */
    @GetMapping("/categories")
    public Result getCategoryList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer mediaType) {
        return Result.success(mediaCategoryService.getCategoryList(pageNum, pageSize, mediaType));
    }
    
    /**
     * 上传媒体资源
     */
    @PostMapping("/resource/upload")
    public Result uploadMediaResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("mediaType") Integer mediaType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) {
        
        try {
            String resourceId = mediaResourceService.uploadMediaResource(
                file, title, categoryId, mediaType, description, coverFile);
            return Result.success(resourceId);
        } catch (IOException e) {
            return Result.fail("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新媒体资源信息
     */
    @PutMapping("/resource/{id}")
    public Result updateMediaResource(@PathVariable String id, @RequestBody MediaResourceDTO dto) {
        try {
            dto.setId(id);
            mediaResourceService.updateMediaResource(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.fail("更新媒体资源失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除媒体资源
     */
    @DeleteMapping("/resource/{id}")
    public Result deleteMediaResource(@PathVariable String id) {
        try {
            mediaResourceService.deleteMediaResource(id);
            return Result.success();
        } catch (Exception e) {
            return Result.fail("删除媒体资源失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取媒体资源详情
     */
    @GetMapping("/resource/{id}")
    public Result getMediaResourceDetail(@PathVariable String id) {
        try {
            MediaResourceVO vo = mediaResourceService.getMediaResourceDetail(id, null);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.fail("获取媒体资源详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取媒体资源列表
     */
    @GetMapping("/resources")
    public Result getMediaResourceList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Integer mediaType,
            @RequestParam(required = false) Integer status) {
        // 实现获取媒体资源列表逻辑
        try {
            // 调用 service 层方法获取分页数据
            Page<MediaResourceVO> page = mediaResourceService.getMediaResourceList(
                pageNum, pageSize, title, categoryId, mediaType, status, null);
            return Result.success(page);
        } catch (Exception e) {
            return Result.fail("获取媒体资源列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新媒体资源状态
     */
    @PutMapping("/resource/status/{id}")
    public Result updateMediaResourceStatus(
            @PathVariable String id, 
            @RequestParam Integer status) {
        try {
            mediaResourceService.updateMediaResourceStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.fail("更新媒体资源状态失败: " + e.getMessage());
        }
    }
} 