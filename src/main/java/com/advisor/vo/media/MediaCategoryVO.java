package com.advisor.vo.media;

import lombok.Data;

/**
 * 媒体分类视图对象
 */
@Data
public class MediaCategoryVO {
    private String id;
    private String name;
    private Integer mediaType;
    private String mediaTypeName;
    private String icon;
    private Integer sort;
    
    /**
     * 获取媒体类型名称
     * @return 媒体类型名称
     */
    public String getMediaTypeName() {
        if (mediaType == null) {
            return "";
        }
        return mediaType == 1 ? "视频" : mediaType == 2 ? "音频" : "未知";
    }
} 