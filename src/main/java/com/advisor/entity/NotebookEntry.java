package com.advisor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 笔记本条目实体类
 */
@Data
@Accessors(chain = true)
@TableName("notebook_entries")
public class NotebookEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * AI回复内容
     */
    @TableField("ai_message_content")
    private String aiMessageContent;

    /**
     * 内容分类
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 用户笔记
     */
    @TableField("user_notes")
    private String userNotes;

    /**
     * 是否收藏
     */
    @TableField("is_favorite")
    private Boolean isFavorite;

    /**
     * 保存时间
     */
    @TableField("saved_at")
    private LocalDateTime savedAt;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
} 