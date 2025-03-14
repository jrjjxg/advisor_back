package com.advisor.vo;

import com.advisor.entity.QuestionOption;
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
    private List<QuestionOptionVO> options;
}