package com.advisor.entity.video;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// 视频信息实体
@Data
@TableName("video_info")
public class VideoInfo {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String title;
    private String categoryId;
    private String coverUrl;
    private String fileKey;
    private Integer duration;
    private String description;
    private Long viewCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private String categoryName;
    @TableField(exist = false)
    private String playUrl; // 播放地址，动态生成
}