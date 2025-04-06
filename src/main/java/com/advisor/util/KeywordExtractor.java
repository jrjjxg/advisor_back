package com.advisor.util;

import org.springframework.stereotype.Component;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.summary.TextRankKeyword;
import java.util.*;

@Component
public class KeywordExtractor {
    
    /**
     * 使用HanLP的TextRank算法提取关键词
     * TextRank是一种基于图的排序算法，类似于Google的PageRank
     */
    public String extract(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        try {
            // 使用TextRank算法提取关键词（返回前10个）
            List<String> keywordList = HanLP.extractKeyword(text, 10);
            return String.join(",", keywordList);
        } catch (Exception e) {
            // 如果发生异常，回退到基本分词方法
            return extractBySegmentation(text);
        }
    }
    
    /**
     * 备用方法：通过基本分词和词频统计提取关键词
     */
    private String extractBySegmentation(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        // 使用HanLP进行分词
        List<Term> termList = HanLP.segment(text);
        
        // 统计词频
        Map<String, Integer> wordCount = new HashMap<>();
        for (Term term : termList) {
            String word = term.word;
            
            // 过滤掉单字词、数字和常见停用词
            if (word.length() < 2 || isStopWord(word) || isNumeric(word)) {
                continue;
            }
            
            // 词频统计
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        
        // 按词频排序
        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordCount.entrySet());
        sortedWords.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        // 提取前10个关键词
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedWords) {
            if (count >= 10) break;
            if (count > 0) sb.append(",");
            sb.append(entry.getKey());
            count++;
        }
        
        return sb.toString();
    }
    
    private boolean isStopWord(String word) {
        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "的", "了", "和", "是", "就", "都", "而", "及", "与", "这", "那", "有", "在",
                "我", "你", "他", "她", "它", "我们", "你们", "他们", "她们", "它们", "自己",
                "一", "二", "三", "不", "也", "很", "到", "来", "去", "又", "也", "但", "但是"
        ));
        return stopWords.contains(word);
    }
    
    private boolean isNumeric(String word) {
        return word.matches("-?\\d+(\\.\\d+)?");
    }
} 