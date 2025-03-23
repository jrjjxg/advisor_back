package com.advisor.entity.video;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

// 用户观看历史实体
@Data
@TableName("user_view_history")
public class UserViewHistory {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String userId;
    private String videoId;
    private Integer progress;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private com.advisor.entity.video.VideoInfo videoInfo;
}
