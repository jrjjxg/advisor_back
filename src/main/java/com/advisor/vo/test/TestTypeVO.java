package com.advisor.vo.test;

import lombok.Data;
import java.time.LocalDateTime;

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
    private Integer estimatedTime;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 新增字段：测试完成人数
    private Integer testCount;
}