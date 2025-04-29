package com.advisor.service.journal;

import com.advisor.entity.journal.Journal;
import com.advisor.vo.journal.JournalRequest;
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
     * 获取用户日记统计概览
     */
    Map<String, Object> getJournalStats(String userId);
    

    /**
     * 获取用户在指定日期范围内有日记的日期列表
     */
    List<String> getJournalDatesInRange(String userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取用户日记关键词云数据
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 关键词数量限制
     * @return 关键词及其频率的列表
     */
    List<Map<String, Object>> getKeywordCloudData(String userId, LocalDateTime startDate, LocalDateTime endDate, Integer limit);
    
    /**
     * 保存AI伙伴的回复
     * @param journalId 日记ID
     * @param aiResponse AI回复内容
     * @param userId 用户ID (用于权限校验)
     */
    void saveAiResponse(String journalId, String aiResponse, String userId);
} 