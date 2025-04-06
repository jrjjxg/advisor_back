package com.advisor.service.journal;

import com.advisor.entity.journal.Journal;
import com.advisor.vo.journal.JournalRequest;
import com.advisor.vo.journal.JournalStatsVO;
import com.advisor.vo.journal.JournalVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface JournalService {
    /**
     * 保存日记
     */
    String saveJournal(JournalRequest journalRequest, String userId);
    
    /**
     * 更新日记
     */
    void updateJournal(JournalVO journalVO);

    /**
     * 删除日记（通过ID和用户ID）
     */
    boolean deleteJournal(String journalId, String userId);
    

    /**
     * 获取日记详情（通过ID和用户ID，带权限检查）
     */
    Journal getJournalDetail(String journalId, String userId);

    /**
     * 获取用户日记列表（分页）
     */
    Page<JournalVO> getUserJournals(String userId, int pageNum, int pageSize, 
                                   String keyword, String mood, Boolean hasImage, 
                                   Boolean isPrivate, String date, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取用户所有日记列表
     */
    List<Journal> getUserJournals(String userId);
    
    /**
     * 提取日记关键词
     */
    void extractKeywords(String journalId);
    
    /**
     * 获取用户日记统计概览
     */
    Map<String, Object> getJournalStats(String userId);
    
    /**
     * 获取用户日记详细统计数据
     */
    JournalStatsVO getUserJournalStats(String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 关联心情记录
     */
    boolean linkMoodRecord(String journalId, String moodId, String userId);
    
    /**
     * 取消关联心情记录
     */
    boolean unlinkMoodRecord(String journalId, String userId);
    
    /**
     * 根据心情ID获取相关日记
     */
    List<JournalVO> getJournalsByMoodId(String moodId, String userId);

    /**
     * 获取用户日记写作趋势数据
     */
    Map<String, Object> getJournalTrends(String userId, String period, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取用户日记内容分析
     */
    Map<String, Object> getJournalContentAnalysis(String userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取用户在指定日期范围内有日记的日期列表
     */
    List<String> getJournalDatesInRange(String userId, LocalDateTime startDate, LocalDateTime endDate);
} 