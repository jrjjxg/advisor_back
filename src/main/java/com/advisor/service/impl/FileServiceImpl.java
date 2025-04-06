package com.advisor.service.impl;

import com.advisor.service.FileService;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${qiniu.access-key}")
    private String accessKey;

    @Value("${qiniu.secret-key}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Value("${qiniu.domain}")
    private String domain;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // 默认调用无类型标识的方法，保持向后兼容
        return uploadFile(file, "default");
    }

    @Override
    public String uploadFile(MultipartFile file, String type) throws IOException {
        try {
            // 1. 创建七牛云配置（指定区域）
            Configuration cfg = new Configuration(Region.autoRegion());
            UploadManager uploadManager = new UploadManager(cfg);
            
            // 2. 创建认证信息
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            
            // 3. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            // 根据类型生成不同的路径前缀
            String prefix = "";
            if ("journal".equals(type)) {
                prefix = "journal/";
            } else if ("community".equals(type)) {
                prefix = "community/";
            } else if ("avatar".equals(type)) {
                prefix = "avatar/";
            } else {
                prefix = "other/";
            }
            
            String key = prefix + UUID.randomUUID().toString().replace("-", "") + suffix;
            
            // 4. 上传文件
            byte[] fileBytes = file.getBytes();
            Response response = uploadManager.put(fileBytes, key, upToken);
            
            // 5. 解析返回结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            
            // 6. 返回完整的URL（公开空间）
            return domain + "/" + putRet.key;
        } catch (QiniuException ex) {
            throw new IOException("上传到七牛云失败: " + ex.getMessage());
        }
    }
}