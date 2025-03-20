package com.advisor.controller;

import com.advisor.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${file.upload.path:/tmp/uploads}")
    private String uploadPath;
    
    @Value("${file.access.url:http://localhost:9000/uploads}")
    private String accessUrl;
    
    // 移动端头像上传路径
    @Value("${file.upload.avatar.path:${file.upload.path}/avatars}")
    private String avatarPath;
    
    // 移动端头像访问URL
    @Value("${file.access.avatar.url:${file.access.url}/avatars}")
    private String avatarUrl;
    
    // 移动端社区图片上传路径
    @Value("${file.upload.community.path:${file.upload.path}/community}")
    private String communityPath;
    
    // 移动端社区图片访问URL
    @Value("${file.access.community.url:${file.access.url}/community}")
    private String communityUrl;

    /**
     * 通用图片上传接口（后台管理使用）
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, uploadPath, accessUrl);
    }
    
    /**
     * 头像上传接口（移动端使用）
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, avatarPath, avatarUrl);
    }
    
    /**
     * 社区图片上传接口（移动端使用）
     */
    @PostMapping("/community/image")
    public Result<String> uploadCommunityImage(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, communityPath, communityUrl);
    }
    
    /**
     * 通用文件上传方法
     */
    private Result<String> uploadFile(MultipartFile file, String savePath, String accessUrlPrefix) {
        if (file.isEmpty()) {
            return Result.fail("上传文件不能为空");
        }
        
        try {
            // 确保目录存在
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = Paths.get(savePath, filename);
            Files.write(filePath, file.getBytes());
            
            System.out.println("文件保存路径: " + filePath.toString());
            
            // 返回访问URL
            String fileUrl = accessUrlPrefix + "/" + filename;
            return Result.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }
}