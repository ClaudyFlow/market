package com.market.controller;

import com.market.annotation.DeprecatedApi;
import com.market.common.Result;
import com.market.entity.Product;
import com.market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API 弃用注解使用示例
 */
@RestController
@RequestMapping("/api/example")
public class DeprecatedApiExampleController {

    @Autowired
    private ProductService productService;

    /**
     * 示例 1：尚未弃用的 API（当前日期在 since 之前）
     * 日志：[API 弃用] 方法 xxx 尚未弃用 (since: 2026-05-01)
     */
    @GetMapping("/products/not-deprecated-yet")
    @DeprecatedApi(
        since = "2026-05-01",
        until = "2026-06-01",
        replacement = "com.market.controller.ProductController#getProductsV2",
        reason = "使用新的分页和排序参数"
    )
    public Result<List<Product>> getProductsNotDeprecatedYet() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = productService.getProducts(pageable);
        return Result.success(page.getContent());
    }

    /**
     * 示例 2：即将弃用的 API（当前日期在 since 和 until 之间）
     * 日志：[API 弃用] 方法 xxx 即将弃用，剩余 X 天 (since: 2026-01-01) (until: 2026-06-01)
     */
    @GetMapping("/products/pending-deprecated")
    @DeprecatedApi(
        since = "2026-01-01",
        until = "2026-06-01",
        replacement = {"com.market.controller.ProductController#getProductsV2"},
        reason = "接口性能优化，新版本支持分页查询"
    )
    public Result<List<Product>> getProductsPendingDeprecated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = productService.getProducts(pageable);
        return Result.success(page.getContent());
    }

    /**
     * 示例 3：已弃用的 API（当前日期在 until 之后），仅记录日志
     * 日志：[API 弃用] 方法 xxx 已弃用 (until: 2026-03-01)，替代方案：xxx
     */
    @GetMapping("/products/deprecated-logging")
    @DeprecatedApi(
        since = "2025-12-01",
        until = "2026-03-01",
        replacement = {"com.market.controller.ProductController#getProductsV2"},
        reason = "接口已下线，请迁移至新版本"
    )
    public Result<List<Product>> getProductsDeprecatedLogging() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = productService.getProducts(pageable);
        return Result.success(page.getContent());
    }

    /**
     * 示例 4：已弃用的 API，抛出异常
     * 调用时抛出 DeprecatedApiException
     */
    @GetMapping("/products/deprecated-exception")
    @DeprecatedApi(
        since = "2025-12-01",
        until = "2026-03-01",
        replacement = {
            "com.market.controller.ProductController#getProductsV2",
            "com.market.controller.ProductController#searchProducts"
        },
        reason = "接口已下线，请迁移至新版本",
        throwException = true,
        message = "此 API 已弃用，请使用替代方案"
    )
    public Result<List<Product>> getProductsDeprecatedException() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = productService.getProducts(pageable);
        return Result.success(page.getContent());
    }

    /**
     * 示例 5：多个替代方案
     */
    @GetMapping("/products/multiple-replacements")
    @DeprecatedApi(
        since = "2026-01-01",
        until = "2026-05-01",
        replacement = {
            "com.market.controller.ProductController#getProductsV2",
            "com.market.controller.ProductController#searchProducts",
            "com.market.controller.SearchController#search"
        },
        reason = "接口拆分，根据需求选择对应的新接口"
    )
    public Result<List<Product>> getProductsMultipleReplacements() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = productService.getProducts(pageable);
        return Result.success(page.getContent());
    }

    /**
     * 示例 6：类级别弃用（该类所有方法都继承弃用配置）
     */
    @DeprecatedApi(
        since = "2026-01-01",
        until = "2026-12-31",
        replacement = "com.market.controller.ProductControllerV2",
        reason = "整个控制器已弃用，请迁移至 V2 版本"
    )
    @RestController
    @RequestMapping("/api/example/legacy")
    public static class LegacyController {

        @GetMapping("/items")
        public Result<List<Product>> getItems() {
            // 此方法会继承类级别的弃用配置
            return Result.success(null);
        }
    }
}
