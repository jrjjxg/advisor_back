package com.advisor.controller.emotion;

import com.advisor.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/emotion")
@Slf4j
public class EmotionRecognitionController {

    @Value("${emotion.recognition.api.url}")
    private String emotionRecognitionApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/analyze")
    public Result<?> analyzeEmotion(@RequestParam("file") MultipartFile file) {
        try {
            // 构建URL
            String url = emotionRecognitionApiUrl + "/api/predict";
            
            // 转发请求到Python服务
            // 这里使用了简化的实现，实际中可能需要更复杂的处理
            // 例如添加超时、错误处理等
            Map<?, ?> response = restTemplate.postForObject(
                url, 
                file.getResource(), 
                Map.class
            );
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("情绪识别请求失败", e);
            return Result.error("情绪识别失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/health")
    public Result<?> checkHealth() {
        try {
            // 检查Python服务是否可用
            String url = emotionRecognitionApiUrl + "/api/health";
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            return Result.success(response);
        } catch (Exception e) {
            log.error("情绪识别服务健康检查失败", e);
            return Result.error("情绪识别服务不可用");
        }
    }
} 