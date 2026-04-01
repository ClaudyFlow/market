package com.market.controller;

import com.market.annotation.AuditLog;
import com.market.annotation.Cacheable;
import com.market.annotation.Retryable;
import com.market.common.Result;
import com.market.entity.Product;
import com.market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 自定义注解使用示例控制器
 * 演示 @Cacheable、@Retryable、@AuditLog 的用法
 */
@RestController
@RequestMapping("/api/annotation-demo")
public class CustomAnnotationDemoController {

    @Autowired
    private ProductService productService;

    private final Random random = new Random();

    /**
     * 示例 1：@Cacheable - 缓存查询结果
     *
     * 第一次请求会执行方法并缓存结果
     * 后续请求在缓存有效期内直接返回缓存数据
     */
    @GetMapping("/cache/product/{id}")
    @Cacheable(
        key = "#id",
        cacheName = "products",
        expire = 300,
        description = "缓存商品详情"
    )
    public Result<Product> getCachedProduct(@PathVariable Long id) {
        System.out.println(">>> 执行数据库查询：getProduct(" + id + ")");
        Pageable pageable = PageRequest.of(0, 1);
        Page<Product> page = productService.getProducts(pageable);
        Product product = page.getContent().isEmpty() ? null : page.getContent().get(0);

        if (product != null) {
            product.setId(id);
        }
        return Result.success(product);
    }

    /**
     * 示例 2：@Cacheable - 使用 SpEL 表达式作为 key
     */
    @GetMapping("/cache/products")
    @Cacheable(
        key = "'all_' + #page + '_' + #size",
        cacheName = "productLists",
        expire = 600,
        description = "缓存商品列表"
    )
    public Result<List<Product>> getCachedProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        System.out.println(">>> 执行数据库查询：getProducts(page=" + page + ", size=" + size + ")");
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> result = productService.getProducts(pageable);
        return Result.success(result.getContent());
    }

    /**
     * 示例 3：@Cacheable - 条件缓存
     * 只有当价格大于 0 时才缓存
     */
    @GetMapping("/cache/product/conditional/{id}")
    @Cacheable(
        key = "#id",
        cacheName = "expensiveProducts",
        expire = 300,
        condition = "#result.price > 0",
        description = "只缓存价格大于 0 的商品"
    )
    public Result<Product> getCachedExpensiveProduct(@PathVariable Long id) {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Product> page = productService.getProducts(pageable);
        Product product = page.getContent().isEmpty() ? null : page.getContent().get(0);

        if (product != null) {
            product.setId(id);
        }
        return Result.success(product);
    }

    /**
     * 示例 4：@Retryable - 自动重试
     *
     * 模拟可能失败的操作，自动重试 3 次
     * 使用指数退避：第一次 1s，第二次 2s，第三次 4s
     */
    @GetMapping("/retry/external-api")
    @Retryable(
        maxAttempts = 3,
        delay = 1000,
        multiplier = 2.0,
        include = {RuntimeException.class},
        logEnabled = true
    )
    public Result<String> callExternalApiWithRetry(
        @RequestParam(defaultValue = "false") boolean shouldFail) {

        System.out.println(">>> 调用外部 API...");

        if (shouldFail) {
            throw new RuntimeException("外部 API 调用失败（模拟）");
        }

        return Result.success("外部 API 调用成功");
    }

    /**
     * 示例 5：@Retryable - 返回默认值
     *
     * 重试失败后返回默认值，不抛出异常
     */
    @GetMapping("/retry/default-value")
    @Retryable(
        maxAttempts = 3,
        delay = 500,
        failureHandler = Retryable.FailureHandler.RETURN_NULL
    )
    public Result<String> callWithDefaultValue() {
        System.out.println(">>> 调用可能失败的服务...");
        throw new RuntimeException("服务暂时不可用");
    }

    /**
     * 示例 6：@Retryable - 模拟随机失败
     *
     * 30% 概率失败，演示重试机制
     */
    @GetMapping("/retry/random-failure")
    @Retryable(
        maxAttempts = 5,
        delay = 200,
        multiplier = 1.5,
        maxDelay = 2000
    )
    public Result<String> callWithRandomFailure() {
        System.out.println(">>> 模拟随机失败的操作...");

        if (random.nextDouble() < 0.3) {
            throw new RuntimeException("随机失败");
        }

        return Result.success("操作成功");
    }

    /**
     * 示例 7：@AuditLog - 记录创建操作
     */
    @PostMapping("/audit/create")
    @AuditLog(
        module = "商品管理",
        action = "创建商品",
        description = "创建商品：#product.name",
        recordParams = true,
        recordResult = true,
        businessType = "PRODUCT"
    )
    public Result<Product> createProduct(@RequestBody Product product) {
        System.out.println(">>> 创建商品：" + product);
        product.setId(1L);
        return Result.success(product);
    }

    /**
     * 示例 8：@AuditLog - 记录更新操作
     */
    @PutMapping("/audit/update/{id}")
    @AuditLog(
        module = "商品管理",
        action = "更新商品",
        description = "更新商品 ID: #id",
        recordParams = true,
        businessId = "#id",
        businessType = "PRODUCT"
    )
    public Result<Product> updateProduct(
        @PathVariable Long id,
        @RequestBody Product product) {

        System.out.println(">>> 更新商品：" + id);
        product.setId(id);
        return Result.success(product);
    }

    /**
     * 示例 9：@AuditLog - 记录删除操作（高日志级别）
     */
    @DeleteMapping("/audit/delete/{id}")
    @AuditLog(
        module = "商品管理",
        action = "删除商品",
        description = "删除商品 ID: #id",
        logLevel = AuditLog.LogLevel.WARNING,
        businessId = "#id",
        businessType = "PRODUCT"
    )
    public Result<Void> deleteProduct(@PathVariable Long id) {
        System.out.println(">>> 删除商品：" + id);
        return Result.success();
    }

    /**
     * 示例 10：组合使用多个注解
     *
     * @Cacheable + @AuditLog + @Retryable
     * 缓存结果 + 记录审计日志 + 失败重试
     */
    @GetMapping("/combined/product/{id}")
    @Cacheable(
        key = "#id",
        cacheName = "combinedProducts",
        expire = 300
    )
    @Retryable(
        maxAttempts = 3,
        delay = 500
    )
    @AuditLog(
        module = "商品查询",
        action = "查询商品详情",
        description = "查询商品 ID: #id",
        recordResponseTime = true
    )
    public Result<Product> getCombinedProduct(@PathVariable Long id) {
        System.out.println(">>> 组合注解查询：" + id);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Product> page = productService.getProducts(pageable);
        Product product = page.getContent().isEmpty() ? null : page.getContent().get(0);

        if (product != null) {
            product.setId(id);
        }
        return Result.success(product);
    }

    /**
     * 示例 11：@AuditLog - 记录用户操作
     */
    @PostMapping("/audit/user-action")
    @AuditLog(
        module = "用户操作",
        action = "自定义操作",
        description = "用户 #operatorId 执行操作：#actionType",
        operatorId = "#userId",
        recordParams = true,
        businessType = "USER_ACTION"
    )
    public Result<Map<String, Object>> recordUserAction(
        @RequestParam String userId,
        @RequestParam String actionType,
        @RequestBody(required = false) Map<String, Object> data) {

        System.out.println(">>> 记录用户操作：" + userId + " - " + actionType);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("actionType", actionType);
        result.put("timestamp", System.currentTimeMillis());

        return Result.success(result);
    }

    /**
     * 示例 12：@Cacheable - 不缓存 null 值
     */
    @GetMapping("/cache/ignore-null/{id}")
    @Cacheable(
        key = "#id",
        cacheName = "nullTest",
        expire = 60,
        ignoreNull = true
    )
    public Result<Product> getWithIgnoreNull(@PathVariable Long id) {
        System.out.println(">>> 查询（可能返回 null）: " + id);
        // 模拟返回 null
        return Result.success(null);
    }
}
