package com.market.annotation;

import java.lang.annotation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 审计日志注解
 *
 * 用于标记需要记录审计日志的操作
 * 记录操作人、操作时间、操作内容、IP 地址等信息
 *
 * @example
 * {@code
 * @AuditLog(
 *     module = "用户管理",
 *     action = "创建用户",
 *     recordParams = true,
 *     recordResult = false
 * )
 * public Result createUser(@RequestBody User user) { ... }
 * }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    /**
     * 操作模块
     * 例如：用户管理、订单管理、商品管理
     */
    String module() default "";

    /**
     * 操作类型/动作
     * 例如：创建、修改、删除、查询
     */
    String action() default "";

    /**
     * 操作描述（支持 SpEL 表达式）
     * 例如："创建用户：#user.username"
     */
    String description() default "";

    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;

    /**
     * 是否记录返回结果
     */
    boolean recordResult() default false;

    /**
     * 是否记录响应时间
     */
    boolean recordResponseTime() default true;

    /**
     * 操作人 ID（SpEL 表达式）
     * 默认从安全上下文获取
     */
    String operatorId() default "#authentication?.name";

    /**
     * 操作人名称（SpEL 表达式）
     */
    String operatorName() default "";

    /**
     * 业务 ID（SpEL 表达式）
     * 用于关联业务数据
     */
    String businessId() default "";

    /**
     * 业务类型
     */
    String businessType() default "";

    /**
     * 日志级别
     */
    LogLevel logLevel() default LogLevel.INFO;

    /**
     * 是否异步记录
     * true: 异步记录，不影响主流程
     * false: 同步记录
     */
    boolean async() default true;

    /**
     * 忽略的异常类型
     * 这些异常不会记录到审计日志
     */
    Class<? extends Throwable>[] ignoreExceptions() default {};

    /**
     * 操作状态
     * 用于标记操作的重要性
     */
    OperationStatus status() default OperationStatus.SUCCESS;

    /**
     * 日志级别枚举
     */
    enum LogLevel {
        /**
         * 调试
         */
        DEBUG,
        /**
         * 信息
         */
        INFO,
        /**
         * 警告
         */
        WARNING,
        /**
         * 错误
         */
        ERROR
    }

    /**
     * 操作状态枚举
     */
    enum OperationStatus {
        /**
         * 成功
         */
        SUCCESS,
        /**
         * 失败
         */
        FAILURE,
        /**
         * 未知
         */
        UNKNOWN
    }
}
