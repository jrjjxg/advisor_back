package com.advisor.service.impl.journal;

import com.advisor.entity.journal.Journal;
import com.advisor.exception.ForbiddenException;
import com.advisor.exception.ResourceNotFoundException;
import com.advisor.mapper.journal.JournalMapper;
import com.advisor.service.emotion.BaiduEmotionService;
import com.advisor.service.journal.JournalService;
import com.advisor.util.KeywordExtractor;
import com.advisor.vo.journal.JournalRequest;
import com.advisor.vo.journal.JournalVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.temporal.ChronoUnit;

/**
 * 日记服务实现类
 */
@Service
public class JournalServiceImpl implements JournalService {

    @Autowired
    private JournalMapper journalMapper;
    
    @Autowired(required = false)
    private KeywordExtractor keywordExtractor;

    @Autowired
    private BaiduEmotionService emotionService;


    @Override
    @Transactional
    public String saveJournal(JournalRequest journalRequest, String userId) {
        Journal journal;
        
        // 新增或更新日记
        if (StringUtils.isBlank(journalRequest.getId())) {
            // 新增日记
            journal = new Journal();
            journal.setUserId(userId);
            journal.setCreateTime(LocalDateTime.now());
        } else {
            // 修改日记，先查询是否存在
            journal = journalMapper.selectById(journalRequest.getId());
            if (journal == null) {
                throw new ResourceNotFoundException("日记不存在");
            }
            
            // 检查权限
            if (!journal.getUserId().equals(userId)) {
                throw new ForbiddenException("无权操作此日记");
            }
        }
        
        // 设置日记内容
        journal.setTitle(journalRequest.getTitle());
        journal.setContent(journalRequest.getContent());
        journal.setIsPrivate(journalRequest.getIsPrivate());
        journal.setRelatedMoodId(journalRequest.getRelatedMoodId());
        journal.setUpdateTime(LocalDateTime.now());
        
        // 设置主题
        journal.setTheme(journalRequest.getTheme());
        
        // 设置新增的字段
        journal.setImageUrls(journalRequest.getImageUrls());
        journal.setImageCount(journalRequest.getImageCount());
        
        // 计算字数
        if (journalRequest.getWordCount() != null) {
            journal.setWordCount(journalRequest.getWordCount());
        } else if (StringUtils.isNotBlank(journalRequest.getContent())) {
            journal.setWordCount(journalRequest.getContent().length());
        }
        
        // 提取关键词
        if (keywordExtractor != null && StringUtils.isNotBlank(journalRequest.getContent())) {
            List<String> keywords = Collections.singletonList(keywordExtractor.extract(journalRequest.getContent()));
            journal.setKeywords(String.join(",", keywords));
        }
        
        // 添加情感分析
        if (StringUtils.isNotBlank(journalRequest.getContent())) {
            emotionService.analyzeJournal(journal);
        }
        
        // 保存日记
        if (StringUtils.isBlank(journal.getId())) {
            journalMapper.insert(journal);
        } else {
            journalMapper.updateById(journal);
        }
        
        return journal.getId();
    }


    @Override
    public Journal getJournalDetail(String journalId, String userId) {
        Journal journal = journalMapper.selectById(journalId);
        if (journal == null) {
            throw new ResourceNotFoundException("日记不存在");
        }
        
        // 检查权限
        if (!journal.getUserId().equals(userId)) {
            if (journal.getIsPrivate() != null && journal.getIsPrivate() == 1) {
                throw new ForbiddenException("无权查看此私密日记");
            }
        }
        
        return journal;
    }

    @Override
    public boolean deleteJournal(String journalId, String userId) {
        Journal journal = journalMapper.selectById(journalId);
        if (journal == null) {
            return false;
        }
        
        // 检查权限
        if (!journal.getUserId().equals(userId)) {
            throw new ForbiddenException("无权删除此日记");
        }
        
        int result = journalMapper.deleteById(journalId);
        return result > 0;
    }


    @Override
    public Map<String, Object> getJournalStats(String userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计总日记数
        LambdaQueryWrapper<Journal> wrapper = Wrappers.<Journal>lambdaQuery()
                .eq(Journal::getUserId, userId);
        Long totalCount = journalMapper.selectCount(wrapper);
        stats.put("totalCount", totalCount != null ? totalCount.intValue() : 0);
        
        // 统计最近一周日记数
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        LambdaQueryWrapper<Journal> weekWrapper = Wrappers.<Journal>lambdaQuery()
                .eq(Journal::getUserId, userId)
                .ge(Journal::getCreateTime, weekStart);
        Long weekCount = journalMapper.selectCount(weekWrapper);
        stats.put("weekCount", weekCount != null ? weekCount.intValue() : 0);
        
        // 统计关联心情的数据，替代原来的情绪标签统计
        List<Map<String, Object>> moodTags = journalMapper.getRelatedMoodStats(userId);
        stats.put("moodTags", moodTags);
        
        return stats;
    }


    @Override
    public Page<JournalVO> getUserJournals(String userId, int pageNum, int pageSize, 
                                          String keyword, String mood, Boolean hasImage, 
                                          Boolean isPrivate,
                                          String date, LocalDateTime startDate, LocalDateTime endDate) {
        Page<Journal> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Journal> queryWrapper = new LambdaQueryWrapper<Journal>()
                .eq(Journal::getUserId, userId)
                .eq(Journal::getIsDeleted, false);
        
        // 关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Journal::getTitle, keyword)
                    .or()
                    .like(Journal::getContent, keyword));
        }
        
        // 心情筛选
        if (StringUtils.isNotBlank(mood)) {
            queryWrapper.eq(Journal::getRelatedMoodId, mood);
        }
        
        // 图片筛选
        if (hasImage != null && hasImage) {
            queryWrapper.gt(Journal::getImageCount, 0);
        }
        
        // 私密日记筛选
        if (isPrivate != null) {
            queryWrapper.eq(Journal::getIsPrivate, isPrivate ? 1 : 0);
        }
        

        // 日期筛选
        if (StringUtils.isNotBlank(date)) {
            // 将日期字符串转换为LocalDateTime
            try {
                LocalDate localDate = LocalDate.parse(date);
                LocalDateTime dayStart = localDate.atStartOfDay();
                LocalDateTime dayEnd = localDate.atTime(23, 59, 59);
                
                queryWrapper.between(Journal::getCreateTime, dayStart, dayEnd);
            } catch (Exception e) {

            }
        } else if (startDate != null && endDate != null) {
            // 日期范围筛选
            queryWrapper.between(Journal::getCreateTime, startDate, endDate);
        } else if (startDate != null) {
            queryWrapper.ge(Journal::getCreateTime, startDate);
        } else if (endDate != null) {
            queryWrapper.le(Journal::getCreateTime, endDate);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Journal::getCreateTime);
        
        Page<Journal> resultPage = journalMapper.selectPage(page, queryWrapper);
        
        // 转换为VO
        Page<JournalVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<JournalVO> voList = new ArrayList<>();
        
        for (Journal journal : resultPage.getRecords()) {
            JournalVO vo = new JournalVO();
            BeanUtils.copyProperties(journal, vo);
            voList.add(vo);
        }
        
        voPage.setRecords(voList);
        return voPage;
    }


    @Override
    public List<String> getJournalDatesInRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return journalMapper.getDistinctJournalDates(userId, startDate, endDate);
    }


    @Override
    public List<Map<String, Object>> getKeywordCloudData(String userId, LocalDateTime startDate, LocalDateTime endDate, Integer limit) {
        // 获取关键词统计，包含每个关键词及其出现次数
        List<Map<String, Object>> keywordStats = journalMapper.getKeywordsStats(userId, startDate, endDate);
        
        // 如果关键词数据为空，返回空列表
        if (keywordStats == null || keywordStats.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 处理结果，确保字段名称适合前端词云组件
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> stat : keywordStats) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", stat.get("keyword"));  // 关键词名称
            item.put("value", stat.get("count"));   // 关键词出现次数
            result.add(item);
            
            // 限制返回的关键词数量
            if (result.size() >= limit) {
                break;
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public void saveAiResponse(String journalId, String aiResponse, String userId) {
        // 1. 验证日记是否存在以及用户权限
        Journal journal = journalMapper.selectOne(Wrappers.<Journal>lambdaQuery()
                .eq(Journal::getId, journalId)
                .eq(Journal::getUserId, userId));

        if (journal == null) {
            // 可以选择抛出异常或静默失败，这里选择抛出异常
            throw new ResourceNotFoundException("日记不存在或无权访问");
        }

        // 2. 创建只包含更新字段的Journal对象
        Journal updateJournal = new Journal();
        updateJournal.setId(journalId);
        updateJournal.setAiCompanionResponse(aiResponse);
        updateJournal.setUpdateTime(LocalDateTime.now()); // 更新修改时间

        // 3. 更新数据库
        int updatedRows = journalMapper.updateById(updateJournal);
        
        if (updatedRows == 0) {
            // 更新失败，可能并发或其他问题
            // 可以添加日志记录
            System.err.println("Failed to save AI response for journal ID: " + journalId);
        }
    }
} 