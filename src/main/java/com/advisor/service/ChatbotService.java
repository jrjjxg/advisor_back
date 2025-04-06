package com.advisor.service;

import com.advisor.dto.ChatResponse;
import com.advisor.dto.ThreadRequest;

import java.util.List;

public interface ChatbotService {
    /**
     * 发送消息到聊天机器人
     */
    ChatResponse sendMessage(String userId, String threadId, String message);
    
    /**
     * 创建新的聊天线程
     */
    String createThread(String userId, String title);
    
    /**
     * 获取用户的所有聊天线程
     */
    List<ThreadRequest> getThreads(String userId);
    
    /**
     * 获取线程的聊天历史
     */
    List<ChatResponse> getChatHistory(String userId, String threadId);
    
    /**
     * 删除聊天线程
     */
    void deleteThread(String userId, String threadId);
}