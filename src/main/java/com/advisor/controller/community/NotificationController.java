package com.advisor.controller.community;
import com.advisor.common.Result;
import com.advisor.service.community.NotificationService;
import com.advisor.util.UserUtil;
import com.advisor.vo.community.NotificationVO;
import com.advisor.vo.community.UnreadCountVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/community/notification")
@Api(tags = "社区通知接口")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @ApiOperation("获取通知列表")
    @GetMapping("/list")
    public Result<Page<NotificationVO>> getNotifications(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String userId = UserUtil.getCurrentUserId();
        Page<NotificationVO> page = notificationService.getNotifications(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("标记通知为已读")
    @PostMapping("/read/{notificationId}")
    public Result<Void> markNotificationRead(@PathVariable String notificationId) {
        String userId = UserUtil.getCurrentUserId();
        notificationService.markNotificationRead(notificationId, userId);
        return Result.success();
    }
    
    @ApiOperation("标记所有通知为已读")
    @PostMapping("/read/all")
    public Result<Integer> markAllNotificationsRead() {
        String userId = UserUtil.getCurrentUserId();
        int count = notificationService.markAllNotificationsRead(userId);
        return Result.success(count);
    }
    
    @ApiOperation("获取未读通知数")
    @GetMapping("/unread")
    public Result<UnreadCountVO> getUnreadCount() {
        String userId = UserUtil.getCurrentUserId();
        UnreadCountVO unreadCount = notificationService.getUnreadCount(userId);
        return Result.success(unreadCount);
    }
}