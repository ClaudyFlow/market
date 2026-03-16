package com.market.common;

/**
 * 统一响应结果封装类
 */
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    // 无参构造器
    public Result() {
    }

    // 全参构造器
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    // 成功响应（不带数据）
    public static <T> Result<T> success() {
        return success(null);
    }

    // 错误响应（自定义错误码和消息）
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    // 错误响应（默认500错误码）
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    // Getter 和 Setter 方法
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}