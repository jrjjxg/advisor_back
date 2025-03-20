package com.advisor.dto.community;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PostCreateRequest {
    
    private String title;
    
    @NotBlank(message = "内容不能为空")
    @Size(max = 2000, message = "内容长度不能超过2000字")
    private String content;
    
    private List<String> tags;
    
    private List<String> images;
    
    private Integer isAnonymous = 0;
}