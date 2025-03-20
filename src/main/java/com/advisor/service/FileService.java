package com.advisor.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileService {
    /**
     * 上传文件
     * @param file 文件对象
     * @return 文件访问URL
     * @throws IOException 文件处理异常
     */
    String uploadFile(MultipartFile file) throws IOException;
}