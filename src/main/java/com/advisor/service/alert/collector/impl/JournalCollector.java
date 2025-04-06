package com.advisor.service.alert.collector.impl;

import com.advisor.entity.journal.Journal;
import com.advisor.mapper.journal.JournalMapper;
import com.advisor.service.alert.collector.DataSourceCollector;
import com.advisor.service.alert.collector.DataSourceType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日记内容数据收集器
 */
@Component
public class JournalCollector implements DataSourceCollector<Journal> {
    
    @Autowired
    private JournalMapper journalMapper;
    
    @Override
    public List<Journal> collectData(String userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<Journal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Journal::getUserId, userId);
        
        if (startTime != null) {
            queryWrapper.ge(Journal::getCreateTime, startTime);
        }
        
        if (endTime != null) {
            queryWrapper.le(Journal::getCreateTime, endTime);
        }
        
        queryWrapper.orderByDesc(Journal::getCreateTime);
        
        return journalMapper.selectList(queryWrapper);
    }
    
    @Override
    public DataSourceType getType() {
        return DataSourceType.JOURNAL;
    }
} 