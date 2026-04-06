package com.market.controller;

import com.market.annotation.AuditLog;
import com.market.annotation.Cacheable;
import com.market.annotation.Retryable;
import com.market.common.Result;
import com.market.entity.Product;
import com.market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
 * 演示 @Cacheable、@Retryable、@AuditLog 等自定义注解的用法。
 * 权限要求：公开接口，无需登录（仅开发环境可用）
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/annotation-demo
 */
@RestController
@RequestMapping("/api/annotation-demo")
@Profile("dev")
@CrossOrigin(origins = "*")
public class CustomAnnotationDemoController {

    @Autowired
    private ProductService productService;

    private final Random random = new Random();

    /**
     * 缓存查询商品详情示例
     * API路径：GET /api/annotation-demo/cache/product/{id}
     * 权限：公开
     *
     * @param id 商品ID
     * @return 商品信息（带缓存）
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
     * 缓存查询商品列表示例（使用 SpEL 表达式作为 key）
     * API路径：GET /api/annotation-demo/cache/products
     * 权限：公开
     *
     * @param page 页码，默认0
     * @param size 每页大小，默认10
     * @return 商品列表（带缓存）
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
     * 条件缓存示例（只缓存价格大于0的商品）
     * API路径：GET /api/annotation-demo/cache/product/conditional/{id}
     * 权限：公开
     *
     * @param id 商品ID
     * @return 商品信息（条件缓存）
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
     * 重试机制示例（自动重试3次，指数退避）
     * API路径：GET /api/annotation-demo/retry/external-api
     * 权限：公开
     *
     * @param shouldFail 是否模拟失败，默认false
     * @return 外部API调用结果
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
     * 重试失败返回默认值示例
     * API路径：GET /api/annotation-demo/retry/default-value
     * 权限：公开
     *
     * @return 服务调用结果，失败返回null
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
     * 随机失败重试示例（30%概率失败）
     * API路径：GET /api/annotation-demo/retry/random-failure
     * 权限：公开
     *
     * @return 操作结果
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
     * 审计日志-创建操作示例
     * API路径：POST /api/annotation-demo/audit/create
     * 权限：公开
     *
     * @param product 商品信息
     * @return 创建的商品信息
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
     * 审计日志-更新操作示例
     * API路径：PUT /api/annotation-demo/audit/update/{id}
     * 权限：公开
     *
     * @param id 商品ID
     * @param product 更新的商品信息
     * @return 更新后的商品信息
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
     * 审计日志-删除操作示例（高日志级别）
     * API路径：DELETE /api/annotation-demo/audit/delete/{id}
     * 权限：公开
     *
     * @param id 商品ID
     * @return 操作结果
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
     * 组合注解示例（@Cacheable + @AuditLog + @Retryable）
     * API路径：GET /api/annotation-demo/combined/product/{id}
     * 权限：公开
     *
     * @param id 商品ID
     * @return 商品信息（带缓存、审计和重试）
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
     * 审计日志-用户自定义操作示例
     * API路径：POST /api/annotation-demo/audit/user-action
     * 权限：公开
     *
     * @param userId 用户ID
     * @param actionType 操作类型
     * @param data 附加数据（可选）
     * @return 操作记录结果
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
     * 不缓存 null 值示例
     * API路径：GET /api/annotation-demo/cache/ignore-null/{id}
     * 权限：公开
     *
     * @param id 商品ID
     * @return 商品信息（不缓存null）
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
        return Result.success(null);
    }
}
