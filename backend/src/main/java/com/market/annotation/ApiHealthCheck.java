package com.market.annotation;

import java.lang.annotation.*;

/**
 * API 健康检查配置注解
 * 
 * 用于配置健康检查的详细参数
 * 通常与 @ApiAvailable 配合使用
 * 
 * @example
 * {@code
 * @ApiAvailable
 * @ApiHealthCheck(
 *     critical = true,
 *     checkInterval = 60,
 *     alertEnabled = true
 * )
 * @GetMapping("/orders")
 * public Result<List<Order>> getOrders() { ... }
 * }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(ApiHealthCheck.Container.class)
public @interface ApiHealthCheck {

    /**
     * 是否为关键检查
     * 关键检查失败会导致接口不可用
     */
    boolean critical() default true;

    /**
     * 检查间隔（秒）
     * 用于周期性健康检查
     */
    long checkInterval() default 60;

    /**
     * 是否启用告警
     */
    boolean alertEnabled() default false;

    /**
     * 告警阈值（连续失败次数）
     */
    int alertThreshold() default 3;

    /**
     * 自定义检查器 Bean 名称
     */
    String checkerBean() default "";

    /**
     * 容器注解，支持多个健康检查配置
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Container {
        ApiHealthCheck[] value();
    }
}
