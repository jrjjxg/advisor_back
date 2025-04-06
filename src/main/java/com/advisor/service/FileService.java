package com.advisor.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileService {
    /**
     * 上传文件到云存储
     * @param file 文件
     * @return 文件URL
     */
    String uploadFile(MultipartFile file) throws IOException;
    
    /**
     * 上传文件到云存储，支持指定类型
     * @param file 文件
     * @param type 文件类型（journal、community、avatar等）
     * @return 文件URL
     */
    String uploadFile(MultipartFile file, String type) throws IOException;

}