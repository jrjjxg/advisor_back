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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public String createThread(String userId, String title, String systemPrompt) {
        logger.info("创建AI对话线程: userId={}, title={}", userId, title);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("title", title);
        if (systemPrompt != null) {
            requestBody.put("systemPrompt", systemPrompt);
        }
        
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
    public List<ChatResponse> getChatHistory(String userId, String threadId) {
        logger.info("获取AI对话历史: userId={}, threadId={}", userId, threadId);
        
        try {
            // --- 修正 1: 构建不含 userId 查询参数的 URL ---
            String fullUrl = chatbotApiUrl + "/api/history/" + threadId; 
            logger.info("调用AI服务URL (修正后): {}", fullUrl);
            
            // --- 修正 2: 使用 exchange 获取完整响应并处理嵌套结构 ---
            // 定义期望的响应体类型，注意是 Map<String, List<ChatResponse>> 或自定义 DTO
            ParameterizedTypeReference<Map<String, Object>> responseType = 
                    new ParameterizedTypeReference<Map<String, Object>>() {};

            // 发起请求
            ResponseEntity<Map<String, Object>> responseEntity = 
                    restTemplate.exchange(fullUrl, HttpMethod.GET, null, responseType);

            // 检查响应状态码和响应体
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                Map<String, Object> responseBody = responseEntity.getBody();
                Object dataObject = responseBody.get("data");
                
                // 检查 "data" 字段是否存在且是一个列表
                if (dataObject instanceof List) {
                    // 注意：这里需要手动转换或使用更健壮的反序列化库 (如 Jackson ObjectMapper)
                    // 这是一个简化的示例，假设 List 中的 Map 结构能匹配 ChatResponse
                    @SuppressWarnings("unchecked") // 抑制类型转换警告
                    List<Map<String, Object>> rawList = (List<Map<String, Object>>) dataObject;
                    
                    // 将原始 Map 列表转换为 ChatResponse 列表 (需要实现这个转换逻辑)
                    return convertRawListToChatResponse(rawList); 
                    
                    // 如果 Python 返回的 data 直接是 List<ChatResponse> 结构，可以简化，但仍然需要处理外层Map
                    // return (List<ChatResponse>) dataObject; // 可能因类型擦除失败
                } else {
                    logger.warn("从 Python 获取的历史记录响应中 'data' 字段不是列表或不存在: {}", dataObject);
                    return List.of();
                }
            } else {
                logger.error("调用 Python 获取历史记录失败，HTTP状态码: {}", responseEntity.getStatusCode());
                return List.of();
            }
            
        } catch (Exception e) {
            logger.error("获取AI对话历史失败: " + e.getMessage(), e);
            // 返回空列表而不是抛出异常
            return List.of();
        }
    }
    
    // --- 新增: 辅助方法，将 List<Map> 转换为 List<ChatResponse> ---
    private List<ChatResponse> convertRawListToChatResponse(List<Map<String, Object>> rawList) {
        if (rawList == null) {
            return List.of();
        }
        return rawList.stream().map(map -> {
            ChatResponse cr = new ChatResponse();
            
            // --- 基础字段映射 ---
            cr.setId((String) map.get("id")); 
            cr.setRole((String) map.get("role"));
            cr.setMessage((String) map.get("message")); // 使用message字段
            cr.setTimestamp((String) map.get("timestamp")); 
            
            // --- 处理类型字段 ---
            String msgType = (String) map.get("type");
            if (msgType != null) {
                cr.setType(msgType); // 设置消息类型
                
                // --- 如果是日记分享类型，映射特定字段 ---
                if ("diary_share".equals(msgType)) {
                    cr.setDiaryId((String) map.get("diaryId"));
                    cr.setDiaryTitle((String) map.get("diaryTitle"));
                    cr.setDiaryDate((String) map.get("diaryDate"));
                    // 如果还有其他 metadata 字段需要映射，在这里添加
                }
                // 处理file_info类型
                if ("file_info".equals(msgType)) {
                    // 获取filename和file_type
                    Object filenameObj = map.get("filename");
                    Object fileTypeObj = map.get("file_type");
                    
                    // 设置文件相关字段
                    if (filenameObj != null) {
                        cr.setFilename((String) filenameObj);
                    }
                    if (fileTypeObj != null) {
                        cr.setFileType((String) fileTypeObj);
                    }
                    
                    // 确保message字段不为空
                    if (cr.getMessage() == null) {
                        Object messageObj = map.get("message");
                        if (messageObj != null) {
                            cr.setMessage((String) messageObj);
                        } else {
                            cr.setMessage("用户上传了文件");
                        }
                    }
                }
            } else {
                cr.setType("text"); // 如果 type 字段不存在，默认为 text
            }

            return cr;
        }).collect(Collectors.toList());
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

}