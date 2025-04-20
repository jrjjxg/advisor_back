package com.advisor.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 笔记本条目数据传输对象
 */
@Data
@Accessors(chain = true)
public class NotebookEntryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * AI回复内容
     */
    private String aiMessageContent;

    /**
     * 内容分类
     */
    private String contentType;

    /**
     * 用户笔记
     */
    private String userNotes;

    /**
     * 是否收藏
     */
    private Boolean isFavorite;

    /**
     * 保存时间
     */
    private LocalDateTime savedAt;

    /**
     * 日期时间格式化后的字符串，前端显示用
     */
    private String savedAtStr;
} 