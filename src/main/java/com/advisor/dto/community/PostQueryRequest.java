package com.advisor.dto.community;

import lombok.Data;

import java.util.List;

/**
 * 帖子查询请求
 */
@Data
public class PostQueryRequest {
    
    /**
     * 关键词
     */
    private String keyword;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 排序方式：time-时间，hot-热度
     */
    private String orderBy;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}