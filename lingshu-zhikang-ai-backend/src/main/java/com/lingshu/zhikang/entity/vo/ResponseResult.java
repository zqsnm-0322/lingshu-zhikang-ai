package com.lingshu.zhikang.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 数据
     */
    private T data;
    
    /**
     * 成功
     */
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "操作成功", data);
    }
    
    /**
     * 成功
     */
    public static <T> ResponseResult<T> success(String msg, T data) {
        return new ResponseResult<>(200, msg, data);
    }
    
    /**
     * 成功（ok方法别名）
     */
    public static <T> ResponseResult<T> ok(T data) {
        return success(data);
    }
    
    /**
     * 成功（ok方法别名）
     */
    public static <T> ResponseResult<T> ok(String msg, T data) {
        return success(msg, data);
    }
    
    /**
     * 失败
     */
    public static <T> ResponseResult<T> error(String msg) {
        return new ResponseResult<>(500, msg, null);
    }
    
    /**
     * 失败
     */
    public static <T> ResponseResult<T> error(Integer code, String msg) {
        return new ResponseResult<>(code, msg, null);
    }
} 