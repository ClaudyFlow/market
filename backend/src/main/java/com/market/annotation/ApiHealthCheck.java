package com.market.annotation;

import java.lang.annotation.*;

/**
 * API 健康检查配置注解
 * 
 * 用于配置健康检查的详细参数，支持对 API 接口进行周期性健康检查
 * 通常与 @ApiAvailable 配合使用，为接口添加健康检查能力
 * 
 * 使用场景：
 * - 关键业务接口需要实时监控可用性
 * - 需要配置告警阈值的 API 服务
 * - 依赖外部服务的接口健康检测
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
 *
 * @author market-team
 * @since 1.0
 * @Target({ElementType.METHOD, ElementType.TYPE}) - 可应用于方法和类
 * @Retention(RetentionPolicy.RUNTIME) - 运行时保留，支持反射读取
 * @Repeatable(ApiHealthCheck.Container.class) - 支持重复注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(ApiHealthCheck.Container.class)
public @interface ApiHealthCheck {

    /**
     * 是否为关键检查
     * 
     * 关键检查失败会导致接口标记为不可用
     * true: 检查失败时接口不可用
     * false: 检查失败不影响接口可用性，仅记录日志
     */
    boolean critical() default true;

    /**
     * 检查间隔（秒）
     * 
     * 用于配置周期性健康检查的执行频率
     * 建议根据业务重要性设置，关键接口建议 30-60 秒
     */
    long checkInterval() default 60;

    /**
     * 是否启用告警
     * 
     * 启用后，当检查失败次数达到告警阈值时会触发告警通知
     */
    boolean alertEnabled() default false;

    /**
     * 告警阈值（连续失败次数）
     * 
     * 连续失败次数达到此值时触发告警
     * 仅在 alertEnabled = true 时生效
     */
    int alertThreshold() default 3;

    /**
     * 自定义检查器 Bean 名称
     * 
     * 指定用于执行健康检查的 Spring Bean 名称
     * 留空时使用默认检查器，可指定实现了 ApiAvailabilityDetector 接口的 Bean
     */
    String checkerBean() default "";

    /**
     * 容器注解，支持多个健康检查配置
     * 
     * 用于在同一个方法或类上配置多个不同的健康检查策略
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Container {
        ApiHealthCheck[] value();
    }
}
