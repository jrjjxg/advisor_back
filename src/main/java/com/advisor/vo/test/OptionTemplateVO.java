package com.advisor.vo.test;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OptionTemplateVO {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<QuestionOptionVO> options;
} 