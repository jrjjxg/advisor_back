package com.advisor.service.alert.collector.impl;

import com.advisor.entity.test.TestResult;
import com.advisor.mapper.test.TestResultMapper;
import com.advisor.service.alert.collector.DataSourceCollector;
import com.advisor.service.alert.collector.DataSourceType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试结果数据收集器
 */
@Component
public class TestResultCollector implements DataSourceCollector<TestResult> {
    
    @Autowired
    private TestResultMapper testResultMapper;
    
    @Override
    public List<TestResult> collectData(String userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<TestResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestResult::getUserId, userId);
        
        if (startTime != null) {
            queryWrapper.ge(TestResult::getCreateTime, startTime);
        }
        
        if (endTime != null) {
            queryWrapper.le(TestResult::getCreateTime, endTime);
        }
        
        queryWrapper.orderByDesc(TestResult::getCreateTime);
        
        return testResultMapper.selectList(queryWrapper);
    }
    
    @Override
    public DataSourceType getType() {
        return DataSourceType.TEST_RESULT;
    }
} 