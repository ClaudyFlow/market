package com.market.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 重试注解
 *
 * 用于标记需要自动重试的方法
 * 当方法执行失败时，根据配置自动重试
 *
 * @example
 * {@code
 * @Retryable(
 *     maxAttempts = 3,
 *     delay = 1000,
 *     multiplier = 2.0,
 *     include = {ResourceAccessException.class}
 * )
 * public Result callExternalApi() { ... }
 * }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retryable {

    /**
     * 最大重试次数（包括首次执行）
     */
    int maxAttempts() default 3;

    /**
     * 重试间隔（毫秒）
     */
    long delay() default 1000;

    /**
     * 延迟乘数（用于指数退避）
     * 例如：delay=1000, multiplier=2.0
     * 第一次重试：1000ms, 第二次：2000ms, 第三次：4000ms
     */
    double multiplier() default 1.0;

    /**
     * 最大延迟时间（毫秒）
     * 与 multiplier 配合使用，限制最大延迟
     */
    long maxDelay() default 10000;

    /**
     * 时间单位（用于 delay 和 maxDelay）
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 需要重试的异常类型
     * 默认重试所有 Exception
     */
    Class<? extends Throwable>[] include() default {Exception.class};

    /**
     * 不需要重试的异常类型
     * 优先级高于 include
     */
    Class<? extends Throwable>[] exclude() default {};

    /**
     * 重试前的监听器（可选）
     */
    Class<?> onRetryListener() default Void.class;

    /**
     * 自定义重试条件（SpEL 表达式）
     * 返回 true 时进行重试
     */
    String condition() default "";

    /**
     * 是否记录重试日志
     */
    boolean logEnabled() default true;

    /**
     * 重试失败后的处理
     * THROW: 抛出最后一次异常
     * RETURN_NULL: 返回 null
     * RETURN_DEFAULT: 返回默认值（需要指定 defaultMethod）
     */
    FailureHandler failureHandler() default FailureHandler.THROW;

    /**
     * 默认值方法（当 failureHandler 为 RETURN_DEFAULT 时调用）
     * 格式：方法名
     */
    String defaultMethod() default "";

    /**
     * 失败处理枚举
     */
    enum FailureHandler {
        /**
         * 抛出异常
         */
        THROW,
        /**
         * 返回 null
         */
        RETURN_NULL,
        /**
         * 返回默认值
         */
        RETURN_DEFAULT
    }
}
