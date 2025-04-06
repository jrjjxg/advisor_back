package com.advisor.service.report;

import com.advisor.vo.report.MoodDataVO;
import com.advisor.vo.report.JournalDataVO;
import com.advisor.vo.report.TestResultDataVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public interface ReportDataService {
    /**
     * 获取情绪记录数据
     */
    MoodDataVO getMoodData(String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取日记数据
     */
    JournalDataVO getJournalData(String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取测试结果数据
     */
    TestResultDataVO getTestResultData(String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取关键词云数据
     */
    List<Map<String, Object>> getKeywordCloudData(String userId, LocalDateTime startDate, LocalDateTime endDate);
} 