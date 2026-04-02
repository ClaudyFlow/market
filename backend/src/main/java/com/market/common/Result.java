package com.market.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应结果类
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 无参构造函数
     */
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

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
     * 时间戳
     */
    private Long timestamp;

    /**
     * 追踪 ID
     */
    private String traceId;

    /**
     * 成功响应
     */
    public Result(T data) {
        this.code = 200;
        this.message = "success";
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应（带消息）
     */
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<T>();
        result.code = 500;
        result.message = message;
        result.timestamp = System.currentTimeMillis();
        return result;
    }

    /**
     * 失败响应（带状态码）
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<T>();
        result.code = code;
        result.message = message;
        result.timestamp = System.currentTimeMillis();
        return result;
    }

    /**
     * 失败响应（带状态码、消息和数据）
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        Result<T> result = new Result<T>();
        result.code = code;
        result.message = message;
        result.data = data;
        result.timestamp = System.currentTimeMillis();
        return result;
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }

    /**
     * 设置追踪 ID
     */
    public Result<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}
