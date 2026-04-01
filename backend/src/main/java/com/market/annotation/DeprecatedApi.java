package com.market.annotation;

import java.lang.annotation.*;

/**
 * API 弃用注解
 *
 * 用于标记即将弃用或已弃用的 API 接口
 * 支持配置弃用时间范围和替代方案
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
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeprecatedApi {

    /**
     * 弃用开始日期（since）
     * 格式：yyyy-MM-dd
     * 在此日期之前，API 显示为"尚未弃用"
     */
    String since() default "";

    /**
     * 弃用结束日期（until）
     * 格式：yyyy-MM-dd
     * 在此日期之后，API 显示为"已弃用"
     * between since 和 until 之间显示为"即将弃用"
     */
    String until() default "";

    /**
     * 替代方案索引
     * 格式：包名。类名#方法名
     * 例如：com.market.controller.ProductController#getProductsV2
     */
    String[] replacement() default {};

    /**
     * 弃用原因说明
     */
    String reason() default "";

    /**
     * 是否抛出异常
     * true: 已弃用后调用抛出 DeprecatedApiException
     * false: 仅记录警告日志
     */
    boolean throwException() default false;

    /**
     * 自定义警告消息
     */
    String message() default "此 API 已弃用，请使用替代方案";
}
