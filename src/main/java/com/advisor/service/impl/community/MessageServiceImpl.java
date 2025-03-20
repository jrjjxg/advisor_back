package com.advisor.service.impl.community;


import com.advisor.dto.MessageSendRequest;
import com.advisor.entity.base.User;
import com.advisor.entity.community.PrivateMessage;
import com.advisor.mapper.base.UserMapper;
import com.advisor.mapper.community.PrivateMessageMapper;
import com.advisor.service.community.MessageService;
import com.advisor.vo.community.MessageSessionVO;
import com.advisor.vo.community.PrivateMessageVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息服务实现类
 */
@Slf4j
@Service
public class MessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage> implements MessageService {
    
    @Autowired
    private PrivateMessageMapper privateMessageMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public String sendPrivateMessage(MessageSendRequest request, String userId) {
        // 查询目标用户是否存在
        User targetUser = userMapper.selectById(request.getToUserId());
        if (targetUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 创建私信
        PrivateMessage message = new PrivateMessage();
        message.setFromUserId(userId);
        message.setToUserId(request.getToUserId());
        message.setContent(request.getContent());
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        
        // 保存私信
        privateMessageMapper.insert(message);
        
        // 更新未读消息数
        User user = userMapper.selectById(request.getToUserId());
        user.setLetterUnread(user.getLetterUnread() + 1);
        userMapper.updateById(user);
        
        return message.getId();
    }
    
    @Override
    public Page<MessageSessionVO> getMessageSessions(String userId, Integer pageNum, Integer pageSize) {
        // 查询与当前用户有私信往来的用户ID列表
        LambdaQueryWrapper<PrivateMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PrivateMessage::getFromUserId, userId)
                .or()
                .eq(PrivateMessage::getToUserId, userId);
        
        List<PrivateMessage> allMessages = privateMessageMapper.selectList(queryWrapper);
        
        // 提取会话用户ID
        Set<String> sessionUserIds = new HashSet<>();
        for (PrivateMessage message : allMessages) {
            if (message.getFromUserId().equals(userId)) {
                sessionUserIds.add(message.getToUserId());
            } else {
                sessionUserIds.add(message.getFromUserId());
            }
        }
        
        // 构建会话列表
        List<MessageSessionVO> sessions = new ArrayList<>();
        for (String sessionUserId : sessionUserIds) {
            MessageSessionVO session = new MessageSessionVO();
            
            // 查询用户信息
            User sessionUser = userMapper.selectById(sessionUserId);
            if (sessionUser != null) {
                session.setUserId(sessionUserId);
                session.setUsername(sessionUser.getUsername());
                session.setAvatar(sessionUser.getAvatar());
            }
            
            // 查询最后一条消息
            LambdaQueryWrapper<PrivateMessage> lastMessageQuery = new LambdaQueryWrapper<>();
            lastMessageQuery.and(wrapper -> wrapper
                    .eq(PrivateMessage::getFromUserId, userId).eq(PrivateMessage::getToUserId, sessionUserId)
                    .or()
                    .eq(PrivateMessage::getFromUserId, sessionUserId).eq(PrivateMessage::getToUserId, userId)
            ).orderByDesc(PrivateMessage::getCreateTime).last("LIMIT 1");
            
            PrivateMessage lastMessage = privateMessageMapper.selectOne(lastMessageQuery);
            if (lastMessage != null) {
                session.setLastMessage(lastMessage.getContent());
                session.setLastTime(lastMessage.getCreateTime());
            }
            
            // 查询未读消息数
            LambdaQueryWrapper<PrivateMessage> unreadQuery = new LambdaQueryWrapper<>();
            unreadQuery.eq(PrivateMessage::getFromUserId, sessionUserId)
                    .eq(PrivateMessage::getToUserId, userId)
                    .eq(PrivateMessage::getIsRead, 0);
            
            int unreadCount = privateMessageMapper.selectCount(unreadQuery).intValue();
            session.setUnreadCount(unreadCount);
            
            sessions.add(session);
        }
        
        // 按最后消息时间排序
        sessions.sort((s1, s2) -> {
            if (s1.getLastTime() == null) return 1;
            if (s2.getLastTime() == null) return -1;
            return s2.getLastTime().compareTo(s1.getLastTime());
        });
        
        // 分页处理
        int total = sessions.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<MessageSessionVO> pagedSessions = fromIndex < toIndex
                ? sessions.subList(fromIndex, toIndex)
                : new ArrayList<>();
        
        Page<MessageSessionVO> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(pagedSessions);
        
        return page;
    }
    
    @Override
    public Page<PrivateMessageVO> getPrivateMessages(String targetUserId, String userId, Integer pageNum, Integer pageSize) {
        // 查询与目标用户的私信记录
        LambdaQueryWrapper<PrivateMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
                .eq(PrivateMessage::getFromUserId, userId).eq(PrivateMessage::getToUserId, targetUserId)
                .or()
                .eq(PrivateMessage::getFromUserId, targetUserId).eq(PrivateMessage::getToUserId, userId)
        ).orderByDesc(PrivateMessage::getCreateTime);
        
        Page<PrivateMessage> page = new Page<>(pageNum, pageSize);
        Page<PrivateMessage> messagePage = privateMessageMapper.selectPage(page, queryWrapper);
        
        // 转换为VO
        Page<PrivateMessageVO> voPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
        
        List<PrivateMessageVO> voList = messagePage.getRecords().stream()
                .map(message -> {
                    PrivateMessageVO vo = new PrivateMessageVO();
                    BeanUtils.copyProperties(message, vo);
                    
                    // 设置是否是自己发送的
                    vo.setIsSelf(message.getFromUserId().equals(userId));
                    
                    // 查询发送者信息
                    User fromUser = userMapper.selectById(message.getFromUserId());
                    if (fromUser != null) {
                        vo.setFromUsername(fromUser.getUsername());
                        vo.setFromAvatar(fromUser.getAvatar());
                    }
                    
                    return vo;
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        // 标记为已读
        markAllMessagesRead(targetUserId, userId);
        
        return voPage;
    }
    
    @Override
    public void markMessageRead(String messageId, String userId) {
        privateMessageMapper.markAsRead(messageId, userId);
        
        // 更新未读消息数
        User user = userMapper.selectById(userId);
        if (user.getLetterUnread() > 0) {
            user.setLetterUnread(user.getLetterUnread() - 1);
            userMapper.updateById(user);
        }
    }
    
    @Override
    public int markAllMessagesRead(String targetUserId, String userId) {
        int count = privateMessageMapper.markAllAsRead(userId, targetUserId);
        
        // 更新未读消息数
        if (count > 0) {
            User user = userMapper.selectById(userId);
            int newUnread = Math.max(0, user.getLetterUnread() - count);
            user.setLetterUnread(newUnread);
            userMapper.updateById(user);
        }
        
        return count;
    }
}