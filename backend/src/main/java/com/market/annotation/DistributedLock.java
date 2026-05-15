package com.market.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解
 *
 * 用于标记需要加锁执行的方法
 * 支持本地锁（生产环境建议使用 Redis 分布式锁）
 *
 * @example
 * {@code
 * @DistributedLock(
 *     key = "#userId",
 *     waitTime = 5000,
 *     leaseTime = 30000,
 *     message = "操作正在执行中，请稍后再试"
 * )
 * public Result<Void> updateUserInfo(@PathVariable Long userId, @RequestBody User user) { ... }
 * }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 锁 key（支持 SpEL 表达式）
     */
    String key() default "";

    /**
     * 等待时间（毫秒）
     * 尝试获取锁的最大等待时间
     */
    long waitTime() default 5000;

    /**
     * 锁持有时间（毫秒）
     * 超过此时间锁自动释放
     */
    long leaseTime() default 30000;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 是否公平锁
     * true: 按请求顺序获取锁
     * false: 非公平锁
     */
    boolean fair() default false;

    /**
     * 获取锁失败时的消息
     */
    String message() default "获取锁失败，请稍后再试";

    /**
     * 是否抛出异常
     * true: 获取锁失败时抛出异常
     * false: 返回 null
     */
    boolean throwException() default true;

    /**
     * 重试次数
     * 获取锁失败后的重试次数
     */
    int retryCount() default 0;

    /**
     * 重试间隔（毫秒）
     */
    long retryInterval() default 100;

    /**
     * 锁类型
     */
    LockType lockType() default LockType.REENTRANT;

    /**
     * 锁类型枚举
     */
    enum LockType {
        /**
         * 可重入锁
         */
        REENTRANT,
        /**
         * 读写锁（读）
         */
        READ,
        /**
         * 读写锁（写）
         */
        WRITE
    }
}
