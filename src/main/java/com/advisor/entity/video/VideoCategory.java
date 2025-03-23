package com.advisor.entity.video;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// 视频分类实体
@Data
@TableName("video_category")
public class VideoCategory {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private Integer sort;
    private String icon;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}