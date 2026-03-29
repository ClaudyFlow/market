package com.market.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 * 限制单位时间内的请求次数
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 最大请求次数
     */
    int maxRequests() default 10;

    /**
     * 时间窗口
     */
    long timeout() default 60;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流 key（支持 SpEL 表达式）
     */
    String key() default "#remoteAddr";

    /**
     * 超过限制时的消息
     */
    String message() default "请求过于频繁，请稍后再试";
}
