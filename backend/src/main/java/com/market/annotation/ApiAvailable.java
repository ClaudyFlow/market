package com.market.annotation;

import java.lang.annotation.*;

/**
 * API 可用性检测注解
 * 
 * 用于标记需要检测可用性的后端接口
 * 可以配置超时时间、重试次数、依赖服务等参数
 * 
 * @example
 * {@code
 * @ApiAvailable(
 *     timeout = 3000,
 *     retryCount = 2,
 *     dependencies = {"database", "redis"}
 * )
 * @GetMapping("/products")
 * public Result<List<Product>> getProducts() { ... }
 * }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiAvailable {

    /**
     * 接口超时时间（毫秒）
     * 默认 3000ms
     */
    long timeout() default 3000;

    /**
     * 重试次数
     * 默认不重试
     */
    int retryCount() default 0;

    /**
     * 重试间隔（毫秒）
     */
    long retryInterval() default 1000;

    /**
     * 依赖的服务列表
     * 支持：database, redis, cache, external_api 等
     */
    String[] dependencies() default {};

    /**
     * 是否启用检测
     * 默认启用
     */
    boolean enabled() default true;

    /**
     * 检测失败时的行为
     * THROW: 抛出异常
     * RETURN_ERROR: 返回错误结果
     * CONTINUE: 继续执行（仅记录日志）
     */
    FailureAction onFailure() default FailureAction.THROW;

    /**
     * 自定义错误消息
     */
    String errorMessage() default "服务暂时不可用，请稍后重试";

    /**
     * 检测器类（可选，用于自定义检测逻辑）
     */
    Class<?> detector() default Void.class;

    /**
     * 失败行为枚举
     */
    enum FailureAction {
        /**
         * 抛出异常
         */
        THROW,
        /**
         * 返回错误结果
         */
        RETURN_ERROR,
        /**
         * 继续执行（仅记录日志）
         */
        CONTINUE
    }
}
