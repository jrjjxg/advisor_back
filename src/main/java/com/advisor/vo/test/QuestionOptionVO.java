package com.advisor.vo.test;

import lombok.Data;

@Data
public class QuestionOptionVO {
    private String id;
    private String questionId;
    private String content;
    private Integer score;
    private Integer orderNum;
}