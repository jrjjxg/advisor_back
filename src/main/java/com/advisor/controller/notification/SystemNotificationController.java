package com.advisor.controller.notification;

import com.advisor.common.Result;
import com.advisor.entity.notification.SystemNotification;
import com.advisor.service.notification.SystemNotificationService;
import com.advisor.util.UserUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统通知控制器
 * 负责处理系统发送给用户的通知消息
 */
@RestController
@RequestMapping("/api/system-notifications")
public class SystemNotificationController {
    
    @Autowired
    private SystemNotificationService notificationService;
    
    /**
     * 获取用户通知列表
     */
    @GetMapping
    public Result<Page<SystemNotification>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type) {
        
        String userId = UserUtil.getCurrentUserId();
        
        // 创建分页对象
        Page<SystemNotification> pageInfo = new Page<>(page, size);
        
        // 调用 Service 层进行分页查询
        Page<SystemNotification> resultPage = notificationService.getNotificationsByPage(userId, pageInfo, type);
        
        // 返回查询结果
        return Result.success(resultPage);
    }
    
    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount() {
        String userId = UserUtil.getCurrentUserId();
        int count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }
    
    /**
     * 标记通知为已读
     */
    @PostMapping("/{id}/read")
    public Result<Boolean> markAsRead(@PathVariable String id) {
        String userId = UserUtil.getCurrentUserId();
        boolean success = notificationService.markAsRead(id, userId);
        return Result.success(success);
    }
    
    /**
     * 标记所有通知为已读
     */
    @PostMapping("/read-all")
    public Result<Integer> markAllAsRead() {
        String userId = UserUtil.getCurrentUserId();
        int count = notificationService.markAllAsRead(userId);
        return Result.success(count);
    }
} 