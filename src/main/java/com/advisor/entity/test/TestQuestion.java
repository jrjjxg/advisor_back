package com.advisor.entity.test;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("test_question")
public class TestQuestion {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String testTypeId;
    private String content;
    private Integer orderNum;
    private Integer optionType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}