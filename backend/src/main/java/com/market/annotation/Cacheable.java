package com.market.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存注解
 * 
 * 用于标记需要缓存结果的方法，自动将方法返回值存储到缓存中
 * 支持 Redis 缓存（如果可用）或本地内存缓存
 * 
 * 使用场景：
 * - 查询频繁且数据变化不频繁的方法
 * - 计算成本高、耗时的查询操作
 * - 需要提升接口响应速度的读操作
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
 *
 * @author market-team
 * @since 1.0
 * @Target({ElementType.METHOD, ElementType.TYPE}) - 可应用于方法和类
 * @Retention(RetentionPolicy.RUNTIME) - 运行时保留，支持 AOP 拦截
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {

    /**
     * 缓存名称
     * 
     * 用于分组管理不同的缓存，便于统一清理和监控
     * 建议按业务模块命名，如: products, users, orders
     */
    String cacheName() default "default";

    /**
     * 缓存 key（支持 SpEL 表达式）
     * 
     * 用于生成唯一的缓存标识
     * 支持：#id, #userId, #param0, #result 等 SpEL 表达式
     * 留空时使用默认策略生成 key
     */
    String key() default "";

    /**
     * 缓存过期时间
     * 
     * 缓存数据在过期后将被自动清除
     * 配合 timeUnit 使用，默认 300 秒（5分钟）
     */
    long expire() default 300;

    /**
     * 时间单位
     * 
     * 指定 expire 字段的时间单位
     * 默认 TimeUnit.SECONDS（秒）
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 是否忽略空值
     * 
     * true: 不缓存 null 值，避免缓存穿透
     * false: 缓存 null 值，适用于明确不存在的查询结果
     */
    boolean ignoreNull() default true;

    /**
     * 缓存条件（SpEL 表达式）
     * 
     * 只有条件表达式结果为 true 时才会缓存
     * 可用于动态控制是否缓存，如: "#result != null && #result.size > 0"
     */
    String condition() default "";

    /**
     * 是否强制刷新缓存
     * 
     * true: 忽略已有缓存，重新执行方法并更新缓存
     * false: 优先使用已有缓存
     * 与 unless 配合使用可实现灵活的缓存策略
     */
    boolean forceRefresh() default false;

    /**
     * 不缓存的条件（SpEL 表达式）
     * 
     * 当表达式结果为 true 时，不缓存方法返回值
     * 例如: "#result.status == 500" 表示状态码为 500 时不缓存
     */
    String unless() default "";

    /**
     * 缓存描述
     * 
     * 用于文档说明和监控日志，描述该缓存的用途
     */
    String description() default "";
}
