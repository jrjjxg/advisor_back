package com.advisor.service.video;

import com.advisor.entity.video.UserViewHistory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

// 用户观看历史服务接口
public interface UserViewHistoryService extends IService<UserViewHistory> {
    // 保存或更新观看进度
    void saveViewProgress(String userId, String videoId, Integer progress);
    
    // 获取用户观看历史
    List<UserViewHistory> getUserViewHistory(String userId);
}