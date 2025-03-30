package com.advisor.common;

/**
 * 统一响应状态码
 */
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    DATA_NOT_FOUND(404, "数据不存在"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    
    BUSINESS_ERROR(600, "业务异常"),
    DATA_CONFLICT(601, "数据冲突"),
    TOO_MANY_REQUESTS(602, "请求过于频繁"),
    
    SYSTEM_ERROR(500, "系统错误");
    
    private final int code;
    private final String message;
    
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
} 