package com.advisor.service.impl;

import com.advisor.entity.test.TestScoreLevel;
import com.advisor.mapper.test.TestScoreLevelMapper;
import com.advisor.service.test.TestScoreLevelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TestScoreLevelServiceImpl implements TestScoreLevelService {

    @Autowired
    private TestScoreLevelMapper testScoreLevelMapper;
    
    @Override
    public List<TestScoreLevel> getLevelsByTestType(String testTypeId) {
        return testScoreLevelMapper.selectList(
                new LambdaQueryWrapper<TestScoreLevel>()
                        .eq(TestScoreLevel::getTestTypeId, testTypeId)
                        .orderByAsc(TestScoreLevel::getOrderNum)
        );
    }
    
    @Override
    public TestScoreLevel findLevelByScore(String testTypeId, int score) {
        return testScoreLevelMapper.selectOne(
                new LambdaQueryWrapper<TestScoreLevel>()
                        .eq(TestScoreLevel::getTestTypeId, testTypeId)
                        .le(TestScoreLevel::getMinScore, score)
                        .ge(TestScoreLevel::getMaxScore, score)
        );
    }
    
    @Override
    public String saveLevel(TestScoreLevel level) {
        if (level.getId() == null || level.getId().isEmpty()) {
            level.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        testScoreLevelMapper.insert(level);
        return level.getId();
    }
    
    @Override
    public void updateLevel(TestScoreLevel level) {
        testScoreLevelMapper.updateById(level);
    }
    
    @Override
    public void deleteLevel(String levelId) {
        testScoreLevelMapper.deleteById(levelId);
    }
} 