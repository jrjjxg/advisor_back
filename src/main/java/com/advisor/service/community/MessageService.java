package com.advisor.service.community;

import com.advisor.dto.MessageSendRequest;
import com.advisor.vo.community.MessageSessionVO;
import com.advisor.vo.community.PrivateMessageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 消息服务接口
 */
public interface MessageService {
    
    /**
     * 发送私信
     *
     * @param request 发送私信请求
     * @param userId  发送用户ID
     * @return 私信ID
     */
    String sendPrivateMessage(MessageSendRequest request, String userId);
    
    /**
     * 获取私信会话列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 会话列表分页结果
     */
    Page<MessageSessionVO> getMessageSessions(String userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取与指定用户的私信记录
     *
     * @param targetUserId 目标用户ID
     * @param userId       当前用户ID
     * @param pageNum      页码
     * @param pageSize     每页大小
     * @return 私信列表分页结果
     */
    Page<PrivateMessageVO> getPrivateMessages(String targetUserId, String userId, Integer pageNum, Integer pageSize);
    
    /**
     * 标记私信为已读
     *
     * @param messageId 私信ID
     * @param userId    用户ID
     */
    void markMessageRead(String messageId, String userId);
    
    /**
     * 标记与指定用户的所有私信为已读
     *
     * @param targetUserId 目标用户ID
     * @param userId       当前用户ID
     * @return 标记为已读的私信数量
     */
    int markAllMessagesRead(String targetUserId, String userId);
}