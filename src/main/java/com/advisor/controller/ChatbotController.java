package com.advisor.controller;

import com.advisor.common.Result;
import com.advisor.dto.ChatRequest;
import com.advisor.dto.ChatResponse;
import com.advisor.dto.ThreadRequest;
import com.advisor.service.ChatbotService;
import com.advisor.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/chatbot")

public class ChatbotController {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);

    @Autowired
    private ChatbotService chatbotService;
    
    // 创建线程池处理流式响应
    private final ExecutorService executorService = Executors.newCachedThreadPool();


    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestParam String threadId, 
                              @RequestParam String userId,
                              @RequestParam(required = false) String message) {
        logger.info("开始流式聊天: userId={}, threadId={}", userId, threadId);
        
        // 创建SSE emitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        
        // 异步处理
        executorService.execute(() -> {
            try {
                // 发送开始事件
                String responseId = UUID.randomUUID().toString();
                emitter.send(SseEmitter.event()
                        .name("start")
                        .data("{\"response_id\":\"" + responseId + "\"}"));
                
                // 如果有消息，处理消息
                if (message != null && !message.trim().isEmpty()) {
                    // 实际项目中这里应该调用聊天服务逐步获取回复，这里模拟一下
                    ChatResponse response = chatbotService.sendMessage(userId, threadId, message);
                    
                    // 模拟流式输出 - 将整个回复分成多个小块发送
                    String fullResponse = response.getMessage();
                    int chunkSize = 5; // 每次发送5个字符
                    
                    for (int i = 0; i < fullResponse.length(); i += chunkSize) {
                        String chunk = fullResponse.substring(i, 
                                Math.min(i + chunkSize, fullResponse.length()));
                        
                        // 发送数据块
                        emitter.send(SseEmitter.event()
                                .name("chunk")
                                .data("{\"chunk\":\"" + chunk + "\", \"response_id\":\"" + responseId + "\"}"));
                        
                        // 模拟延迟
                        Thread.sleep(50);
                    }
                    
                    // 发送完成事件
                    emitter.send(SseEmitter.event()
                            .name("complete")
                            .data("{\"full_response\":\"" + fullResponse + "\", \"response_id\":\"" + responseId + "\"}"));
                }
                
                // 完成
                emitter.complete();
            } catch (Exception e) {
                logger.error("流式聊天处理异常", e);
                emitter.completeWithError(e);
            }
        });
        
        // 设置超时和错误处理
        emitter.onTimeout(() -> logger.warn("聊天流超时: userId={}, threadId={}", userId, threadId));
        emitter.onError((e) -> logger.error("聊天流错误: userId={}, threadId={}, error={}", userId, threadId, e.getMessage()));
        
        return emitter;
    }
    
    /**
     * 处理POST方式的流式请求，与GET方式功能相同
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStreamPost(@RequestParam String threadId, 
                                 @RequestParam String userId,
                                 @RequestBody(required = false) ChatRequest request) {
        String message = request != null ? request.getMessage() : null;
        return chatStream(threadId, userId, message);
    }

    /**
     * 发送消息到聊天机器人
     */
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            String userId = UserUtil.getCurrentUsername();
            if (userId == null) {
                logger.warn("用户未登录");
                return Result.fail(401, "用户未登录");
            }
            
            logger.info("发送聊天消息: userId={}, threadId={}", userId, request.getThreadId());
            
            // 调用聊天机器人服务
            ChatResponse response = chatbotService.sendMessage(userId, request.getThreadId(), request.getMessage());
            return Result.success(response);
        } catch (Exception e) {
            logger.error("发送消息异常", e);
            return Result.fail(500, "发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建新的聊天线程
     */
    @PostMapping("/thread")
    public Result<String> createThread(@RequestBody ThreadRequest request) {
        try {
            String userId = UserUtil.getCurrentUsername();
            if (userId == null) {
                logger.warn("用户未登录");
                return Result.fail(401, "用户未登录");
            }
            
            logger.info("创建聊天线程: userId={}, title={}", userId, request.getTitle());
            
            String threadId = chatbotService.createThread(userId, request.getTitle());
            return Result.success(threadId);
        } catch (Exception e) {
            logger.error("创建线程异常", e);
            return Result.fail(500, "创建线程失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的所有聊天线程
     */
    @GetMapping("/threads")
    public Result<List<ThreadRequest>> getThreads() {
        try {
            String userId = UserUtil.getCurrentUsername();
            if (userId == null) {
                logger.warn("用户未登录");
                return Result.fail(401, "用户未登录");
            }
            
            logger.info("获取聊天线程列表: userId={}", userId);
            
            List<ThreadRequest> threads = chatbotService.getThreads(userId);
            return Result.success(threads);
        } catch (Exception e) {
            logger.error("获取线程列表异常", e);
            return Result.fail(500, "获取线程列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取线程的聊天历史
     */
    @GetMapping("/history/{threadId}")
    public Result<List<ChatResponse>> getChatHistory(@PathVariable String threadId) {
        try {
            String userId = UserUtil.getCurrentUsername();
            if (userId == null) {
                logger.warn("用户未登录");
                return Result.fail(401, "用户未登录");
            }
            
            logger.info("获取聊天历史: userId={}, threadId={}", userId, threadId);
            
            List<ChatResponse> history = chatbotService.getChatHistory(userId, threadId);
            return Result.success(history);
        } catch (Exception e) {
            logger.error("获取聊天历史异常", e);
            return Result.fail(500, "获取聊天历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除聊天线程
     */
    @DeleteMapping("/thread/{threadId}")
    public Result<?> deleteThread(@PathVariable String threadId) {
        try {
            String userId = UserUtil.getCurrentUsername();
            if (userId == null) {
                logger.warn("用户未登录");
                return Result.fail(401, "用户未登录");
            }
            
            logger.info("删除聊天线程: userId={}, threadId={}", userId, threadId);
            
            chatbotService.deleteThread(userId, threadId);
            return Result.success(null);
        } catch (Exception e) {
            logger.error("删除线程异常", e);
            return Result.fail(500, "删除线程失败: " + e.getMessage());
        }
    }
}
