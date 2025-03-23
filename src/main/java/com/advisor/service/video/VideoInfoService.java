package com.advisor.service.video;

import com.advisor.entity.video.VideoInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 视频信息服务接口
public interface VideoInfoService extends IService<VideoInfo> {
    // 根据分类获取视频列表
    List<VideoInfo> getVideosByCategory(String categoryId);
    
    // 获取视频详情
    VideoInfo getVideoDetail(String videoId);
    
    // 获取视频播放地址
    String getVideoPlayUrl(String fileKey);
    
    // 上传视频到七牛云
    String uploadVideo(MultipartFile file, String fileName);
    
    // 更新视频观看次数
    void incrementViewCount(String videoId);
}
