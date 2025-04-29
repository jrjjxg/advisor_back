package com.advisor.dto;

import lombok.Data;

/**
 * 用户查询数据传输对象
 */
@Data
public class UserQueryDTO {
    private int page = 1; // 默认页码
    private int size = 10; // 默认每页大小
    private String username; // 按用户名筛选
    private String email; // 按邮箱筛选
    private Integer status; // 按状态筛选
    private String sortBy = "createTime"; // 默认排序字段
    private String sortOrder = "desc"; // 默认排序顺序
} 