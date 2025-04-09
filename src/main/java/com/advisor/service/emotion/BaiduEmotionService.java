// src/main/java/com/advisor/service/emotion/BaiduEmotionService.java
package com.advisor.service.emotion;

import com.advisor.common.Result;
import com.advisor.entity.journal.Journal;
import com.advisor.mapper.journal.JournalMapper;
import com.advisor.vo.emotion.EmotionAnalysisResult;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaiduEmotionService {
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private String accessToken;

    @Autowired
    JournalMapper journalMapper;

    @Value("${baidu.aip.api-key}")
    private String apiKey;
    
    private static final String SENTIMENT_URL = "https://aip.baidubce.com/rpc/2.0/nlp/v1/emotion";
    
    public EmotionAnalysisResult analyzeText(String text) {
        try {
            String url = SENTIMENT_URL + "?access_token=" + accessToken;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 构建请求体
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text", text);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            String response = restTemplate.postForObject(url, request, String.class);
            
            // 解析响应
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            
            EmotionAnalysisResult result = new EmotionAnalysisResult();
            
            if (root.has("items") && root.get("items").isArray() && root.get("items").size() > 0) {
                JsonNode item = root.get("items").get(0);
                result.setLabel(item.get("label").asText());
                result.setProb(item.get("prob").asDouble());
                
                if (item.has("subitems") && item.get("subitems").isArray() && item.get("subitems").size() > 0) {
                    JsonNode subitem = item.get("subitems").get(0);
                    result.setSubLabel(subitem.get("label").asText());
                    result.setSubProb(subitem.get("prob").asDouble());
                }
            }
            
            return result;
        } catch (Exception e) {
            return null;
        }
    }
    
    public void analyzeJournal(Journal journal) {
        if (journal == null || StringUtils.isBlank(journal.getContent())) {
            return;
        }
        
        EmotionAnalysisResult result = analyzeText(journal.getContent());
        if (result != null) {
            journal.setEmotionType(result.getLabel());
            journal.setEmotionProb(result.getProb());
            journal.setEmotionSubtype(result.getSubLabel());
            journal.setEmotionSubtypeProb(result.getSubProb());
            journal.setEmotionAnalysisTime(LocalDateTime.now());
            journal.setEmotionAnalysisResult(JSON.toJSONString(result));
        }
    }

    public EmotionAnalysisResult getEmotionAnalysis(String journalId) {
        // 从数据库获取日记内容
        Journal journal = journalMapper.selectById(journalId);
        if (journal == null) {
            throw new RuntimeException("日记不存在");
        }
        
        // 如果日记已经有情感分析结果，直接返回
        if (journal.getEmotionAnalysisResult() != null) {
            return JSON.parseObject(journal.getEmotionAnalysisResult(), EmotionAnalysisResult.class);
        }
        
        // 否则，重新分析
        return analyzeText(journal.getContent());
    }
}