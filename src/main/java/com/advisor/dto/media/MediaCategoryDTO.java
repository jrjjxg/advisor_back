package com.advisor.dto.media;

import lombok.Data;

/**
 * 媒体分类数据传输对象
 */
@Data
public class MediaCategoryDTO {
    private String id;
    private String name;
    private Integer mediaType;
    private String icon;
    private Integer sort;
} 