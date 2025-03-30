package com.advisor.entity.test;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("template_option")
public class TemplateOption {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String templateId;
    private String content;
    private Integer score;
    private Integer orderNum;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 