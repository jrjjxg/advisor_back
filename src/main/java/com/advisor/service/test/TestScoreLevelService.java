package com.advisor.service.test;

import com.advisor.entity.test.TestScoreLevel;
import java.util.List;

public interface TestScoreLevelService {
    // 获取测试类型所有分数级别
    List<TestScoreLevel> getLevelsByTestType(String testTypeId);
    
    // 根据分数查找级别
    TestScoreLevel findLevelByScore(String testTypeId, int score);
    
    // CRUD操作
    String saveLevel(TestScoreLevel level);
    void updateLevel(TestScoreLevel level);
    void deleteLevel(String levelId);
} 