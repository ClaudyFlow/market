package com.market.annotation;

import java.lang.annotation.*;

/**
 * API 弃用注解
 * 
 * 用于标记即将弃用或已弃用的 API 接口
 * 支持配置弃用时间范围和替代方案，帮助开发者平滑过渡到新版本 API
 * 
 * 使用场景：
 * - 标记旧版本 API，提示开发者迁移到新接口
 * - 设置弃用过渡期，在指定时间后自动拒绝调用
 * - 记录弃用原因和替代方案，便于维护
 * 
 * 状态说明：
 * - since 之前：显示为"尚未弃用"
 * - since 和 until 之间：显示为"即将弃用"
 * - until 之后：显示为"已弃用"
 * 
 * @example
 * {@code
 * @DeprecatedApi(
 *     since = "2024-01-01",
 *     until = "2024-06-01",
 *     replacement = "com.market.controller.ProductController#getProductsV2",
 *     reason = "使用新的分页参数"
 * )
 * @GetMapping("/products/old")
 * public Result<List<Product>> getOldProducts() { ... }
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
public @interface DeprecatedApi {

    /**
     * 弃用开始日期（since）
     * 
     * 格式：yyyy-MM-dd
     * 标记 API 开始弃用的时间点
     * 在此日期之前，API 显示为"尚未弃用"
     * 留空表示立即弃用
     */
    String since() default "";

    /**
     * 弃用结束日期（until）
     * 
     * 格式：yyyy-MM-dd
     * 标记 API 完全停止服务的时间点
     * 在此日期之后，API 显示为"已弃用"
     * between since 和 until 之间显示为"即将弃用"
     * 留空表示无限期保留（仅提示不拒绝）
     */
    String until() default "";

    /**
     * 替代方案索引
     * 
     * 指定替代 API 的位置，帮助开发者快速定位新接口
     * 格式：包名。类名#方法名
     * 例如：com.market.controller.ProductController#getProductsV2
     * 支持多个替代方案
     */
    String[] replacement() default {};

    /**
     * 弃用原因说明
     * 
     * 描述为什么弃用此 API，帮助开发者理解迁移必要性
     * 例如："使用新的分页参数"、"性能优化，请使用 V2 接口"
     */
    String reason() default "";

    /**
     * 是否抛出异常
     * 
     * 控制弃用后的行为：
     * true: 已弃用后调用抛出 DeprecatedApiException，强制拒绝访问
     * false: 仅记录警告日志，允许继续调用
     */
    boolean throwException() default false;

    /**
     * 自定义警告消息
     * 
     * 当 API 被调用时显示的提示信息
     * 默认消息会提示 API 已弃用并引导使用替代方案
     */
    String message() default "此 API 已弃用，请使用替代方案";
}
