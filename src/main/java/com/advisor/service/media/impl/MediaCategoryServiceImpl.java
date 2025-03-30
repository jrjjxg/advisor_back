package com.advisor.service.media.impl;

import com.advisor.dto.media.MediaCategoryDTO;
import com.advisor.entity.media.MediaCategory;
import com.advisor.exception.BusinessException;
import com.advisor.mapper.media.MediaCategoryMapper;
import com.advisor.service.media.MediaCategoryService;
import com.advisor.vo.media.MediaCategoryVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 媒体分类服务实现类
 */
@Service
public class MediaCategoryServiceImpl implements MediaCategoryService {
    
    @Autowired
    private MediaCategoryMapper mediaCategoryMapper;
    
    @Override
    @Transactional
    public String createCategory(MediaCategoryDTO dto) {
        // 检查同类型下是否有同名分类
        LambdaQueryWrapper<MediaCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MediaCategory::getName, dto.getName())
               .eq(MediaCategory::getMediaType, dto.getMediaType());
        
        if (mediaCategoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(600, "同类型下已存在同名分类");
        }
        
        MediaCategory category = new MediaCategory();
        BeanUtils.copyProperties(dto, category);
        
        mediaCategoryMapper.insert(category);
        
        return category.getId();
    }
    
    @Override
    @Transactional
    public void updateCategory(MediaCategoryDTO dto) {
        MediaCategory category = mediaCategoryMapper.selectById(dto.getId());
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        
        // 检查同类型下是否有同名分类(排除自身)
        LambdaQueryWrapper<MediaCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MediaCategory::getName, dto.getName())
               .eq(MediaCategory::getMediaType, dto.getMediaType())
               .ne(MediaCategory::getId, dto.getId());
        
        if (mediaCategoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(600, "同类型下已存在同名分类");
        }
        
        BeanUtils.copyProperties(dto, category);
        
        mediaCategoryMapper.updateById(category);
    }
    
    @Override
    @Transactional
    public void deleteCategory(String id) {
        MediaCategory category = mediaCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        
        // TODO: 检查分类下是否有媒体资源，有则不允许删除
        
        mediaCategoryMapper.deleteById(id);
    }
    
    @Override
    public MediaCategoryVO getCategoryDetail(String id) {
        MediaCategory category = mediaCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        
        return convertToVO(category);
    }
    
    @Override
    public Page<MediaCategoryVO> getCategoryList(int pageNum, int pageSize, Integer mediaType) {
        LambdaQueryWrapper<MediaCategory> wrapper = new LambdaQueryWrapper<>();
        if (mediaType != null) {
            wrapper.eq(MediaCategory::getMediaType, mediaType);
        }
        wrapper.orderByAsc(MediaCategory::getSort);
        
        Page<MediaCategory> page = new Page<>(pageNum, pageSize);
        Page<MediaCategory> categoryPage = mediaCategoryMapper.selectPage(page, wrapper);
        
        Page<MediaCategoryVO> voPage = new Page<>();
        BeanUtils.copyProperties(categoryPage, voPage, "records");
        
        List<MediaCategoryVO> voList = categoryPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    public List<MediaCategoryVO> getCategoryListByType(Integer mediaType) {
        List<MediaCategory> categories = mediaCategoryMapper.selectListByMediaType(mediaType);
        
        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将实体转换为VO
     */
    private MediaCategoryVO convertToVO(MediaCategory category) {
        MediaCategoryVO vo = new MediaCategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
} 