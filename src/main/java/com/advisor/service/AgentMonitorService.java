package com.advisor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AgentMonitorService {
    private static final Logger logger = LoggerFactory.getLogger(AgentMonitorService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${agent.api.url}")
    private String agentApiUrl;
    
    public Map<String, Object> getEmotionAnalysis(String userId, String threadId) {
        logger.info("获取情绪分析: userId={}, threadId={}", userId, threadId);
        
        try {
            String fullUrl = agentApiUrl + "/api/emotion/analysis";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("userId", userId);
            requestBody.put("threadId", threadId);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            return restTemplate.postForObject(fullUrl, request, Map.class);
        } catch (Exception e) {
            logger.error("获取情绪分析失败: " + e.getMessage(), e);
            return new HashMap<>();
        }
    }
    
    public void sendAlert(String userId, String message) {
        logger.info("发送预警: userId={}", userId);
        
        try {
            String fullUrl = agentApiUrl + "/api/alert";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("userId", userId);
            requestBody.put("message", message);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            restTemplate.postForObject(fullUrl, request, Void.class);
        } catch (Exception e) {
            logger.error("发送预警失败: " + e.getMessage(), e);
        }
    }
}