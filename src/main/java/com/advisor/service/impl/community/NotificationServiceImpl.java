package com.advisor.service.impl.community;

import com.advisor.entity.base.User;
import com.advisor.entity.community.Notification;
import com.advisor.mapper.base.UserMapper;
import com.advisor.mapper.community.NotificationMapper;
import com.advisor.service.community.NotificationService;
import com.advisor.vo.community.NotificationVO;
import com.advisor.vo.community.UnreadCountVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public String createNotification(String userId, String senderId, String type, String targetId, String content) {
        // 创建通知
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setSenderId(senderId);
        notification.setType(type);
        notification.setTargetId(targetId);
        notification.setContent(content);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        
        // 保存通知
        notificationMapper.insert(notification);
        
        // 更新未读通知数
        User user = userMapper.selectById(userId);
        switch (type) {
            case "like":
                user.setLikeUnread(user.getLikeUnread() + 1);
                break;
            case "comment":
                user.setCommentUnread(user.getCommentUnread() + 1);
                break;
            case "follow":
                user.setNotificationUnread(user.getNotificationUnread() + 1);
                break;
            case "system":
                user.setNotificationUnread(user.getNotificationUnread() + 1);
                break;
        }
        userMapper.updateById(user);
        
        return notification.getId();
    }
    
    @Override
    public Page<NotificationVO> getNotifications(String userId, Integer pageNum, Integer pageSize) {
        // 查询通知
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreateTime);
        
        Page<Notification> page = new Page<>(pageNum, pageSize);
        Page<Notification> notificationPage = notificationMapper.selectPage(page, queryWrapper);
        
        // 转换为VO
        Page<NotificationVO> voPage = new Page<>(notificationPage.getCurrent(), notificationPage.getSize(), notificationPage.getTotal());
        
        List<NotificationVO> voList = notificationPage.getRecords().stream()
                .map(notification -> {
                    NotificationVO vo = new NotificationVO();
                    BeanUtils.copyProperties(notification, vo);
                    
                    // 查询发送者信息
                    if (notification.getSenderId() != null) {
                        User sender = userMapper.selectById(notification.getSenderId());
                        if (sender != null) {
                            vo.setSenderName(sender.getUsername());
                            vo.setSenderAvatar(sender.getAvatar());
                        }
                    }
                    
                    return vo;
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public void markNotificationRead(String notificationId, String userId) {
        // 查询通知
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            return;
        }
        
        // 标记为已读
        if (notification.getIsRead() == 0) {
            notification.setIsRead(1);
            notificationMapper.updateById(notification);
            
            // 更新未读通知数
            User user = userMapper.selectById(userId);
            switch (notification.getType()) {
                case "like":
                    if (user.getLikeUnread() > 0) {
                        user.setLikeUnread(user.getLikeUnread() - 1);
                    }
                    break;
                case "comment":
                    if (user.getCommentUnread() > 0) {
                        user.setCommentUnread(user.getCommentUnread() - 1);
                    }
                    break;
                case "follow":
                case "system":
                    if (user.getNotificationUnread() > 0) {
                        user.setNotificationUnread(user.getNotificationUnread() - 1);
                    }
                    break;
            }
            userMapper.updateById(user);
        }
    }
    
    @Override
    public int markAllNotificationsRead(String userId) {
        // 查询所有未读通知
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        
        List<Notification> notifications = notificationMapper.selectList(queryWrapper);
        
        if (notifications.isEmpty()) {
            return 0;
        }
        
        // 统计各类型未读数
        int likeCount = 0;
        int commentCount = 0;
        int otherCount = 0;
        
        for (Notification notification : notifications) {
            switch (notification.getType()) {
                case "like":
                    likeCount++;
                    break;
                case "comment":
                    commentCount++;
                    break;
                case "follow":
                case "system":
                    otherCount++;
                    break;
            }
            
            // 标记为已读
            notification.setIsRead(1);
            notificationMapper.updateById(notification);
        }
        
        // 更新用户未读数
        User user = userMapper.selectById(userId);
        user.setLikeUnread(Math.max(0, user.getLikeUnread() - likeCount));
        user.setCommentUnread(Math.max(0, user.getCommentUnread() - commentCount));
        user.setNotificationUnread(Math.max(0, user.getNotificationUnread() - otherCount));
        userMapper.updateById(user);
        
        return notifications.size();
    }
    
    @Override
    public UnreadCountVO getUnreadCount(String userId) {
        User user = userMapper.selectById(userId);
        
        UnreadCountVO vo = new UnreadCountVO();
        vo.setLikeUnread(user.getLikeUnread());
        vo.setCommentUnread(user.getCommentUnread());
        vo.setLetterUnread(user.getLetterUnread());
        vo.setNotificationUnread(user.getNotificationUnread());
        vo.setTotalUnread(user.getLikeUnread() + user.getCommentUnread() + user.getLetterUnread() + user.getNotificationUnread());
        
        return vo;
    }
}