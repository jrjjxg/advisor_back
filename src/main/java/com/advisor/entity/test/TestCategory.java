package com.advisor.entity.test;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("test_category")
public class TestCategory {
    @TableId
    private String code;
    private String name;
    private String description;
    private String icon;
    private String color;
    private Integer displayOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
} 