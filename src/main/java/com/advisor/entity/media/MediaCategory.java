package com.advisor.entity.media;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 媒体分类实体
 */
@Data
@TableName("media_category")
public class MediaCategory {
    /**
     * 分类ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 媒体类型：1=视频，2=音频
     */
    private Integer mediaType;
    
    /**
     * 分类图标URL
     */
    private String icon;
    
    /**
     * 排序字段
     */
    private Integer sort;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 