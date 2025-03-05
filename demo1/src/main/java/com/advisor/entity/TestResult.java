package com.advisor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("test_result")
public class TestResult {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String userId;
    private String testTypeId;
    private Integer totalScore;
    private String resultLevel;
    private String resultDescription;
    private String suggestions;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
}