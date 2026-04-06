package com.market.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 * 
 * 限制单位时间内的请求次数，防止接口被过度调用
 * 基于滑动窗口算法实现，支持按 IP、用户等维度进行限流
 * 
 * 使用场景：
 * - 防止恶意刷接口
 * - 保护高并发场景下的系统稳定性
 * - API 调用频率控制（如短信验证码、查询接口）
 * 
 * @example
 * {@code
 * @RateLimiter(
 *     maxRequests = 100,
 *     timeout = 60,
 *     timeUnit = TimeUnit.SECONDS,
 *     key = "#userId"
 * )
 * @PostMapping("/order")
 * public Result createOrder(Long userId) { ... }
 * }
 *
 * @author market-team
 * @since 1.0
 * @Target({ElementType.METHOD, ElementType.TYPE}) - 可应用于方法和类
 * @Retention(RetentionPolicy.RUNTIME) - 运行时保留，支持 AOP 拦截
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 最大请求次数
     * 
     * 在指定的时间窗口内允许的最大请求数
     * 超过此限制将被拦截并返回错误消息
     */
    int maxRequests() default 10;

    /**
     * 时间窗口大小
     * 
     * 限流统计的时间周期，与 timeUnit 配合使用
     * 默认 60 秒，即 1 分钟内最多允许 maxRequests 次请求
     */
    long timeout() default 60;

    /**
     * 时间单位
     * 
     * 指定 timeout 字段的时间单位
     * 默认 TimeUnit.SECONDS（秒），可设置为 MILLISECONDS、MINUTES 等
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流 key（支持 SpEL 表达式）
     * 
     * 用于区分不同维度的限流策略
     * 支持：#remoteAddr（客户端 IP）、#userId（用户 ID）、#param0（第一个参数）等
     * 默认使用客户端 IP 作为限流维度
     */
    String key() default "#remoteAddr";

    /**
     * 超过限制时的消息
     * 
     * 当请求频率超过限制时返回给客户端的提示信息
     * 默认提示"请求过于频繁，请稍后再试"
     */
    String message() default "请求过于频繁，请稍后再试";
}
