# API 可用性检测注解系统 - 完整文档

## 📖 概述

基于系统已有的消息状态库（`ChatMessage`、`UserNotification`、`SystemMessage`）设计的 API 可用性检测注解系统。

### 设计理念

参考系统现有消息状态设计：
- **状态码**：参考 `ChatMessage.status`（1000-5000 分段）
- **消息级别**：参考 `UserNotification.level`（INFO、WARNING、URGENT）
- **优先级**：参考 `SystemMessage.priority`（1-5）

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                    Controller 层                          │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────┐ │
│  │业务控制器    │  │ApiHealthCtrl │  │TestController  │ │
│  └──────┬──────┘  └──────┬───────┘  └────────┬───────┘ │
└─────────┼────────────────┼───────────────────┼─────────┘
          │                │                    │
┌─────────┴────────────────┴────────────────────┴─────────┐
│                    Aspect 层                              │
│  ┌─────────────────────────────────────────────────────┐│
│  │          ApiAvailabilityAspect (切面)                ││
│  │  - 可用性检测                                        ││
│  │  - 超时控制                                          ││
│  │  - 重试机制                                          ││
│  │  - 健康状态缓存                                      ││
│  └─────────────────────────────────────────────────────┘│
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────┴──────────────────────────────┐
│                    核心组件                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  注解        │  │  检测器      │  │  异常类      │  │
│  │@ApiAvailable │  │Detector      │  │Exception     │  │
│  │@ApiHealthChk │  │              │  │              │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                           │
┌──────────────────────────┴──────────────────────────────┐
│                    状态码系统                             │
│  ┌──────────────────────────────────────────────────┐   │
│  │            ApiStatusCode (状态码枚举)              │   │
│  │  2000-2999: 成功  │  3000-3999: 处理中            │   │
│  │  4000-4999: 失败  │  5000: 未知                   │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │            ApiStatusResult (状态响应)              │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 文件结构

```
backend/src/main/java/com/market/
├── annotation/
│   ├── ApiAvailable.java                 # 主注解
│   ├── ApiHealthCheck.java               # 健康检查注解
│   ├── ApiAvailabilityDetector.java      # 检测器接口
│   ├── DatabaseAvailabilityDetector.java # 数据库检测器
│   ├── RedisAvailabilityDetector.java    # Redis 检测器
│   └── ExternalApiAvailabilityDetector.java # 外部 API 检测器
│
├── aspect/
│   ├── ApiAvailabilityAspect.java        # 切面实现
│   ├── ReviewLogAspect.java              # (现有)
│   └── ChatLogAspect.java                # (现有)
│
├── common/
│   ├── Result.java                       # (现有)
│   ├── ApiStatusCode.java                # ★ 新增：状态码枚举
│   └── ApiStatusResult.java              # ★ 新增：状态响应
│
├── controller/
│   ├── ApiHealthController.java          # ★ 新增：健康监控
│   ├── ApiAvailabilityTestController.java# ★ 新增：测试控制器
│   └── ... (其他业务控制器)
│
├── entity/
│   ├── ChatMessage.java                  # (现有) 参考：状态码
│   ├── UserNotification.java             # (现有) 参考：消息级别
│   ├── SystemMessage.java                # (现有) 参考：优先级
│   └── MessageReceive.java               # (现有) 参考：已读状态
│
├── exception/
│   └── ApiAvailabilityException.java     # ★ 改进：可用性异常
│
└── config/
    └── TestConfig.java                   # 测试配置
```

---

## 🎯 核心功能

### 1. 超时控制

```java
@GetMapping("/products")
@ApiAvailable(timeout = 5000)  // 5 秒超时
public Result<List<Product>> getProducts() {
    return Result.success(productService.getProducts());
}
```

### 2. 依赖服务检测

```java
@GetMapping("/orders")
@ApiAvailable(
    dependencies = {"database", "redis"}  // 检测数据库和 Redis
)
public Result<List<Order>> getOrders() {
    return Result.success(orderService.getOrders());
}
```

### 3. 失败处理策略

```java
@ApiAvailable(
    onFailure = ApiAvailable.FailureAction.RETURN_ERROR  // 失败返回错误
)
```

| 策略 | 说明 |
|------|------|
| `THROW` | 抛出异常 |
| `RETURN_ERROR` | 返回错误响应 |
| `CONTINUE` | 继续执行（降级） |

### 4. 重试机制

```java
@ApiAvailable(
    retryCount = 2,        // 重试 2 次
    retryInterval = 500    // 间隔 500ms
)
```

### 5. 健康状态监控

```java
// 查询所有服务状态
GET /api/health/status

// 查询指定服务
GET /api/health/status/{serviceName}
```

---

## 📊 状态码系统

### 成功状态 (2000-2999)

| 代码 | 说明 | 级别 |
|------|------|------|
| 2000 | 服务可用 | INFO |
| 2001 | 降级模式 | INFO |
| 2002 | 功能受限 | WARNING |

### 处理中 (3000-3999)

| 代码 | 说明 | 级别 |
|------|------|------|
| 3000 | 正在检测 | INFO |
| 3100 | 正在重试 | INFO |
| 3200 | 等待依赖 | INFO |

### 失败状态 (4000-4999)

| 代码 | 说明 | 级别 |
|------|------|------|
| 4000 | 服务不可用 | URGENT |
| 4001 | 服务超时 | URGENT |
| 4100 | 依赖不可用 | URGENT |
| 4101 | 数据库不可用 | URGENT |
| 4102 | Redis 不可用 | URGENT |
| 4200 | 检测器失败 | URGENT |
| 4300 | 服务异常 | URGENT |

---

## 🔧 使用指南

### 快速开始

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @GetMapping
    @ApiAvailable(
        timeout = 5000,
        dependencies = {"database"},
        onFailure = ApiAvailable.FailureAction.RETURN_ERROR
    )
    public Result<List<Order>> getOrders() {
        return Result.success(orderService.getOrders());
    }
}
```

### 完整示例

```java
@PostMapping("/create")
@ApiAvailable(
    timeout = 10000,
    retryCount = 2,
    retryInterval = 500,
    dependencies = {"database", "redis"},
    onFailure = ApiAvailable.FailureAction.THROW,
    errorMessage = "订单创建失败"
)
@ApiHealthCheck(
    critical = true,
    alertEnabled = true,
    alertThreshold = 3
)
public Result<Order> createOrder(@RequestBody Order order) {
    return Result.success(orderService.createOrder(order));
}
```

---

## 🧪 测试方法

### 1. 单元测试

```bash
# 运行测试类
java com.market.aspect.ApiAvailabilityIntegrationTest
```

### 2. 集成测试

```bash
# 启动服务后
test-availability.bat
```

### 3. 手动测试端点

```bash
# 基础测试
curl http://localhost:8080/api/test/availability/basic

# 数据库依赖
curl http://localhost:8080/api/test/availability/database

# 超时测试
curl http://localhost:8080/api/test/availability/timeout-test?delayMs=2000

# 健康状态
curl http://localhost:8080/api/health/status
```

---

## 📈 监控与告警

### 健康状态查询

```bash
# 所有服务
GET /api/health/status
```

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "timestamp": "2024-01-01T12:00:00",
    "status": "UP",
    "services": {
      "api": {"name": "API 服务", "status": "UP"},
      "database": {"name": "数据库", "status": "UP"},
      "redis": {"name": "Redis", "status": "DOWN"}
    }
  }
}
```

### 日志监控

```
INFO  API 可用性检测通过：com.market.controller.OrderController.getOrders - 响应时间：150ms
ERROR API 可用性检测失败：com.market.controller.OrderController.getOrders - 状态码：4101 - 数据库不可用
```

---

## ❓ 常见问题

### Q1: 注解不生效

**检查：**
1. 方法/类上有 `@ApiAvailable`
2. `ApiAvailabilityAspect` 有 `@Aspect` 和 `@Component`
3. Spring 扫描到 aspect 包

### Q2: 状态码不显示

**解决：** 确保返回 `Result.error(code, message)` 格式

### Q3: 健康状态为空

**解决：** 先调用带注解的接口，健康状态会被缓存

---

## 📚 相关文档

- `API_AVAILABILITY_GUIDE.md` - 使用指南
- `QUICK_START.md` - 快速开始
- `TEST_GUIDE.md` - 测试指南
- `改进说明.md` - 改进说明
- `如何测试.md` - 测试方法
- `启动服务指南.md` - 启动方法

---

## 📝 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2024-01-01 | 初始版本 |
| 1.1 | 2024-01-01 | 整合消息状态库，添加状态码系统 |
