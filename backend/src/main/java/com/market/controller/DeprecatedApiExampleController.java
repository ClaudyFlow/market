package com.market.controller;

import com.market.annotation.DeprecatedApi;
import com.market.common.Result;
import com.market.entity.Product;
import com.market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API 弃用注解使用示例控制器
 * 演示 @DeprecatedApi 注解的各种用法，包括不同弃用阶段的行为。
 * 权限要求：公开接口，无需登录（仅开发环境可用）
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/example
 */
@RestController
@RequestMapping("/api/example")
@Profile("dev")
@CrossOrigin(origins = "*")
public class DeprecatedApiExampleController {

    @Autowired
    private ProductService productService;

    /**
     * 尚未弃用的 API 示例
     * API路径：GET /api/example/products/not-deprecated-yet
     * 权限：公开
     *
     * @return 商品列表
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
     * 即将弃用的 API 示例
     * API路径：GET /api/example/products/pending-deprecated
     * 权限：公开
     *
     * @return 商品列表
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
     * 已弃用仅记录日志的 API 示例
     * API路径：GET /api/example/products/deprecated-logging
     * 权限：公开
     *
     * @return 商品列表
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
     * 已弃用并抛出异常的 API 示例
     * API路径：GET /api/example/products/deprecated-exception
     * 权限：公开
     *
     * @return 商品列表（调用时会抛出 DeprecatedApiException）
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
     * 多个替代方案的弃用 API 示例
     * API路径：GET /api/example/products/multiple-replacements
     * 权限：公开
     *
     * @return 商品列表
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
     * 类级别弃用示例控制器
     * 该类所有方法都继承类级别的弃用配置。
     * 权限要求：公开接口，无需登录
     *
     * @author market-team
     * @since 1.0
     * @RequestMapping /api/example/legacy
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

        /**
         * 获取商品列表（类级别弃用）
         * API路径：GET /api/example/legacy/items
         * 权限：公开
         *
         * @return 商品列表
         */
        @GetMapping("/items")
        public Result<List<Product>> getItems() {
            return Result.success(null);
        }
    }
}
