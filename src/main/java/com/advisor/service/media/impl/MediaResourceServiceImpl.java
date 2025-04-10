package com.advisor.service.media.impl;

import com.advisor.dto.media.MediaResourceDTO;
import com.advisor.entity.media.MediaCategory;
import com.advisor.entity.media.MediaResource;
import com.advisor.entity.media.UserMediaFavorite;
import com.advisor.entity.media.UserMediaHistory;
import com.advisor.exception.BusinessException;
import com.advisor.mapper.media.MediaCategoryMapper;
import com.advisor.mapper.media.MediaResourceMapper;
import com.advisor.mapper.media.UserMediaFavoriteMapper;
import com.advisor.mapper.media.UserMediaHistoryMapper;
import com.advisor.service.media.MediaResourceService;
import com.advisor.vo.media.MediaResourceVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 媒体资源服务实现类
 */
@Service
public class MediaResourceServiceImpl implements MediaResourceService {

    private static final Logger logger = LoggerFactory.getLogger(MediaResourceServiceImpl.class);

    @Autowired
    private MediaResourceMapper mediaResourceMapper;

    @Autowired
    private MediaCategoryMapper mediaCategoryMapper;

    @Autowired
    private UserMediaFavoriteMapper userMediaFavoriteMapper;

    @Autowired
    private UserMediaHistoryMapper userMediaHistoryMapper;

    @Value("${qiniu.access-key}")
    private String accessKey;

    @Value("${qiniu.secret-key}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Value("${qiniu.domain}")
    private String domain;

    @Override
    @Transactional
    public String uploadMediaResource(MultipartFile file, String title, String categoryId,
                                    Integer mediaType, String description,
                                    MultipartFile coverFile) throws IOException {
        // 检查分类是否存在
        MediaCategory category = mediaCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }

        // 检查媒体类型是否与分类匹配
        if (!category.getMediaType().equals(mediaType)) {
            throw new BusinessException(400, "媒体类型与分类不匹配");
        }

        // 上传媒体文件
        String resourceUrl = uploadMediaFile(file, mediaType);

        // 上传封面文件（如果有）
        String coverUrl = null;
        if (coverFile != null && !coverFile.isEmpty()) {
            coverUrl = uploadCoverFile(coverFile);
        }

        // 创建媒体资源记录
        MediaResource mediaResource = new MediaResource();
        mediaResource.setTitle(title);
        mediaResource.setDescription(description);
        mediaResource.setCategoryId(categoryId);
        mediaResource.setMediaType(mediaType);
        mediaResource.setResourceUrl(resourceUrl);
        mediaResource.setCoverUrl(coverUrl);
        mediaResource.setStatus(1); // 默认发布状态
        mediaResource.setViews(0);
        mediaResource.setLikes(0);
        mediaResource.setCreateTime(LocalDateTime.now());
        mediaResource.setUpdateTime(LocalDateTime.now());

        // TODO: 如果是视频，可以考虑提取时长信息
        // mediaResource.setDuration(...);

        mediaResourceMapper.insert(mediaResource);

        return mediaResource.getId();
    }

    @Override
    public String uploadMediaFile(MultipartFile file, Integer mediaType) throws IOException {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String folder = mediaType == 1 ? "videos" : "audios";
        String fileKey = folder + "/" + UUID.randomUUID().toString().replace("-", "") + extension;

        // 七牛云配置
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
        UploadManager uploadManager = new UploadManager(cfg);

        try {
            // 创建认证信息
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            // 执行上传
            Response response = uploadManager.put(file.getInputStream(), fileKey, upToken, null, null);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            logger.info("文件上传成功，key: {}", putRet.key);
            return putRet.key;
        } catch (QiniuException ex) {
            logger.error("七牛云上传失败: {}", ex.getMessage());
            throw new BusinessException(500, "文件上传失败: " + ex.getMessage());
        }
    }

    @Override
    public String uploadCoverFile(MultipartFile file) throws IOException {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileKey = "covers/" + UUID.randomUUID().toString().replace("-", "") + extension;

        // 七牛云配置
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);

        try {
            // 创建认证信息
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            // 执行上传
            Response response = uploadManager.put(file.getInputStream(), fileKey, upToken, null, null);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            logger.info("封面上传成功，key: {}", putRet.key);
            return putRet.key;
        } catch (QiniuException ex) {
            logger.error("七牛云上传失败: {}", ex.getMessage());
            throw new BusinessException(500, "封面上传失败: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateMediaResource(MediaResourceDTO dto) {
        MediaResource mediaResource = mediaResourceMapper.selectById(dto.getId());
        if (mediaResource == null) {
            throw new BusinessException(404, "媒体资源不存在");
        }

        // 检查分类是否存在
        if (dto.getCategoryId() != null) {
            MediaCategory category = mediaCategoryMapper.selectById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException(404, "分类不存在");
            }

            // 检查媒体类型是否与分类匹配
            if (dto.getMediaType() != null && !category.getMediaType().equals(dto.getMediaType())) {
                throw new BusinessException(400, "媒体类型与分类不匹配");
            }
        }

        BeanUtils.copyProperties(dto, mediaResource);
        mediaResource.setUpdateTime(LocalDateTime.now());

        mediaResourceMapper.updateById(mediaResource);
    }

    @Override
    @Transactional
    public void deleteMediaResource(String id) {
        MediaResource mediaResource = mediaResourceMapper.selectById(id);
        if (mediaResource == null) {
            throw new BusinessException(404, "媒体资源不存在");
        }

        // TODO: 考虑是否需要从七牛云删除资源文件

        mediaResourceMapper.deleteById(id);

        // 删除相关的收藏和历史记录
        LambdaQueryWrapper<UserMediaFavorite> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.eq(UserMediaFavorite::getMediaId, id);
        userMediaFavoriteMapper.delete(favoriteWrapper);

        LambdaQueryWrapper<UserMediaHistory> historyWrapper = new LambdaQueryWrapper<>();
        historyWrapper.eq(UserMediaHistory::getMediaId, id);
        userMediaHistoryMapper.delete(historyWrapper);
    }

    @Override
    public MediaResourceVO getMediaResourceDetail(String id, String userId) {
        MediaResource mediaResource = mediaResourceMapper.selectById(id);
        if (mediaResource == null) {
            throw new BusinessException(404, "媒体资源不存在");
        }

        // 增加播放次数
        mediaResourceMapper.incrementViews(id);

        return convertToVO(mediaResource, userId);
    }

    @Override
    public Page<MediaResourceVO> getMediaResourceList(int pageNum, int pageSize, String title,
                                                   String categoryId, Integer mediaType,
                                                   Integer status, String userId) {
        LambdaQueryWrapper<MediaResource> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (title != null && !title.isEmpty()) {
            wrapper.like(MediaResource::getTitle, title);
        }

        if (categoryId != null && !categoryId.isEmpty()) {
            wrapper.eq(MediaResource::getCategoryId, categoryId);
        }

        if (mediaType != null) {
            wrapper.eq(MediaResource::getMediaType, mediaType);
        }

        if (status != null) {
            wrapper.eq(MediaResource::getStatus, status);
        } else {
            // 默认只查询已发布的资源
            wrapper.eq(MediaResource::getStatus, 1);
        }

        // 按创建时间降序排序
        wrapper.orderByDesc(MediaResource::getCreateTime);

        // 分页查询
        Page<MediaResource> page = new Page<>(pageNum, pageSize);
        Page<MediaResource> resourcePage = mediaResourceMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<MediaResourceVO> voPage = new Page<>();
        BeanUtils.copyProperties(resourcePage, voPage, "records");

        List<MediaResourceVO> voList = resourcePage.getRecords().stream()
                .map(resource -> convertToVO(resource, userId))
                .collect(Collectors.toList());

        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional
    public void updateMediaResourceStatus(String id, Integer status) {
        MediaResource mediaResource = mediaResourceMapper.selectById(id);
        if (mediaResource == null) {
            throw new BusinessException(404, "媒体资源不存在");
        }

        mediaResource.setStatus(status);
        mediaResource.setUpdateTime(LocalDateTime.now());

        mediaResourceMapper.updateById(mediaResource);
    }

    @Override
    @Transactional
    public void incrementViews(String id) {
        mediaResourceMapper.incrementViews(id);
    }

    @Override
    public List<MediaResourceVO> getRecommendedMediaResources(int limit, Integer mediaType, String userId) {
        LambdaQueryWrapper<MediaResource> wrapper = new LambdaQueryWrapper<>();

        // 只查询已发布的资源
        wrapper.eq(MediaResource::getStatus, 1);

        if (mediaType != null) {
            wrapper.eq(MediaResource::getMediaType, mediaType);
        }

        // 按播放量降序排序
        wrapper.orderByDesc(MediaResource::getViews);

        // 限制数量
        Page<MediaResource> page = new Page<>(1, limit);
        Page<MediaResource> resourcePage = mediaResourceMapper.selectPage(page, wrapper);

        // 转换为VO
        return resourcePage.getRecords().stream()
                .map(resource -> convertToVO(resource, userId))
                .collect(Collectors.toList());
    }

    @Override
    public Page<MediaResourceVO> getPublishedAudiosByCategory(int pageNum, int pageSize, String categoryId) {
        LambdaQueryWrapper<MediaResource> wrapper = new LambdaQueryWrapper<>();

        // 只查询已发布的资源
        wrapper.eq(MediaResource::getStatus, 1);

        // 只查询音频资源（媒体类型为2）
        wrapper.eq(MediaResource::getMediaType, 2);

        // 根据分类ID查询
        if (categoryId != null && !categoryId.isEmpty()) {
            wrapper.eq(MediaResource::getCategoryId, categoryId);
        }

        // 按创建时间降序排序
        wrapper.orderByDesc(MediaResource::getCreateTime);

        // 分页查询
        Page<MediaResource> page = new Page<>(pageNum, pageSize);
        Page<MediaResource> resourcePage = mediaResourceMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<MediaResourceVO> voPage = new Page<>();
        BeanUtils.copyProperties(resourcePage, voPage, "records");

        List<MediaResourceVO> voList = resourcePage.getRecords().stream()
                .map(resource -> convertToVO(resource, null))
                .collect(Collectors.toList());

        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public String getMediaResourceUrl(String resourceUrl) {
        if (resourceUrl == null || resourceUrl.isEmpty()) {
            return null;
        }

        // 如果已经是完整URL，直接返回
        if (resourceUrl.startsWith("http://") || resourceUrl.startsWith("https://")) {
            return resourceUrl;
        }

        // 拼接域名和资源路径
        return domain + "/" + resourceUrl;
    }

    @Override
    public void saveProgress(String userId, String mediaId, Integer progress, Boolean completed) {
        if (userId == null || userId.isEmpty() || mediaId == null || mediaId.isEmpty()) {
            throw new BusinessException(400, "用户ID和媒体ID不能为空");
        }

//        // 查询是否存在历史记录
//        UserMediaHistory history = userMediaHistoryMapper.selectByUserIdAndMediaId(userId, mediaId);
//
//        if (history == null) {
//            // 创建新的历史记录
//            history = new UserMediaHistory();
//            history.setUserId(userId);
//            history.setMediaId(mediaId);
//            history.setProgress(progress != null ? progress : 0);
//            history.setIsCompleted(completed != null && completed ? 1 : 0);
//            history.setLastViewTime(LocalDateTime.now());
//
//            userMediaHistoryMapper.insert(history);
//        } else {
//            // 更新历史记录
//            if (progress != null) {
//                history.setProgress(progress);
//            }
//
//            if (completed != null) {
//                history.setIsCompleted(completed ? 1 : 0);
//            }
//
//            history.setLastViewTime(LocalDateTime.now());
//
//            userMediaHistoryMapper.updateById(history);
//        }
    }

    /**
     * 将实体转换为VO
     */
    private MediaResourceVO convertToVO(MediaResource resource, String userId) {
        MediaResourceVO vo = new MediaResourceVO();
        BeanUtils.copyProperties(resource, vo);

        // 设置资源URL
        vo.setResourceUrl(getMediaResourceUrl(resource.getResourceUrl()));

        // 设置封面URL
        if (resource.getCoverUrl() != null && !resource.getCoverUrl().isEmpty()) {
            vo.setCoverUrl(getMediaResourceUrl(resource.getCoverUrl()));
        }

        // 查询分类信息
        MediaCategory category = mediaCategoryMapper.selectById(resource.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        // 如果有用户ID，查询用户相关信息
        if (userId != null && !userId.isEmpty()) {
            // 查询是否收藏
            UserMediaFavorite favorite = userMediaFavoriteMapper.selectByUserIdAndMediaId(userId, resource.getId());
            vo.setIsFavorite(favorite != null);

            // 查询播放进度
            UserMediaHistory history = userMediaHistoryMapper.selectByUserIdAndMediaId(userId, resource.getId());
            if (history != null) {
                vo.setProgress(history.getProgress());
                vo.setIsCompleted(history.getIsCompleted() == 1);
            } else {
                vo.setProgress(0);
                vo.setIsCompleted(false);
            }
        } else {
            vo.setIsFavorite(false);
            vo.setProgress(0);
            vo.setIsCompleted(false);
        }

        return vo;
    }
}
