package com.advisor.controller.video;

import com.advisor.common.Result;
import com.advisor.entity.video.VideoInfo;
import com.advisor.service.video.VideoInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// 后台管理控制器
@RestController
@RequestMapping("/admin/videos")
public class AdminVideoController {
    @Autowired
    private VideoInfoService videoInfoService;
    
    // 上传视频
    @PostMapping("/upload")
    public Result uploadVideo(@RequestParam("file") MultipartFile file,
                             @RequestParam("title") String title,
                             @RequestParam("categoryId") String categoryId,
                             @RequestParam(value = "description", required = false) String description,
                             @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) {
        if (file.isEmpty()) {
            return Result.error("请选择视频文件");
        }
        
        try {
            // 上传视频到七牛云
            String fileName = file.getOriginalFilename();
            String fileKey = videoInfoService.uploadVideo(file, fileName);
            
            // 上传封面图
            String coverUrl = null;
            if (coverFile != null && !coverFile.isEmpty()) {
                // 实际项目中应该有单独的图片上传服务
                // 这里简化处理
                coverUrl = "封面图URL";
            }
            
            // 保存视频信息
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setTitle(title);
            videoInfo.setCategoryId(categoryId);
            videoInfo.setFileKey(fileKey);
            videoInfo.setCoverUrl(coverUrl);
            videoInfo.setDescription(description);
            videoInfo.setStatus(1); // 默认上架
            
            // 保存并确保ID被设置回对象
            boolean saved = videoInfoService.save(videoInfo);        
            return Result.success(videoInfo);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
    
    // 更新视频信息
    @PutMapping("/{id}")
    public Result updateVideo(@PathVariable String id, @RequestBody VideoInfo videoInfo) {
        videoInfo.setId(id);
        boolean success = videoInfoService.updateById(videoInfo);
        return success ? Result.success() : Result.error("更新失败");
    }
    
    // 删除视频
    @DeleteMapping("/{id}")
    public Result deleteVideo(@PathVariable String id) {
        boolean success = videoInfoService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }
    
    // 视频上下架
    @PutMapping("/status/{id}")
    public Result changeStatus(@PathVariable String id, @RequestParam Integer status) {
        VideoInfo video = new VideoInfo();
        video.setId(id);
        video.setStatus(status);
        boolean success = videoInfoService.updateById(video);
        return success ? Result.success() : Result.error("操作失败");
    }
}