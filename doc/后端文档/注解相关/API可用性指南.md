# API 可用性检测注解使用指南

## 概述

本项目提供了一套完整的 API 可用性检测注解系统，用于检测和监控后端接口的可用性。通过简单的注解配置，可以实现：

- 接口超时控制
- 依赖服务健康检查
- 自动重试机制
- 失败处理策略
- 健康状态监控

## 核心注解

### 1. @ApiAvailable

主注解，用于标记需要检测可用性的接口。

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiAvailable {
    long timeout() default 3000;           // 超时时间（毫秒）
    int retryCount() default 0;            // 重试次数
    long retryInterval() default 1000;     // 重试间隔
    String[] dependencies() default {};    // 依赖服务列表
    boolean enabled() default true;        // 是否启用
    FailureAction onFailure() default THROW; // 失败处理策略
    String errorMessage() default "...";   // 错误消息
    Class<?> detector() default Void.class;// 自定义检测器
}
```

### 2. @ApiHealthCheck

健康检查配置注解，可配置详细的检查参数。

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiHealthCheck {
    boolean critical() default true;       // 是否为关键检查
    long checkInterval() default 60;       // 检查间隔（秒）
    boolean alertEnabled() default false;  // 是否启用告警
    int alertThreshold() default 3;        // 告警阈值
    String checkerBean() default "";       // 自定义检查器 Bean
}
```

## 失败处理策略

| 策略 | 说明 |
|------|------|
| `THROW` | 抛出 `ApiAvailabilityException` 异常 |
| `RETURN_ERROR` | 返回 `Result.error()` 响应 |
| `CONTINUE` | 继续执行（仅记录日志） |

## 内置检测器

| 检测器 | Bean 名称 | 说明 |
|--------|----------|------|
| `DatabaseAvailabilityDetector` | `databaseAvailabilityDetector` | 检测数据库连接 |
| `RedisAvailabilityDetector` | `redisAvailabilityDetector` | 检测 Redis 连接 |
| `ExternalApiAvailabilityDetector` | `externalApiAvailabilityDetector` | 检测外部 API |

## 使用示例

### 基础用法

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    @ApiAvailable(timeout = 5000)
    public Result<List<Product>> getAllProducts() {
        return Result.success(productService.getAllProducts());
    }
}
```

### 检测数据库依赖

```java
@GetMapping("/users")
@ApiAvailable(
    timeout = 5000,
    dependencies = {"database"},
    onFailure = ApiAvailable.FailureAction.RETURN_ERROR
)
public Result<List<User>> getAllUsers() {
    return Result.success(userService.getAllUsers());
}
```

### 检测多个依赖

```java
@GetMapping("/orders")
@ApiAvailable(
    timeout = 10000,
    retryCount = 2,
    retryInterval = 500,
    dependencies = {"database", "redis"},
    onFailure = ApiAvailable.FailureAction.THROW
)
@ApiHealthCheck(critical = true, alertEnabled = true)
public Result<List<Order>> getOrders() {
    return Result.success(orderService.getOrders());
}
```

### 自定义检测器

```java
// 1. 实现检测器接口
@Component
public class CustomApiDetector implements ApiAvailabilityDetector {
    @Override
    public DetectionResult detect() {
        // 自定义检测逻辑
        if (checkPass()) {
            return DetectionResult.success("服务正常");
        }
        return DetectionResult.failure("服务异常");
    }
}

// 2. 在接口上使用
@PostMapping("/payment")
@ApiAvailable(
    timeout = 15000,
    detector = CustomApiDetector.class,
    onFailure = ApiAvailable.FailureAction.RETURN_ERROR
)
public Result<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
    return Result.success(paymentService.process(request));
}
```

### 类级别注解

```java
@RestController
@RequestMapping("/api/admin")
@ApiAvailable(
    timeout = 5000,
    dependencies = {"database"},
    onFailure = ApiAvailable.FailureAction.RETURN_ERROR
)
public class AdminController {
    
    // 所有方法都会进行可用性检测
    @GetMapping("/users")
    public Result<List<User>> getUsers() { ... }
    
    @GetMapping("/orders")
    public Result<List<Order>> getOrders() { ... }
}
```

## 异常处理

当检测失败且配置为 `FailureAction.THROW` 时，会抛出 `ApiAvailabilityException`：

```java
@ExceptionHandler(ApiAvailabilityException.class)
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public Result<Void> handleApiAvailabilityException(ApiAvailabilityException e) {
    log.error("服务不可用：{}", e.getServiceName(), e);
    return Result.error("服务暂时不可用：" + e.getMessage());
}
```

## 最佳实践

1. **合理设置超时时间**：根据业务复杂度设置合适的 timeout
2. **关键服务使用 THROW 策略**：确保调用方能感知服务不可用
3. **非关键服务使用 CONTINUE**：允许降级处理
4. **配置健康检查**：对关键接口配置 @ApiHealthCheck
5. **监控告警**：启用 alertEnabled 进行持续监控

## 注意事项

- 检测器会自动从 Spring 容器中获取
- 超时控制会中断超时方法
- 重试机制会增加请求延迟
- 建议配合全局异常处理器使用
