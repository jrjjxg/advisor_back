package com.advisor.vo.test;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class TestTypeVO {
    private String id;
    private String name;
    private String description;
    private String icon;
    private String imageUrl; // 确保VO中也有这个字段
    private String category;
    private Integer timeMinutes;
    private Integer questionCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 新增字段：测试完成人数
    private Integer testCount;

    // --- 新增科普字段 ---
    private String theoreticalBasis;
    private String applicationScenarios;
    private String precautions;
    private String detailedInterpretation;
    private String developmentHistory;
    private String relatedStudies;
    // FAQ 在 VO 中使用 List<Map> 类型方便前端
    private List<Map<String, String>> faq;
}