package com.advisor.entity.mood;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("mood_record_tag")
public class MoodRecordTag {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long moodRecordId;
    
    private Long tagId;
}