package com.advisor.service.media.impl;

import com.advisor.entity.media.MediaResource;
import com.advisor.entity.media.UserMediaHistory;
import com.advisor.exception.BusinessException;
import com.advisor.mapper.media.MediaResourceMapper;
import com.advisor.mapper.media.UserMediaHistoryMapper;
import com.advisor.service.media.MediaResourceService;
import com.advisor.service.media.UserMediaHistoryService;
import com.advisor.vo.media.MediaResourceVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMediaHistoryServiceImpl implements UserMediaHistoryService {

    @Autowired
    private UserMediaHistoryMapper userMediaHistoryMapper;

    @Autowired
    private MediaResourceMapper mediaResourceMapper;

    @Autowired
    private MediaResourceService mediaResourceService;

    @Override
    @Transactional
    public void recordProgress(String userId, String mediaId, Integer progress, Boolean isCompleted) {
        // 检查媒体资源是否存在
        MediaResource media = mediaResourceMapper.selectById(mediaId);
        if (media == null) {
            throw new BusinessException(404, "媒体资源不存在");
        }

        // 查找现有的播放记录
        UserMediaHistory history = userMediaHistoryMapper.selectByUserIdAndMediaId(userId, mediaId);

        if (history == null) {
            // 创建新记录
            history = new UserMediaHistory();
            history.setUserId(userId);
            history.setMediaId(mediaId);
            history.setProgress(progress);
            history.setIsCompleted(isCompleted ? 1 : 0);
            history.setCreateTime(LocalDateTime.now());
            history.setUpdateTime(LocalDateTime.now());
            userMediaHistoryMapper.insert(history);
        } else {
            // 更新现有记录
            history.setProgress(progress);
            history.setIsCompleted(isCompleted ? 1 : 0);
            history.setUpdateTime(LocalDateTime.now());
            userMediaHistoryMapper.updateById(history);
        }
    }

    @Override
    public Page<MediaResourceVO> getUserHistory(String userId, int pageNum, int pageSize) {
        // 分页查询历史记录
        Page<UserMediaHistory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserMediaHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMediaHistory::getUserId, userId)
               .orderByDesc(UserMediaHistory::getUpdateTime);

        Page<UserMediaHistory> historyPage = userMediaHistoryMapper.selectPage(page, wrapper);

        // 转换为MediaResourceVO
        Page<MediaResourceVO> voPage = new Page<>();
        voPage.setTotal(historyPage.getTotal());
        voPage.setCurrent(historyPage.getCurrent());
        voPage.setSize(historyPage.getSize());

        List<MediaResourceVO> voList = historyPage.getRecords().stream()
                .map(history -> mediaResourceService.getMediaResourceDetail(history.getMediaId(), userId))
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Integer getProgress(String userId, String mediaId) {
        UserMediaHistory history = userMediaHistoryMapper.selectByUserIdAndMediaId(userId, mediaId);
        return history != null ? history.getProgress() : 0;
    }

    @Override
    @Transactional
    public void clearHistory(String userId, String mediaId) {
        LambdaQueryWrapper<UserMediaHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMediaHistory::getUserId, userId);
        
        if (mediaId != null) {
            wrapper.eq(UserMediaHistory::getMediaId, mediaId);
        }
        
        userMediaHistoryMapper.delete(wrapper);
    }
} 