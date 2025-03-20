package com.advisor.dto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 发送私信请求
 */
@Data
public class MessageSendRequest {
    
    /**
     * 接收用户ID
     */
    @NotNull(message = "接收用户ID不能为空")
    private String toUserId;
    
    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 500, message = "消息内容不能超过500字")
    private String content;
}