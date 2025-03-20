package com.advisor.service.impl;

import com.advisor.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    
    @Value("${file.upload.path:/tmp/uploads}")  // 设置默认值
    private String uploadPath;
    
    @Value("${file.access.url:http://localhost:8080/files}")  // 设置默认值
    private String accessUrl;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 获取文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        // 检查文件类型
        if (!isAllowedFileType(extension)) {
            throw new IllegalArgumentException("不支持的文件类型");
        }

        // 生成唯一文件名
        String filename = UUID.randomUUID().toString() + extension;
        
        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // 保存文件
        File destFile = new File(uploadPath + File.separator + filename);
        file.transferTo(destFile);
        
        // 返回访问URL
        return accessUrl + "/" + filename;
    }

    /**
     * 检查文件类型是否允许
     */
    private boolean isAllowedFileType(String extension) {
        String[] allowedTypes = {".jpg", ".jpeg", ".png", ".gif"};
        extension = extension.toLowerCase();
        for (String type : allowedTypes) {
            if (type.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}