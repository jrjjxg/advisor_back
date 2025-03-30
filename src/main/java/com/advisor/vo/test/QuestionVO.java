package com.advisor.vo.test;

import com.advisor.vo.test.QuestionOptionVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionVO {
    private String id;
    private String testTypeId;
    private String content;
    private Integer orderNum;
    private Integer optionType;
    private String optionTemplateId;
    private String imageUrl;
    private List<QuestionOptionVO> options;
    private LocalDateTime createTime;
}