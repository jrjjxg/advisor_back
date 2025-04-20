package com.advisor.service;

import com.advisor.dto.EmotionRecordDto;
import com.advisor.entity.FacialEmotionHistory;
import com.advisor.mapper.FacialEmotionHistoryMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmotionHistoryService {

    @Autowired
    private FacialEmotionHistoryMapper emotionHistoryMapper; // 注入 Mapper

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional // 推荐加上事务注解
    public FacialEmotionHistory saveEmotionRecord(EmotionRecordDto recordDto) throws Exception {
        FacialEmotionHistory history = new FacialEmotionHistory();
        history.setUserId(recordDto.getUserId());
        history.setPredictedEmotion(recordDto.getPredictedEmotion());
        history.setProbability(recordDto.getProbability());

        // 如果用户选择保存图片并且提供了URL，则保存URL
        if (recordDto.isSaveImage() && recordDto.getImageUrl() != null && !recordDto.getImageUrl().isEmpty()) {
            history.setImageUrl(recordDto.getImageUrl());
        }

        // 设置识别时间
        history.setRecognitionTime(LocalDateTime.now());

        // 将 Map 转换为 JSON 字符串存储
        if (recordDto.getAllProbabilities() != null) {
            history.setAllProbabilitiesJson(objectMapper.writeValueAsString(recordDto.getAllProbabilities()));
        }

        // 使用 Mapper 的 insert 方法
        int insertCount = emotionHistoryMapper.insert(history);

        if (insertCount > 0) {
            // 插入成功后，MP 默认会回填自增 ID 到 history 对象中
            return history;
        } else {
            throw new RuntimeException("Failed to insert emotion history record.");
        }
    }
} 