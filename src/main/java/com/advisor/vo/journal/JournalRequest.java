package com.advisor.vo.journal;

import lombok.Data;

/**
 * 日记请求VO
 */
@Data
public class JournalRequest {
    /**
     * 日记ID，新增时为空，修改时必填
     */
    private String id;
    
    /**
     * 日记标题
     */
    private String title;
    
    /**
     * 日记内容
     */
    private String content;
    
    /**
     * 是否私密日记 0-公开 1-私密
     */
    private Integer isPrivate;
    
    /**
     * 关联心情ID
     */
    private String relatedMoodId;
    
    /**
     * 日记背景主题
     */
    private String theme;
    
    /**
     * 图片URL列表，JSON字符串格式
     */
    private String imageUrls;
    
    /**
     * 图片数量
     */
    private Integer imageCount;
    
    
    /**
     * 字数统计
     */
    private Integer wordCount;
} 