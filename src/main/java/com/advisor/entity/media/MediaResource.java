package com.advisor.entity.media;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 媒体资源实体
 */
@Data
@TableName("media_resource")
public class MediaResource {
    /**
     * 资源ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 资源标题
     */
    private String title;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * 封面图URL
     */
    private String coverUrl;
    
    /**
     * 资源URL
     */
    private String resourceUrl;
    
    /**
     * 媒体类型：1=视频，2=音频
     */
    private Integer mediaType;
    
    /**
     * 时长（秒）
     */
    private Integer duration;
    
    /**
     * 所属分类ID
     */
    private String categoryId;
    
    /**
     * 播放次数
     */
    private Integer views;
    
    /**
     * 点赞数
     */
    private Integer likes;
    
    /**
     * 状态：0=未发布，1=已发布
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 