package com.advisor.service.mood;

import com.advisor.dto.MoodRecordDTO;
import com.advisor.dto.MoodShareCardDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MoodService {
    
    MoodRecordDTO createMoodRecord(MoodRecordDTO moodDTO);
    
    MoodRecordDTO getMoodRecord(Long id);
    
    Page<MoodRecordDTO> getUserMoodHistory(String userId, int pageNum, int pageSize);
    
    List<MoodRecordDTO> getUserMoodByDateRange(String userId, LocalDate startDate, LocalDate endDate);
    
    Map<String, Object> getMoodAnalytics(String userId, LocalDate startDate, LocalDate endDate);
    
    List<String> getAllEmotionTypes();
    
    List<String> getAllTags();
    
    MoodShareCardDTO generateMoodShareCard(String userId, LocalDate startDate, LocalDate endDate);
}