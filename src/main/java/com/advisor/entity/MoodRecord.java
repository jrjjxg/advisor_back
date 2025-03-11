package com.advisor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mood_record")
public class MoodRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String userId;
    
    private String emotionType;
    
    private Integer intensity;
    
    private String note;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}