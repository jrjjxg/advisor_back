package com.advisor.service.media;

import com.advisor.vo.media.MediaResourceVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 用户媒体收藏服务接口
 */
public interface UserMediaFavoriteService {
    
    /**
     * 收藏媒体资源
     * @param userId 用户ID
     * @param mediaId 媒体资源ID
     * @return 是否收藏成功
     */
    boolean favoriteMedia(String userId, String mediaId);
    
    /**
     * 取消收藏媒体资源
     * @param userId 用户ID
     * @param mediaId 媒体资源ID
     * @return 是否取消成功
     */
    boolean unfavoriteMedia(String userId, String mediaId);
    
    /**
     * 切换收藏状态
     * @param userId 用户ID
     * @param mediaId 媒体资源ID
     * @return 切换后的收藏状态，true为已收藏，false为未收藏
     */
    boolean toggleFavorite(String userId, String mediaId);
    
    /**
     * 检查用户是否收藏了指定媒体
     * @param userId 用户ID
     * @param mediaId 媒体资源ID
     * @return 是否已收藏
     */
    boolean isFavorited(String userId, String mediaId);
    
    /**
     * 获取用户收藏的媒体列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<MediaResourceVO> getUserFavorites(String userId, int pageNum, int pageSize);
} 