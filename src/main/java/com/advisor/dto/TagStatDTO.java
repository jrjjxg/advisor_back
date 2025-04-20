package com.advisor.dto;

import lombok.Data;

@Data
public class TagStatDTO {
    private String tagName;    // 标签名称
    private Integer count;     // 出现次数
}