package com.advisor.service.impl.video;

import com.advisor.entity.video.UserViewHistory;
import com.advisor.mapper.video.UserViewHistoryMapper;
import com.advisor.service.video.UserViewHistoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 用户观看历史服务实现
@Service
public class UserViewHistoryServiceImpl extends ServiceImpl<UserViewHistoryMapper, UserViewHistory> implements UserViewHistoryService {
    @Autowired
    private UserViewHistoryMapper userViewHistoryMapper;
    
    @Override
    public void saveViewProgress(String userId, String videoId, Integer progress) {
        // 先查询是否存在记录
        QueryWrapper<UserViewHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("video_id", videoId);
        UserViewHistory history = this.getOne(queryWrapper);
        
        if (history == null) {
            // 不存在则新增
            history = new UserViewHistory();
            history.setUserId(userId);
            history.setVideoId(videoId);
            history.setProgress(progress);
            this.save(history);
        } else {
            // 存在则更新
            history.setProgress(progress);
            this.updateById(history);
        }
    }
    
    @Override
    public List<UserViewHistory> getUserViewHistory(String userId) {
        return userViewHistoryMapper.selectHistoryWithVideoByUserId(userId);
    }
}