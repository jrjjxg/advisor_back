package com.advisor.service.impl;

import com.advisor.dto.MoodRecordDTO;

import com.advisor.dto.MoodShareCardDTO;
import com.advisor.entity.mood.MoodRecord;
import com.advisor.entity.mood.MoodRecordTag;
import com.advisor.entity.mood.MoodTag;
import com.advisor.mapper.mood.MoodRecordMapper;
import com.advisor.mapper.mood.MoodRecordTagMapper;
import com.advisor.mapper.mood.MoodTagMapper;
import com.advisor.service.mood.MoodService;
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
        
        // 手动设置创建时间和更新时间，解决create_time不能为null的问题
        LocalDateTime now = LocalDateTime.now();
        moodRecord.setCreateTime(now);
        moodRecord.setUpdateTime(now);
        
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

    @Override
    public MoodShareCardDTO generateMoodShareCard(String userId, LocalDate startDate, LocalDate endDate) {
    // 获取日期范围内的情绪记录
    List<MoodRecordDTO> moodRecords = getUserMoodByDateRange(userId, startDate, endDate);
    
    // 如果没有记录，返回空数据
    if (moodRecords.isEmpty()) {
        return MoodShareCardDTO.builder()
                .userId(userId)
                .startDate(startDate)
                .endDate(endDate)
                .totalRecords(0)
                .averageMoodScore(0.0)
                .dominantEmotion("未知")
                .emotionDistribution(Collections.emptyList())
                .moodTrend(Collections.emptyList())
                .build();
    }
    
    // 计算平均情绪强度 (假设您的DTO中有intensity字段而不是moodScore)
    double averageMoodScore = moodRecords.stream()
            .mapToInt(MoodRecordDTO::getIntensity)  // 使用getIntensity替代getMoodScore
            .average()
            .orElse(0);
    
    // 统计情绪类型分布
    Map<String, Long> emotionCounts = moodRecords.stream()
            .collect(Collectors.groupingBy(MoodRecordDTO::getEmotionType, Collectors.counting()));
    
    // 找出主要情绪
    String dominantEmotion = emotionCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("未知");
    
    // 情绪类型对应的颜色
    Map<String, String> emotionColors = Map.of(
            "快乐/愉悦", "#4ADE80",
            "平静/满足", "#60A5FA",
            "焦虑/紧张", "#F97316",
            "悲伤/低落", "#8B5CF6",
            "愤怒/烦躁", "#EF4444",
            "疲惫/无力", "#A855F7",
            "中性/平淡", "#94A3B8"
    );
    
    // 创建情绪分布数据
    List<MoodShareCardDTO.EmotionDistributionDTO> emotionDistribution = emotionCounts.entrySet().stream()
            .map(entry -> {
                String emotion = entry.getKey();
                long count = entry.getValue();
                double percentage = (double) count / moodRecords.size() * 100;
                
                return MoodShareCardDTO.EmotionDistributionDTO.builder()
                        .name(emotion)
                        .percentage(percentage)
                        .color(emotionColors.getOrDefault(emotion, "#94A3B8"))
                        .build();
            })
            .collect(Collectors.toList());
    
    // 按日期分组，计算每天的平均情绪强度
    Map<LocalDate, Double> dailyAverages = moodRecords.stream()
            .collect(Collectors.groupingBy(
                    record -> record.getCreateTime().toLocalDate(),
                    Collectors.averagingInt(MoodRecordDTO::getIntensity)  // 使用getIntensity替代getMoodScore
            ));
    
    // 创建情绪趋势数据
    List<MoodShareCardDTO.DailyMoodDTO> moodTrend = new ArrayList<>();
    LocalDate currentDate = startDate;
    
    while (!currentDate.isAfter(endDate)) {
        Double averageScore = dailyAverages.getOrDefault(currentDate, 0.0);
        
        moodTrend.add(MoodShareCardDTO.DailyMoodDTO.builder()
                .date(currentDate)
                .value(averageScore)
                .build());
        
        currentDate = currentDate.plusDays(1);
    }
    
    // 构建并返回分享卡片数据
    return MoodShareCardDTO.builder()
            .userId(userId)
            .startDate(startDate)
            .endDate(endDate)
            .totalRecords(moodRecords.size())
            .averageMoodScore(averageMoodScore)
            .dominantEmotion(dominantEmotion)
            .emotionDistribution(emotionDistribution)
            .moodTrend(moodTrend)
            .build();
}
}