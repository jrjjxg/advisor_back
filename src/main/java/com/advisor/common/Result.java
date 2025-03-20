package com.advisor.common;

import lombok.Data;

/**
 * 统一返回结果
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> {
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 返回结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    
    /**
     * 成功，无数据
     *
     * @return 返回结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }
    
    /**
     * 失败
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     数据类型
     * @return 返回结果
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    /**
     * 失败
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 返回结果
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}