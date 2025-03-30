package com.advisor.dto.media;

import lombok.Data;

/**
 * 媒体资源数据传输对象
 */
@Data
public class MediaResourceDTO {
    private String id;
    private String title;
    private String description;
    private String coverUrl;
    private String resourceUrl;
    private Integer mediaType;
    private Integer duration;
    private String categoryId;
    private Integer status;
} 