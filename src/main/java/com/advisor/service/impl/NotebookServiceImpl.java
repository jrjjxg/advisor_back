package com.advisor.service.impl;

import com.advisor.dto.NotebookEntryDTO;
import com.advisor.entity.NotebookEntry;
import com.advisor.mapper.NotebookMapper;
import com.advisor.service.NotebookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 笔记本服务实现类
 */
@Service
public class NotebookServiceImpl extends ServiceImpl<NotebookMapper, NotebookEntry> implements NotebookService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public boolean saveNotebookEntry(NotebookEntryDTO dto) {
        NotebookEntry entity = new NotebookEntry();
        BeanUtils.copyProperties(dto, entity);
        
        // 设置默认值
        if (entity.getContentType() == null || entity.getContentType().trim().isEmpty()) {
            entity.setContentType("未分类");
        }
        if (entity.getIsFavorite() == null) {
            entity.setIsFavorite(false);
        }
        entity.setSavedAt(LocalDateTime.now());
        
        return this.save(entity);
    }

    @Override
    public List<NotebookEntryDTO> getNotebookEntriesByUserId(String userId) {
        LambdaQueryWrapper<NotebookEntry> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NotebookEntry::getUserId, userId)
                .orderByDesc(NotebookEntry::getSavedAt);
        
        return this.list(queryWrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NotebookEntryDTO> getNotebookEntriesByPage(String userId, Integer page, Integer pageSize) {
        Page<NotebookEntry> entityPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<NotebookEntry> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NotebookEntry::getUserId, userId)
                .orderByDesc(NotebookEntry::getSavedAt);
        
        Page<NotebookEntry> resultPage = this.page(entityPage, queryWrapper);
        
        // 转换为DTO
        Page<NotebookEntryDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<NotebookEntryDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(records);
        
        return dtoPage;
    }

    @Override
    public NotebookEntryDTO getNotebookEntryDetail(Long id, String userId) {
        LambdaQueryWrapper<NotebookEntry> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NotebookEntry::getId, id)
                .eq(NotebookEntry::getUserId, userId);
        
        NotebookEntry entity = this.getOne(queryWrapper);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    public boolean updateNotebookEntry(NotebookEntryDTO dto) {
        NotebookEntry entity = new NotebookEntry();
        BeanUtils.copyProperties(dto, entity);
        
        // 确保只能更新自己的笔记
        LambdaQueryWrapper<NotebookEntry> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NotebookEntry::getId, entity.getId())
                .eq(NotebookEntry::getUserId, entity.getUserId());
        
        return this.update(entity, queryWrapper);
    }

    @Override
    public boolean deleteNotebookEntry(Long id, String userId) {
        LambdaQueryWrapper<NotebookEntry> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NotebookEntry::getId, id)
                .eq(NotebookEntry::getUserId, userId);
        
        return this.remove(queryWrapper);
    }
    
    /**
     * 将实体转换为DTO
     */
    private NotebookEntryDTO convertToDTO(NotebookEntry entity) {
        NotebookEntryDTO dto = new NotebookEntryDTO();
        BeanUtils.copyProperties(entity, dto);
        
        // 格式化日期时间为字符串
        if (entity.getSavedAt() != null) {
            dto.setSavedAtStr(entity.getSavedAt().format(DATE_FORMATTER));
        }
        
        return dto;
    }
} 