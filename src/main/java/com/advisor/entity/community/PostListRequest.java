package com.advisor.dto.community;

import lombok.Data;

import java.util.List;

@Data
public class PostListRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private List<String> tags;
    private String sortBy = "createTime"; // createTime, likeCount, commentCount
    private String sortOrder = "desc"; // asc, desc
}