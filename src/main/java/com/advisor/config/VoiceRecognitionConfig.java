package com.advisor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 语音识别配置类
 */
@Configuration
public class VoiceRecognitionConfig {

    @Value("${dashscope.api-key}")
    private String apiKey;
    
    @Value("${voice.recognition.enabled:true}")
    private boolean enabled;
    
    @Value("${voice.recognition.max-size:20971520}")
    private long maxSize;
    
    @Value("${voice.recognition.upload-dir:voice/}")
    private String uploadDir;
    
    @Value("${qiniu.domain}")
    private String qiniuDomain;

    public String getApiKey() {
        return apiKey;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public String getUploadDir() {
        return uploadDir;
    }
    
    public String getQiniuDomain() {
        return qiniuDomain;
    }
} 