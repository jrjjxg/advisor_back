package com.advisor.entity.test;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_answer")
public class UserAnswer {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String testResultId;
    private String questionId;
    private String optionId;
    private Integer score;
    private LocalDateTime createTime;
}