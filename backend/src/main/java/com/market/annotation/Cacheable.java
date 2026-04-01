package com.market.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存注解
 *
 * 用于标记需要缓存结果的方法
 * 支持 Redis 缓存（如果可用）或本地内存缓存
 *
 * @example
 * {@code
 * @Cacheable(
 *     key = "#id",
 *     cacheName = "products",
 *     expire = 300,
 *     timeUnit = TimeUnit.SECONDS
 * )
 * public Product getProduct(Long id) { ... }
 * }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {

    /**
     * 缓存名称
     * 用于分组管理不同的缓存
     */
    String cacheName() default "default";

    /**
     * 缓存 key（支持 SpEL 表达式）
     * 支持：#id, #userId, #param0, #result 等
     */
    String key() default "";

    /**
     * 缓存过期时间
     */
    long expire() default 300;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 是否忽略空值
     * true: 不缓存 null 值
     * false: 缓存 null 值
     */
    boolean ignoreNull() default true;

    /**
     * 缓存条件（SpEL 表达式）
     * 只有条件为 true 时才缓存
     */
    String condition() default "";

    /**
     * 是否强制刷新缓存
     * 与 unless 配合使用
     */
    boolean forceRefresh() default false;

    /**
     * 不缓存的条件（SpEL 表达式）
     */
    String unless() default "";

    /**
     * 缓存描述
     */
    String description() default "";
}
