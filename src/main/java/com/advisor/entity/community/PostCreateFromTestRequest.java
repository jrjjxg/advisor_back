package com.advisor.dto.community;

import lombok.Data;

@Data
public class PostCreateFromTestRequest {
    private String testResultId;
    private String content;
    private Integer isAnonymous = 0;
}