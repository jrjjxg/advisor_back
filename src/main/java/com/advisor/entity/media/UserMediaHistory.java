package com.advisor.entity.media;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户媒体历史记录实体
 */
@Data
@TableName("user_media_history")
public class UserMediaHistory {
    /**
     * 记录ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 媒体资源ID
     */
    private String mediaId;
    
    /**
     * 播放进度（秒）
     */
    private Integer progress;
    
    /**
     * 是否播放完成：0=未完成，1=已完成
     */
    private Integer isCompleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 