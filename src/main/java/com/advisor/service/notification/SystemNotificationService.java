package com.advisor.service.notification;

import com.advisor.entity.notification.SystemNotification;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 系统通知服务接口
 */
public interface SystemNotificationService {
    
    /**
     * 发送系统消息
     * @param userId 用户ID
     * @param title 消息标题
     * @param content 消息内容
     * @param type 消息类型
     * @param data 附加数据
     * @return 是否发送成功
     */
    boolean sendSystemMessage(String userId, String title, String content, String type, Object data);
    
    /**
     * 发送预警通知
     * @param userId 用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param data 附加数据
     * @return 是否发送成功
     */
    boolean sendAlertNotification(String userId, String title, String content, Object data);
    
    /**
     * 获取用户通知分页列表
     * @param userId 用户ID
     * @param page 分页对象
     * @param type 筛选类型 (可选)
     * @return 通知分页结果
     */
    Page<SystemNotification> getNotificationsByPage(String userId, Page<SystemNotification> page, String type);
    
    /**
     * 获取用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int getUnreadCount(String userId);
    
    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     * @param userId 用户ID
     * @return 是否标记成功
     */
    boolean markAsRead(String notificationId, String userId);
    
    /**
     * 标记所有通知为已读
     * @param userId 用户ID
     * @return 标记为已读的通知数量
     */
    int markAllAsRead(String userId);
} 