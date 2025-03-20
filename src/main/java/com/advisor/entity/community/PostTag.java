package com.advisor.entity.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 帖子标签实体类
 */
@Data
@TableName("post_tag")
public class PostTag {
    
    /**
     * 标签ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 帖子ID
     */
    private String postId;
    
    /**
     * 标签名称
     */
    private String tagName;
}