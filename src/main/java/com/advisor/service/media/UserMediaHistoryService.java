package com.advisor.service.media;

import com.advisor.vo.media.MediaResourceVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 用户媒体历史记录服务接口
 */
public interface UserMediaHistoryService {
    
    /**
     * 记录播放进度
     * @param userId 用户ID
     * @param mediaId 媒体资源ID
     * @param progress 播放进度（秒）
     * @param isCompleted 是否已完成播放
     */
    void recordProgress(String userId, String mediaId, Integer progress, Boolean isCompleted);
    
    /**
     * 获取用户的媒体播放历史
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<MediaResourceVO> getUserHistory(String userId, int pageNum, int pageSize);
    
    /**
     * 获取用户对指定媒体的播放进度
     * @param userId 用户ID
     * @param mediaId 媒体资源ID
     * @return 播放进度（秒），未播放过则返回0
     */
    Integer getProgress(String userId, String mediaId);
    
    /**
     * 清除用户历史记录
     * @param userId 用户ID
     * @param mediaId 媒体资源ID，如果为null则清除所有
     */
    void clearHistory(String userId, String mediaId);
} 