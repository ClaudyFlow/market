package com.market.controller;

import com.market.annotation.ApiAvailable;
import com.market.annotation.ApiHealthCheck;
import com.market.common.Result;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品控制器 - API 可用性检测示例
 * 
 * 演示如何使用 @ApiAvailable 注解来检测接口可用性
 */
@Slf4j
@RestController
@RequestMapping("/api/products-example")
@CrossOrigin(origins = "*")
public class ProductControllerExample {

    @Autowired
    private ProductService productService;

    /**
     * 获取所有产品
     * 
     * 使用 @ApiAvailable 注解检测数据库依赖
     * 如果数据库不可用，将返回错误响应
     */
    @GetMapping
    @ApiAvailable(
        timeout = 5000,
        retryCount = 1,
        retryInterval = 500,
        dependencies = {"database"},
        onFailure = ApiAvailable.FailureAction.RETURN_ERROR,
        errorMessage = "产品服务暂时不可用"
    )
    @ApiHealthCheck(critical = true, alertEnabled = true)
    public Result<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(productService.getProducts(pageable));
    }

    /**
     * 根据 ID 获取产品
     * 
     * 轻量级检测，仅设置超时
     */
    @GetMapping("/{id}")
    @ApiAvailable(timeout = 3000)
    public Result<Product> getProductById(@PathVariable Long id) {
        return Result.success(productService.getProductById(id));
    }

    /**
     * 搜索产品
     * 
     * 使用数据库和 Redis 缓存依赖
     */
    @GetMapping("/search")
    @ApiAvailable(
        timeout = 5000,
        dependencies = {"database", "redis"},
        onFailure = ApiAvailable.FailureAction.CONTINUE
    )
    @ApiHealthCheck(critical = false, checkInterval = 30)
    public Result<Page<Product>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(productService.searchProducts(keyword, pageable));
    }

    /**
     * 创建产品（商家）
     * 
     * 严格检测，失败时抛出异常
     */
    @PostMapping
    @ApiAvailable(
        timeout = 10000,
        retryCount = 2,
        dependencies = {"database"},
        onFailure = ApiAvailable.FailureAction.THROW
    )
    @ApiHealthCheck(critical = true, alertEnabled = true, alertThreshold = 2)
    public Result<Product> createProduct(
            @AuthenticationPrincipal User user,
            @RequestBody Product product) {
        return Result.success(productService.createProduct(user, product));
    }
}
