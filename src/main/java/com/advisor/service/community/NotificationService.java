package com.advisor.service.community;

import com.advisor.vo.community.NotificationVO;
import com.advisor.vo.community.UnreadCountVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 创建通知
     *
     * @param userId    接收用户ID
     * @param senderId  发送用户ID
     * @param type      类型：like/comment/follow/system
     * @param targetId  目标ID
     * @param content   通知内容
     * @return 通知ID
     */
    String createNotification(String userId, String senderId, String type, String targetId, String content);
    
    /**
     * 获取通知列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 通知列表分页结果
     */
    Page<NotificationVO> getNotifications(String userId, Integer pageNum, Integer pageSize);
    
    /**
     * 标记通知为已读
     *
     * @param notificationId 通知ID
     * @param userId         用户ID
     */
    void markNotificationRead(String notificationId, String userId);
    
    /**
     * 标记所有通知为已读
     *
     * @param userId 用户ID
     * @return 标记为已读的通知数量
     */
    int markAllNotificationsRead(String userId);
    
    /**
     * 获取未读通知数
     *
     * @param userId 用户ID
     * @return 未读通知数
     */
    UnreadCountVO getUnreadCount(String userId);
}