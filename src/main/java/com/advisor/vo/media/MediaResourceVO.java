package com.advisor.vo.media;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 媒体资源视图对象
 */
@Data
public class MediaResourceVO {
    private String id;
    private String title;
    private String description;
    private String coverUrl;
    private String resourceUrl;
    private Integer mediaType;
    private String mediaTypeName;
    private Integer duration;
    private String durationText;
    private String categoryId;
    private String categoryName;
    private Integer views;
    private Integer likes;
    private Integer status;
    private LocalDateTime createTime;
    
    // 用户相关数据
    private Boolean isFavorite;
    private Integer progress;
    private Boolean isCompleted;
    
    /**
     * 获取媒体类型名称
     */
    public String getMediaTypeName() {
        if (mediaType == null) {
            return "";
        }
        return mediaType == 1 ? "视频" : mediaType == 2 ? "音频" : "未知";
    }
    
    /**
     * 获取格式化的时长文本
     */
    public String getDurationText() {
        if (duration == null || duration <= 0) {
            return "00:00";
        }
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
} 