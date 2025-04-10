package com.advisor.service.media;

import com.advisor.dto.media.MediaResourceDTO;
import com.advisor.vo.media.MediaResourceVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 媒体资源服务接口
 */
public interface MediaResourceService {
    
    /**
     * 上传媒体资源
     * @param file 媒体文件
     * @param title 标题
     * @param categoryId 分类ID
     * @param mediaType 媒体类型：1=视频，2=音频
     * @param description 描述（可选）
     * @param coverFile 封面文件（可选）
     * @return 资源ID
     */
    String uploadMediaResource(MultipartFile file, String title, String categoryId, 
                             Integer mediaType, String description, MultipartFile coverFile) throws IOException;
    
    /**
     * 上传媒体文件到云存储
     * @param file 媒体文件
     * @param mediaType 媒体类型：1=视频，2=音频
     * @return 云存储中的文件路径
     */
    String uploadMediaFile(MultipartFile file, Integer mediaType) throws IOException;
    
    /**
     * 上传封面文件到云存储
     * @param file 封面文件
     * @return 云存储中的文件路径
     */
    String uploadCoverFile(MultipartFile file) throws IOException;
    
    /**
     * 更新媒体资源信息
     * @param dto 媒体资源DTO
     */
    void updateMediaResource(MediaResourceDTO dto);
    
    /**
     * 删除媒体资源
     * @param id 资源ID
     */
    void deleteMediaResource(String id);
    
    /**
     * 获取媒体资源详情
     * @param id 资源ID
     * @param userId 用户ID（可选，用于获取收藏状态和播放进度）
     * @return 媒体资源VO
     */
    MediaResourceVO getMediaResourceDetail(String id, String userId);
    
    /**
     * 分页查询媒体资源列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param title 标题（可选，模糊查询）
     * @param categoryId 分类ID（可选）
     * @param mediaType 媒体类型（可选）
     * @param status 状态（可选）
     * @param userId 用户ID（可选，用于获取收藏状态和播放进度）
     * @return 分页结果
     */
    Page<MediaResourceVO> getMediaResourceList(int pageNum, int pageSize, String title, 
                                             String categoryId, Integer mediaType, 
                                             Integer status, String userId);
    
    /**
     * 根据分类获取已发布的音频资源
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param categoryId 分类ID
     * @return 分页的音频资源列表
     */
    Page<MediaResourceVO> getPublishedAudiosByCategory(int pageNum, int pageSize, String categoryId);
    
    /**
     * 更新媒体资源状态
     * @param id 资源ID
     * @param status 状态
     */
    void updateMediaResourceStatus(String id, Integer status);
    
    /**
     * 增加播放次数
     * @param id 资源ID
     */
    void incrementViews(String id);
    
    /**
     * 获取推荐媒体资源
     * @param limit 数量限制
     * @param mediaType 媒体类型（可选）
     * @param userId 用户ID（可选，用于获取收藏状态和播放进度）
     * @return 推荐列表
     */
    List<MediaResourceVO> getRecommendedMediaResources(int limit, Integer mediaType, String userId);
    
    /**
     * 获取媒体资源URL
     * @param resourceUrl 资源相对路径
     * @return 完整URL
     */
    String getMediaResourceUrl(String resourceUrl);
    
    /**
     * 保存媒体播放进度
     * @param userId 用户ID
     * @param mediaId 媒体资源ID
     * @param progress 播放进度（秒）
     * @param completed 是否已完成播放
     */
    void saveProgress(String userId, String mediaId, Integer progress, Boolean completed);
} 