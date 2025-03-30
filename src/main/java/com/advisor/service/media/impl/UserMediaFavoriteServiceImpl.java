package com.advisor.service.media.impl;

import com.advisor.entity.media.MediaResource;
import com.advisor.entity.media.UserMediaFavorite;
import com.advisor.exception.BusinessException;
import com.advisor.mapper.media.MediaResourceMapper;
import com.advisor.mapper.media.UserMediaFavoriteMapper;
import com.advisor.service.media.MediaResourceService;
import com.advisor.service.media.UserMediaFavoriteService;
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
public class UserMediaFavoriteServiceImpl implements UserMediaFavoriteService {

    @Autowired
    private UserMediaFavoriteMapper userMediaFavoriteMapper;

    @Autowired
    private MediaResourceMapper mediaResourceMapper;

    @Autowired
    private MediaResourceService mediaResourceService;

    @Override
    @Transactional
    public boolean favoriteMedia(String userId, String mediaId) {
        // 检查媒体资源是否存在
        MediaResource media = mediaResourceMapper.selectById(mediaId);
        if (media == null) {
            throw new BusinessException(404, "媒体资源不存在");
        }

        // 检查是否已经收藏
        if (isFavorited(userId, mediaId)) {
            return false;
        }

        // 创建收藏记录
        UserMediaFavorite favorite = new UserMediaFavorite();
        favorite.setUserId(userId);
        favorite.setMediaId(mediaId);
        favorite.setCreateTime(LocalDateTime.now());

        userMediaFavoriteMapper.insert(favorite);
        return true;
    }

    @Override
    @Transactional
    public boolean unfavoriteMedia(String userId, String mediaId) {
        LambdaQueryWrapper<UserMediaFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMediaFavorite::getUserId, userId)
               .eq(UserMediaFavorite::getMediaId, mediaId);

        return userMediaFavoriteMapper.delete(wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean toggleFavorite(String userId, String mediaId) {
        if (isFavorited(userId, mediaId)) {
            unfavoriteMedia(userId, mediaId);
            return false;
        } else {
            favoriteMedia(userId, mediaId);
            return true;
        }
    }

    @Override
    public boolean isFavorited(String userId, String mediaId) {
        return userMediaFavoriteMapper.selectByUserIdAndMediaId(userId, mediaId) != null;
    }

    @Override
    public Page<MediaResourceVO> getUserFavorites(String userId, int pageNum, int pageSize) {
        // 分页查询收藏记录
        Page<UserMediaFavorite> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserMediaFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMediaFavorite::getUserId, userId)
               .orderByDesc(UserMediaFavorite::getCreateTime);

        Page<UserMediaFavorite> favoritePage = userMediaFavoriteMapper.selectPage(page, wrapper);

        // 转换为MediaResourceVO
        Page<MediaResourceVO> voPage = new Page<>();
        voPage.setTotal(favoritePage.getTotal());
        voPage.setCurrent(favoritePage.getCurrent());
        voPage.setSize(favoritePage.getSize());

        List<MediaResourceVO> voList = favoritePage.getRecords().stream()
                .map(favorite -> mediaResourceService.getMediaResourceDetail(favorite.getMediaId(), userId))
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }
} 