package com.advisor.util;

import org.springframework.stereotype.Component;
import com.hankcs.hanlp.HanLP;
import java.util.*;

@Component
public class KeywordExtractor {
    
    /**
     * 使用的TextRank算法提取关键词
     * TextRank是一种基于图的排序算法
     */
    public String extract(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        // 使用TextRank算法提取关键词（返回前10个）
        List<String> keywordList = HanLP.extractKeyword(text, 10);
        return String.join(",", keywordList);

    }
} 