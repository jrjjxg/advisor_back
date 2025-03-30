package com.advisor.service.media;

import com.advisor.dto.media.MediaCategoryDTO;
import com.advisor.vo.media.MediaCategoryVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 媒体分类服务接口
 */
public interface MediaCategoryService {
    
    /**
     * 创建媒体分类
     * @param dto 分类DTO
     * @return 分类ID
     */
    String createCategory(MediaCategoryDTO dto);
    
    /**
     * 更新媒体分类
     * @param dto 分类DTO
     */
    void updateCategory(MediaCategoryDTO dto);
    
    /**
     * 删除媒体分类
     * @param id 分类ID
     */
    void deleteCategory(String id);
    
    /**
     * 获取媒体分类详情
     * @param id 分类ID
     * @return 分类VO
     */
    MediaCategoryVO getCategoryDetail(String id);
    
    /**
     * 获取媒体分类列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param mediaType 媒体类型，可为null
     * @return 分页结果
     */
    Page<MediaCategoryVO> getCategoryList(int pageNum, int pageSize, Integer mediaType);
    
    /**
     * 根据媒体类型获取分类列表
     * @param mediaType 媒体类型：1=视频，2=音频
     * @return 分类列表
     */
    List<MediaCategoryVO> getCategoryListByType(Integer mediaType);
} 