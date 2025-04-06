package com.advisor.service.notification.impl;

import com.advisor.entity.notification.SystemNotification;
import com.advisor.mapper.notification.SystemNotificationMapper;
import com.advisor.service.notification.SystemNotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 系统通知服务实现
 */
@Service
public class SystemNotificationServiceImpl implements SystemNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemNotificationServiceImpl.class);
    
    @Autowired
    private SystemNotificationMapper notificationMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public boolean sendSystemMessage(String userId, String title, String content, String type, Object data) {
        try {
            SystemNotification notification = new SystemNotification();
            notification.setId(UUID.randomUUID().toString());
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(type);
            
            // 如果有附加数据，将其转换为JSON字符串
            if (data != null) {
                notification.setData(objectMapper.writeValueAsString(data));
            }
            
            notification.setIsRead(false);
            notification.setCreateTime(LocalDateTime.now());
            notification.setUpdateTime(LocalDateTime.now());
            
            int result = notificationMapper.insert(notification);
            return result > 0;
        } catch (Exception e) {
            logger.error("发送系统消息失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean sendAlertNotification(String userId, String title, String content, Object data) {
        return sendSystemMessage(userId, title, content, "ALERT", data);
    }
    
    @Override
    public int getUnreadCount(String userId) {
        LambdaQueryWrapper<SystemNotification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemNotification::getUserId, userId)
                   .eq(SystemNotification::getIsRead, false);
        
        // 修复: 将 Long 类型的结果转换为 int
        Long count = notificationMapper.selectCount(queryWrapper);
        return count != null ? count.intValue() : 0;
    }
    
    @Override
    public boolean markAsRead(String notificationId, String userId) {
        try {
            SystemNotification notification = notificationMapper.selectById(notificationId);
            if (notification == null || !notification.getUserId().equals(userId)) {
                return false;
            }
            
            notification.setIsRead(true);
            notification.setUpdateTime(LocalDateTime.now());
            int result = notificationMapper.updateById(notification);
            return result > 0;
        } catch (Exception e) {
            logger.error("标记通知为已读失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public int markAllAsRead(String userId) {
        try {
            return notificationMapper.markAllAsRead(userId);
        } catch (Exception e) {
            logger.error("标记所有通知为已读失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Page<SystemNotification> getNotificationsByPage(String userId, Page<SystemNotification> page, String type) {
        LambdaQueryWrapper<SystemNotification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemNotification::getUserId, userId);

        // 如果提供了类型，则添加类型筛选条件
        if (StringUtils.isNotBlank(type)) {
            queryWrapper.eq(SystemNotification::getType, type);
        }

        // 按创建时间降序排序
        queryWrapper.orderByDesc(SystemNotification::getCreateTime);

        // 执行分页查询
        return notificationMapper.selectPage(page, queryWrapper);
    }
} 