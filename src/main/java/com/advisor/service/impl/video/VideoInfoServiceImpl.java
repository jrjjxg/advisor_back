package com.advisor.service.impl.video;


import com.advisor.entity.video.VideoInfo;
import com.advisor.mapper.video.VideoInfoMapper;
import com.advisor.service.video.VideoInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

// 视频信息服务实现
@Service
@Slf4j
public class VideoInfoServiceImpl extends ServiceImpl<VideoInfoMapper, VideoInfo> implements VideoInfoService {
    @Autowired
    private VideoInfoMapper videoInfoMapper;
    
    @Value("${qiniu.access-key}")
    private String accessKey;
    
    @Value("${qiniu.secret-key}")
    private String secretKey;
    
    @Value("${qiniu.bucket}")
    private String bucket;
    
    @Value("${qiniu.domain}")
    private String domain;
    
    @Override
    public List<VideoInfo> getVideosByCategory(String categoryId) {
        return videoInfoMapper.selectByCategoryId(categoryId);
    }
    
    @Override
    public VideoInfo getVideoDetail(String videoId) {
        VideoInfo videoInfo = this.getById(videoId);
        if (videoInfo != null) {
            videoInfo.setPlayUrl(getVideoPlayUrl(videoInfo.getFileKey()));
        }
        return videoInfo;
    }
    
    @Override
    public String getVideoPlayUrl(String fileKey) {
        Auth auth = Auth.create(accessKey, secretKey);
        // 私有空间需要生成带签名的URL，公开空间则直接返回URL
        long expireInSeconds = 3600; // URL有效期1小时
        String finalUrl = auth.privateDownloadUrl(domain + "/" + fileKey, expireInSeconds);
        return finalUrl;
    }
    
    @Override
    public String uploadVideo(MultipartFile file, String fileName) {
        try {
            // 构造一个带指定Region对象的配置类
            Configuration cfg = new Configuration();
            cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
            
            UploadManager uploadManager = new UploadManager(cfg);
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            
            // 生成唯一文件名
            String fileKey = "videos/" + UUID.randomUUID().toString() +
                    fileName.substring(fileName.lastIndexOf("."));
            
            Response response = uploadManager.put(file.getInputStream(), fileKey, upToken, null, null);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            
            log.info("Upload success, key: {}, hash: {}", putRet.key, putRet.hash);
            return putRet.key;
        } catch (Exception e) {
            log.error("Upload video error", e);
            throw new RuntimeException("上传视频失败", e);
        }
    }
    
    @Override
    public void incrementViewCount(String videoId) {
        VideoInfo video = this.getById(videoId);
        if (video != null) {
            video.setViewCount(video.getViewCount() + 1);
            this.updateById(video);
        }
    }
}