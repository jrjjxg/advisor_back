package com.advisor.service.impl;

import com.advisor.dto.ChatResponse;
import com.advisor.dto.ThreadRequest;
import com.advisor.service.ChatbotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.advisor.service.AgentMonitorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotServiceImpl implements ChatbotService {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotServiceImpl.class);
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${chatbot.api.url}")
    private String chatbotApiUrl;
    
    @Autowired
    private AgentMonitorService agentMonitorService;
    
    @Override
    public ChatResponse sendMessage(String userId, String threadId, String message) {
        logger.info("发送消息到AI服务: userId={}, threadId={}", userId, threadId);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("threadId", threadId);
        requestBody.put("message", message);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            // 调用Python应用的API，打印完整URL便于调试
            String fullUrl = chatbotApiUrl + "/api/chat";
            logger.info("调用AI服务URL: {}", fullUrl);
            
            ChatResponse response = restTemplate.postForObject(fullUrl, request, ChatResponse.class);
            
            // 获取情绪分析
            Map<String, Object> emotionAnalysis = agentMonitorService.getEmotionAnalysis(userId, threadId);
            
            // 检查是否需要发送预警
            if (emotionAnalysis != null && 
                (Boolean)emotionAnalysis.getOrDefault("needsAlert", false)) {
                String alertMessage = (String)emotionAnalysis.get("alertMessage");
                agentMonitorService.sendAlert(userId, alertMessage);
            }
            
            return response;
        } catch (Exception e) {
            logger.error("调用AI服务失败: " + e.getMessage(), e);
            
            // 返回错误响应而不是抛出异常
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setMessage("无法连接到AI服务，请检查服务是否运行。错误: " + e.getMessage());
            errorResponse.setRole("assistant");
            return errorResponse;
        }
    }
    
    @Override
    public String createThread(String userId, String title) {
        logger.info("创建AI对话线程: userId={}, title={}", userId, title);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("title", title);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            // 调用Python应用的API
            String fullUrl = chatbotApiUrl + "/api/thread";
            logger.info("调用AI服务URL: {}", fullUrl);
            
            Object response = restTemplate.postForObject(fullUrl, request, Object.class);
            logger.info("AI服务响应: {}", response);
            
            // 处理响应，确保返回字符串类型的threadId
            if (response instanceof String) {
                return (String) response;
            } else if (response instanceof Map) {
                return String.valueOf(((Map<?,?>) response).get("threadId"));
            }
            
            return String.valueOf(response);
        } catch (Exception e) {
            logger.error("创建AI对话线程失败: " + e.getMessage(), e);
            // 创建临时线程ID并返回，避免前端出错
            return "temp_" + System.currentTimeMillis();
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<ThreadRequest> getThreads(String userId) {
        logger.info("获取用户AI对话线程: userId={}", userId);
        
        try {
            // 调用Python应用的API
            String fullUrl = chatbotApiUrl + "/api/threads?userId=" + userId;
            logger.info("调用AI服务URL: {}", fullUrl);
            
            return restTemplate.getForObject(fullUrl, List.class);
        } catch (Exception e) {
            logger.error("获取AI对话线程失败: " + e.getMessage(), e);
            // 返回空列表而不是抛出异常
            return List.of();
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<ChatResponse> getChatHistory(String userId, String threadId) {
        logger.info("获取AI对话历史: userId={}, threadId={}", userId, threadId);
        
        try {
            // 修正API路径 - 使用路径参数而非查询参数
            String fullUrl = chatbotApiUrl + "/api/history/" + threadId + "?userId=" + userId;
            logger.info("调用AI服务URL: {}", fullUrl);
            
            return restTemplate.getForObject(fullUrl, List.class);
        } catch (Exception e) {
            logger.error("获取AI对话历史失败: " + e.getMessage(), e);
            // 返回空列表而不是抛出异常
            return List.of();
        }
    }
    
    @Override
    public void deleteThread(String userId, String threadId) {
        logger.info("删除AI对话线程: userId={}, threadId={}", userId, threadId);
        
        try {
            // 修正API路径 - 使用路径参数而非查询参数
            String fullUrl = chatbotApiUrl + "/api/thread/" + threadId + "?userId=" + userId;
            logger.info("调用AI服务URL: {}", fullUrl);
            
            restTemplate.delete(fullUrl);
        } catch (Exception e) {
            logger.error("删除AI对话线程失败: " + e.getMessage(), e);
            // 忽略异常，不中断流程
        }
    }
    
    public boolean handleConfirmation(String userId, String threadId, String response) {
        // 构建请求
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("threadId", threadId);
        requestBody.put("response", response);
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            // 调用Python服务的API
            String fullUrl = chatbotApiUrl + "/api/confirm";
            return restTemplate.postForObject(fullUrl, request, Boolean.class);
        } catch (Exception e) {
            logger.error("处理确认失败: " + e.getMessage(), e);
            return false;
        }
    }
}