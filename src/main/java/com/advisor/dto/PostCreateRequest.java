package com.advisor.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 创建帖子请求
 */
@Data
public class PostCreateRequest {
    
    /**
     * 帖子内容
     */
    @NotBlank(message = "内容不能为空")
    @Size(max = 10000, message = "内容长度不能超过10000字")
    private String content;
    
    /**
     * 图片URL列表
     */
    private List<String> images;
    
    /**
     * 位置信息
     */
    private String location;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 情绪记录ID
     */
    private Long moodRecordId;
    
    /**
     * 测试结果ID
     */
    private String testResultId;
    
    /**
     * 是否匿名：0-否，1-是
     */
    private Integer isAnonymous;
}