package com.market.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性注解
 *
 * 用于标记需要保证幂等性的操作
 * 防止重复提交、重复处理
 *
 * @example
 * {@code
 * @Idempotent(
 *     key = "#orderId",
 *     expire = 3600,
 *     message = "订单正在处理中，请勿重复提交"
 * )
 * public Result<Void> createOrder(@RequestBody Order order) { ... }
 * }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 幂等 key（支持 SpEL 表达式）
     * 用于标识同一个操作
     */
    String key() default "";

    /**
     * 过期时间
     * 超过此时间后允许再次执行
     */
    long expire() default 3600;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 错误消息
     */
    String message() default "操作正在执行中，请勿重复提交";

    /**
     * 是否抛出异常
     * true: 抛出 IdempotentException
     * false: 返回上次的结果
     */
    boolean throwException() default true;

    /**
     * 是否缓存结果
     * true: 缓存首次执行结果，重复请求返回缓存
     * false: 仅防止重复执行
     */
    boolean cacheResult() default false;

    /**
     * 包含的异常类型
     * 这些异常不会触发幂等保护
     */
    Class<? extends Throwable>[] includeExceptions() default {};
}
