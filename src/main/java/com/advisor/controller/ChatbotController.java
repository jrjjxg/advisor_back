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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);

    @Autowired
    private ChatbotService chatbotService;

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
