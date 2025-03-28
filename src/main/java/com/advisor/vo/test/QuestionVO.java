package com.advisor.vo.test;


import com.advisor.vo.test.QuestionOptionVO;
import lombok.Data;

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