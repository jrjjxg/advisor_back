package com.advisor.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

/**
 * 更新用户状态数据传输对象
 */
@Data
public class UpdateUserStatusDTO {
    @NotNull(message = "用户状态不能为空")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    private Integer status; // 0-禁用, 1-正常
} 