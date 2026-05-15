# API 弃用注解系统

## 📋 概述

`@DeprecatedApi` 注解用于标记即将弃用或已弃用的 API 接口，支持配置弃用时间范围和替代方案。

### 核心功能

| 功能 | 说明 |
|------|------|
| **时间范围检测** | 根据 `since` 和 `until` 日期自动判断弃用状态 |
| **状态显示** | 尚未弃用 / 即将弃用 / 已弃用 |
| **替代方案索引** | 显示可用的新函数名 |
| **灵活处理** | 支持仅记录日志或抛出异常 |

---

## 🚀 快速开始

### 基础用法

```java
@GetMapping("/products/old")
@DeprecatedApi(
    since = "2026-01-01",
    until = "2026-06-01",
    replacement = "com.market.controller.ProductController#getProductsV2",
    reason = "使用新的分页参数"
)
public Result<List<Product>> getOldProducts() {
    return Result.success(productService.getOldProducts());
}
```

---

## 📖 注解参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `since` | String | "" | 弃用开始日期（格式：yyyy-MM-dd） |
| `until` | String | "" | 弃用结束日期（格式：yyyy-MM-dd） |
| `replacement` | String[] | {} | 替代方案索引数组 |
| `reason` | String | "" | 弃用原因说明 |
| `throwException` | boolean | false | 已弃用后是否抛出异常 |
| `message` | String | "此 API 已弃用，请使用替代方案" | 自定义警告/错误消息 |

---

## 📊 弃用状态说明

### 1. 尚未弃用（NOT_DEPRECATED）

**条件**：当前日期 < `since` 日期

**行为**：
- 正常执行方法
- 记录 INFO 级别日志

**日志示例**：
```
[API 弃用] 方法 com.market.controller.ExampleController#getOldProducts 尚未弃用 (since: 2026-05-01)
```

---

### 2. 即将弃用（PENDING_DEPRECATED）

**条件**：`since` 日期 ≤ 当前日期 ≤ `until` 日期

**行为**：
- 正常执行方法
- 记录 WARNING 级别日志
- 显示剩余天数和替代方案

**日志示例**：
```
[API 弃用] 方法 com.market.controller.ExampleController#getOldProducts 即将弃用，剩余 30 天 (since: 2026-01-01) (until: 2026-06-01)，原因：使用新的分页参数，请使用：com.market.controller.ProductController#getProductsV2
```

---

### 3. 已弃用（DEPRECATED）

**条件**：当前日期 > `until` 日期

**行为**：
- 记录 ERROR 级别日志
- 如果 `throwException = true`，抛出 `DeprecatedApiException`
- 如果 `throwException = false`，继续执行方法

**日志示例**：
```
[API 弃用] 方法 com.market.controller.ExampleController#getOldProducts 已弃用 (until: 2026-03-01)，原因：接口已下线，请迁移至新版本，替代方案：com.market.controller.ProductController#getProductsV2
```

**异常响应示例**：
```json
{
  "code": 500,
  "message": "此 API 已弃用，请使用替代方案",
  "data": null
}
```

---

## 💡 使用示例

### 示例 1：即将弃用的 API

```java
@GetMapping("/users/old")
@DeprecatedApi(
    since = "2026-01-01",
    until = "2026-06-01",
    replacement = "com.market.controller.UserController#getUsersV2",
    reason = "性能优化，新版本支持分页查询"
)
public Result<List<User>> getOldUsers() {
    return Result.success(userService.getAllUsers());
}
```

---

### 示例 2：已弃用并抛出异常

```java
@DeleteMapping("/orders/legacy")
@DeprecatedApi(
    since = "2025-12-01",
    until = "2026-03-01",
    replacement = {
        "com.market.controller.OrderController#cancelOrder",
        "com.market.controller.OrderController#refundOrder"
    },
    reason = "接口拆分，请使用新的取消和退款接口",
    throwException = true
)
public Result<Void> deleteLegacyOrder(@RequestParam Long orderId) {
    orderService.deleteLegacy(orderId);
    return Result.success();
}
```

---

### 示例 3：多个替代方案

```java
@PostMapping("/search/old")
@DeprecatedApi(
    since = "2026-01-01",
    until = "2026-05-01",
    replacement = {
        "com.market.controller.SearchController#searchProducts",
        "com.market.controller.SearchController#searchShops",
        "com.market.controller.SearchController#searchUsers"
    },
    reason = "搜索接口已拆分，请根据搜索类型选择对应接口"
)
public Result<SearchResult> oldSearch(@RequestParam String keyword) {
    return Result.success(searchService.oldSearch(keyword));
}
```

---

### 示例 4：类级别弃用

```java
@DeprecatedApi(
    since = "2026-01-01",
    until = "2026-12-31",
    replacement = "com.market.controller.ProductControllerV2",
    reason = "整个控制器已弃用，请迁移至 V2 版本"
)
@RestController
@RequestMapping("/api/legacy/products")
public class LegacyProductController {

    @GetMapping("/list")
    public Result<List<Product>> list() {
        // 继承类级别的弃用配置
        return Result.success(productService.list());
    }

    @GetMapping("/detail/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        // 继承类级别的弃用配置
        return Result.success(productService.detail(id));
    }
}
```

---

## 🔍 替代方案索引格式

替代方案索引采用以下格式：

```
包名。类名#方法名
```

**示例**：
- `com.market.controller.ProductController#getProductsV2`
- `com.market.service.UserService#getUserById`

**多个替代方案**：
```java
replacement = {
    "com.market.controller.ProductController#getProductsV2",
    "com.market.controller.ProductController#searchProducts"
}
```

---

## 🧪 测试示例

### 单元测试

```java
@SpringBootTest
class DeprecatedApiAspectTest {

    @Autowired
    private TestController testController;

    @Test
    void testNotDeprecated() {
        // 当前日期在 since 之前，应该正常执行
        Result<Product> result = testController.notDeprecated();
        assertEquals(200, result.getCode());
    }

    @Test
    void testPendingDeprecated() {
        // 当前日期在 since 和 until 之间，应该正常执行但记录警告
        Result<Product> result = testController.pendingDeprecated();
        assertEquals(200, result.getCode());
    }

    @Test
    void testDeprecatedWithException() {
        // 当前日期在 until 之后，应该抛出异常
        assertThrows(DeprecatedApiException.class, () -> {
            testController.deprecatedWithException();
        });
    }
}
```

---

## 📁 文件结构

```
backend/src/main/java/com/market/
├── annotation/
│   └── DeprecatedApi.java              # 弃用注解定义
├── aspect/
│   └── DeprecatedApiAspect.java        # 弃用检测切面
├── common/
│   └── DeprecationStatus.java          # 弃用状态枚举
├── exception/
│   └── DeprecatedApiException.java     # 弃用异常
└── controller/
    └── DeprecatedApiExampleController.java # 使用示例
```

---

## ⚙️ 技术实现

### 核心技术

| 技术 | 作用 |
|------|------|
| **Java 注解** | 声明式配置弃用信息 |
| **Spring AOP** | 动态代理拦截方法调用 |
| **`@Around` 环绕通知** | 在方法执行前判断弃用状态 |
| **java.time.LocalDate** | 官方日期 API 进行日期比较 |
| **反射 API** | 运行时获取注解参数 |

### 日期检测逻辑

```java
LocalDate currentDate = LocalDate.now();
LocalDate sinceDate = parseDate(deprecatedApi.since());
LocalDate untilDate = parseDate(deprecatedApi.until());

if (currentDate.isBefore(sinceDate)) {
    return DeprecationStatus.NOT_DEPRECATED;  // 尚未弃用
}
if (currentDate.isAfter(untilDate)) {
    return DeprecationStatus.DEPRECATED;      // 已弃用
}
return DeprecationStatus.PENDING_DEPRECATED;  // 即将弃用
```

---

## 📝 注意事项

1. **日期格式**：必须使用 `yyyy-MM-dd` 格式（如 `2026-01-01`）
2. **时区处理**：使用系统默认时区
3. **方法优先级**：方法上的注解配置优先于类上的配置
4. **异常处理**：建议配合全局异常处理器使用
5. **日志级别**：
   - NOT_DEPRECATED → INFO
   - PENDING_DEPRECATED → WARNING
   - DEPRECATED → ERROR

---

## 🔧 扩展功能

### 查询 API 弃用状态

`DeprecatedApiAspect` 提供 `getDeprecationInfo` 方法用于查询：

```java
@Autowired
private DeprecatedApiAspect deprecatedApiAspect;

DeprecationInfo info = deprecatedApiAspect.getDeprecationInfo(
    "com.market.controller.ExampleController",
    "getOldProducts"
);

if (info != null) {
    System.out.println("状态：" + info.getStatus().getDescription());
    System.out.println("替代方案：" + Arrays.toString(info.getReplacement()));
    System.out.println("剩余天数：" + info.getDaysUntilDeprecated());
}
```

---

## ✅ 最佳实践

1. **提前通知**：设置合理的 `since` 和 `until` 间隔（建议至少 30 天）
2. **清晰原因**：在 `reason` 中说明弃用原因
3. **明确替代**：提供详细的 `replacement` 索引
4. **渐进式弃用**：
   - 第一阶段：仅记录警告日志
   - 第二阶段：抛出异常阻止调用
5. **文档更新**：在 API 文档中标记弃用信息
