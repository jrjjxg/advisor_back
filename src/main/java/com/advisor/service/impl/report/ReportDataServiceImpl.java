package com.advisor.service.impl.report;

import com.advisor.mapper.journal.JournalMapper;
import com.advisor.mapper.mood.MoodRecordMapper;
import com.advisor.mapper.test.TestResultMapper;
import com.advisor.service.report.ReportDataService;
import com.advisor.vo.report.JournalDataVO;
import com.advisor.vo.report.MoodDataVO;
import com.advisor.vo.report.TestResultDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportDataServiceImpl implements ReportDataService {

    @Autowired
    private MoodRecordMapper moodRecordMapper;
    
    @Autowired
    private JournalMapper journalMapper;
    
    @Autowired
    private TestResultMapper testResultMapper;

    @Override
    public MoodDataVO getMoodData(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        MoodDataVO moodData = new MoodDataVO();
        
        // 获取情绪统计数据
        List<Map<String, Object>> moodStats = moodRecordMapper.getMoodStatsByDateRange(userId, startDate, endDate);
        moodData.setDailyMoods(moodStats);
        
        // 计算平均情绪分数
        double totalScore = 0;
        int count = 0;
        for (Map<String, Object> stat : moodStats) {
            Number scoreObj = (Number) stat.get("avg_score");
            if (scoreObj != null) {
                totalScore += scoreObj.doubleValue();
                count++;
            }
        }
        moodData.setAverageMoodScore(count > 0 ? totalScore / count : 0);
        
        // 计算情绪类型分布
        List<Map<String, Object>> emotionTypes = moodRecordMapper.getEmotionTypeDistribution(userId, startDate, endDate);
        moodData.setEmotionTypeDistribution(emotionTypes);
        
        try {
            // 获取最常用的情绪标签 - 注意键名是tag_name而不是tags
            List<Map<String, Object>> commonTags = moodRecordMapper.getCommonEmotionTags(userId, startDate, endDate);
            // 如果前端期望keys为"tags"和"count"，可以在这里转换
            List<Map<String, Object>> formattedTags = commonTags.stream().map(tag -> {
                Map<String, Object> formatted = new HashMap<>();
                formatted.put("tags", tag.get("tag_name"));
                formatted.put("count", tag.get("count"));
                return formatted;
            }).collect(Collectors.toList());
            moodData.setCommonTags(formattedTags);
        } catch (Exception e) {
            // 记录错误但不中断流程
            System.err.println("获取情绪标签失败: " + e.getMessage());
            moodData.setCommonTags(new ArrayList<>());
        }
        
        return moodData;
    }

    @Override
    public JournalDataVO getJournalData(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        JournalDataVO journalData = new JournalDataVO();
        
        // 获取日记统计数据
        List<Map<String, Object>> journalStats = journalMapper.getJournalStatsByDateRange(userId, startDate, endDate);
        journalData.setDailyStats(journalStats);
        
        // 计算总日记数
        int totalJournals = 0;
        if (journalStats != null) {
            for (Map<String, Object> stat : journalStats) {
                int count = ((Number) stat.get("count")).intValue();
                totalJournals += count;
            }
        }
        journalData.setTotalJournals(totalJournals);
        
        // 获取平均字数
        if (totalJournals > 0) {
            Map<String, Object> avgWordCountMap = journalMapper.getAverageWordCount(userId, startDate, endDate);
            if (avgWordCountMap != null && avgWordCountMap.get("avg_word_count") != null) {
                journalData.setAverageWordCount(((Number) avgWordCountMap.get("avg_word_count")).doubleValue());
            } else {
                journalData.setAverageWordCount(0);
            }
        } else {
            journalData.setAverageWordCount(0);
        }
        
        // 获取关联心情标签统计，替代原来的情绪标签统计
        List<Map<String, Object>> moodTags = journalMapper.getRelatedMoodStatsByDateRange(userId, startDate, endDate);
        journalData.setMoodTagStats(moodTags);
        
        return journalData;
    }

    @Override
    public TestResultDataVO getTestResultData(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        TestResultDataVO testData = new TestResultDataVO();
        
        // 获取测试次数统计
        int testCount = testResultMapper.countTestsByDateRange(userId, startDate, endDate);
        testData.setTotalTests(testCount);
        
        // 获取测试类型分布
        List<Map<String, Object>> testTypeStats = testResultMapper.getTestTypeDistribution(userId, startDate, endDate);
        testData.setTestTypeDistribution(testTypeStats);
        
        // 获取各类型测试的分数变化趋势（最多获取前3种测试类型）
        if (testTypeStats.size() > 0) {
            int limit = Math.min(3, testTypeStats.size());
            Map<String, List<Map<String, Object>>> scoreChanges = new HashMap<>();
            
            for (int i = 0; i < limit; i++) {
                String testTypeId = (String) testTypeStats.get(i).get("test_type_id");
                List<Map<String, Object>> scores = testResultMapper.getTestScoresByType(userId, testTypeId, startDate, endDate);
                scoreChanges.put(testTypeId, scores);
            }
            
            testData.setScoreChanges(scoreChanges);
        }
        
        return testData;
    }

    @Override
    public List<Map<String, Object>> getKeywordCloudData(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        // 获取关键词频率
        List<Map<String, Object>> keywordStats = journalMapper.getKeywordFrequency(userId, startDate, endDate);
        
        // 限制返回前50个关键词
        if (keywordStats != null && keywordStats.size() > 50) {
            keywordStats = keywordStats.subList(0, 50);
        } else if (keywordStats == null) {
            keywordStats = Collections.emptyList(); // 返回空列表避免 null
        }
        
        return keywordStats;
    }
} 