package com.advisor.service.impl;

import com.advisor.dto.MoodRecordDTO;
import com.advisor.entity.MoodRecord;
import com.advisor.entity.MoodRecordTag;
import com.advisor.entity.MoodTag;
import com.advisor.mapper.MoodRecordMapper;
import com.advisor.mapper.MoodRecordTagMapper;
import com.advisor.mapper.MoodTagMapper;
import com.advisor.service.MoodService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MoodServiceImpl implements MoodService {
    
    @Autowired
    private MoodRecordMapper moodRecordMapper;
    
    @Autowired
    private MoodTagMapper moodTagMapper;
    
    @Autowired
    private MoodRecordTagMapper moodRecordTagMapper;
    
    @Override
    @Transactional
    public MoodRecordDTO createMoodRecord(MoodRecordDTO moodDTO) {
        // 保存情绪记录
        MoodRecord moodRecord = new MoodRecord();
        moodRecord.setUserId(moodDTO.getUserId());
        moodRecord.setEmotionType(moodDTO.getEmotionType());
        moodRecord.setIntensity(moodDTO.getIntensity());
        moodRecord.setNote(moodDTO.getNote());
        
        moodRecordMapper.insert(moodRecord);
        
        // 处理标签
        if (moodDTO.getTags() != null && !moodDTO.getTags().isEmpty()) {
            for (String tagName : moodDTO.getTags()) {
                // 查找或创建标签
                MoodTag tag = moodTagMapper.selectOne(
                    new LambdaQueryWrapper<MoodTag>().eq(MoodTag::getName, tagName)
                );
                
                if (tag == null) {
                    tag = new MoodTag();
                    tag.setName(tagName);
                    moodTagMapper.insert(tag);
                }
                
                // 创建关联
                MoodRecordTag recordTag = new MoodRecordTag();
                recordTag.setMoodRecordId(moodRecord.getId());
                recordTag.setTagId(tag.getId());
                moodRecordTagMapper.insert(recordTag);
            }
        }
        
        // 返回创建的记录
        return getMoodRecord(moodRecord.getId());
    }
    
    @Override
    public MoodRecordDTO getMoodRecord(Long id) {
        MoodRecord moodRecord = moodRecordMapper.selectById(id);
        if (moodRecord == null) {
            return null;
        }
        
        MoodRecordDTO dto = new MoodRecordDTO();
        BeanUtils.copyProperties(moodRecord, dto);
        
        // 获取标签
        List<String> tags = moodRecordTagMapper.findTagNamesByMoodRecordId(id);
        dto.setTags(tags);
        
        return dto;
    }
    
    @Override
    public Page<MoodRecordDTO> getUserMoodHistory(String userId, int pageNum, int pageSize) {
        // 分页查询情绪记录
        Page<MoodRecord> page = new Page<>(pageNum, pageSize);
        Page<MoodRecord> recordPage = moodRecordMapper.selectPage(page, 
            new LambdaQueryWrapper<MoodRecord>()
                .eq(MoodRecord::getUserId, userId)
                .orderByDesc(MoodRecord::getCreateTime)
        );
        
        // 转换为DTO
        Page<MoodRecordDTO> dtoPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        List<MoodRecordDTO> records = recordPage.getRecords().stream().map(record -> {
            MoodRecordDTO dto = new MoodRecordDTO();
            BeanUtils.copyProperties(record, dto);
            
            // 获取标签
            List<String> tags = moodRecordTagMapper.findTagNamesByMoodRecordId(record.getId());
            dto.setTags(tags);
            
            return dto;
        }).collect(Collectors.toList());
        
        dtoPage.setRecords(records);
        return dtoPage;
    }
    
    @Override
    public List<MoodRecordDTO> getUserMoodByDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // 查询日期范围内的情绪记录
        List<MoodRecord> records = moodRecordMapper.selectList(
            new LambdaQueryWrapper<MoodRecord>()
                .eq(MoodRecord::getUserId, userId)
                .between(MoodRecord::getCreateTime, startDateTime, endDateTime)
                .orderByDesc(MoodRecord::getCreateTime)
        );
        
        // 转换为DTO
        return records.stream().map(record -> {
            MoodRecordDTO dto = new MoodRecordDTO();
            BeanUtils.copyProperties(record, dto);
            
            // 获取标签
            List<String> tags = moodRecordTagMapper.findTagNamesByMoodRecordId(record.getId());
            dto.setTags(tags);
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getMoodAnalytics(String userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // 获取平均情绪强度
        Double avgIntensity = moodRecordMapper.getAverageIntensityByUserIdAndDateRange(
            userId, startDateTime, endDateTime);
        
        // 获取情绪类型统计
        List<Map<String, Object>> emotionStatsList = moodRecordMapper.countEmotionTypesByUserId(userId);
        
        // 转换为前端需要的格式
        Map<String, Long> emotionStats = new HashMap<>();
        for (Map<String, Object> stat : emotionStatsList) {
            String emotionType = (String) stat.get("emotion_type");
            Long count = ((Number) stat.get("count")).longValue();
            emotionStats.put(emotionType, count);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("averageIntensity", avgIntensity != null ? avgIntensity : 0);
        result.put("emotionStats", emotionStats);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        
        return result;
    }
    
    @Override
    public List<String> getAllEmotionTypes() {
        return Arrays.asList(
            "快乐/愉悦", "平静/满足", "焦虑/紧张", "悲伤/低落", 
            "愤怒/烦躁", "疲惫/无力", "中性/平淡"
        );
    }
    
    @Override
    public List<String> getAllTags() {
        List<MoodTag> tags = moodTagMapper.selectList(null);
        return tags.stream()
            .map(MoodTag::getName)
            .collect(Collectors.toList());
    }
}