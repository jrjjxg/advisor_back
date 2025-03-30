package com.advisor.entity.media;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户媒体收藏实体
 */
@Data
@TableName("user_media_favorite")
public class UserMediaFavorite {
    /**
     * 收藏ID
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
     * 创建时间
     */
    private LocalDateTime createTime;
} 