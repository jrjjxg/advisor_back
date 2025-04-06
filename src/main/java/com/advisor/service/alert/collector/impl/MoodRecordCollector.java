package com.advisor.service.alert.collector.impl;

import com.advisor.entity.mood.MoodRecord;
import com.advisor.mapper.mood.MoodRecordMapper;
import com.advisor.service.alert.collector.DataSourceCollector;
import com.advisor.service.alert.collector.DataSourceType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 情绪记录数据收集器
 */
@Component
public class MoodRecordCollector implements DataSourceCollector<MoodRecord> {
    
    @Autowired
    private MoodRecordMapper moodRecordMapper;
    
    @Override
    public List<MoodRecord> collectData(String userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<MoodRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MoodRecord::getUserId, userId);
        
        if (startTime != null) {
            queryWrapper.ge(MoodRecord::getCreateTime, startTime);
        }
        
        if (endTime != null) {
            queryWrapper.le(MoodRecord::getCreateTime, endTime);
        }
        
        queryWrapper.orderByDesc(MoodRecord::getCreateTime);
        
        return moodRecordMapper.selectList(queryWrapper);
    }
    
    @Override
    public DataSourceType getType() {
        return DataSourceType.MOOD_RECORD;
    }
} 