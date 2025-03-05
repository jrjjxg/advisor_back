package com.advisor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("test_type")
public class TestType {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String description;
    private String icon;
    private String category;
    private Integer timeMinutes;
    private Integer questionCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
}