# API 可用性检测注解 - 完整测试方案

## 📋 测试概览

| 测试类型 | 说明 | 是否需要 Spring |
|----------|------|----------------|
| 单元测试 | 验证注解配置 | ❌ 否 |
| 集成测试 | 验证完整功能 | ✅ 是 |
| 手动测试 | 使用脚本测试 | ✅ 是 |

---

## 🚀 快速测试（3 步）

### 步骤 1: 运行单元测试

```bash
# 在 IDE 中右键运行，或命令行运行
java com.market.aspect.ApiAvailabilityIntegrationTest
```

**预期输出：**
```
========================================
  API 可用性检测注解 - 功能测试
========================================

测试 1: 验证注解存在性...
  ✓ 所有类都存在

测试 2: 验证注解属性值...
  ✓ 所有属性值正确

测试 3: 验证失败策略枚举...
  枚举值:
    - THROW
    - RETURN_ERROR
    - CONTINUE
  ✓ 枚举定义正确

测试 4: 验证健康检查注解...
  ✓ 健康检查注解配置正确

========================================
  ✓ 所有测试通过!
========================================
```

### 步骤 2: 启动后端服务

```bash
# 方式 1: 使用 Maven
cd d:\market
mvn spring-boot:run -pl backend

# 方式 2: 在 IDE 中运行
# 右键 -> backend/src/main/java/com/market/MarketApplication.java -> Run
```

### 步骤 3: 运行集成测试脚本

```bash
# Windows
test-availability.bat

# 或手动使用 curl 测试
curl http://localhost:8080/api/test/availability/basic
```

---

## 📝 详细测试用例

### 1. 基础功能测试

```bash
curl http://localhost:8080/api/test/availability/basic
```

**预期响应：**
```json
{"code":200,"message":"基础测试通过","data":"基础测试通过"}
```

---

### 2. 数据库依赖测试

```bash
curl http://localhost:8080/api/test/availability/database
```

**预期响应（数据库可用）：**
```json
{"code":200,"message":"数据库测试通过","data":"数据库测试通过"}
```

**预期响应（数据库不可用）：**
```json
{"code":500,"message":"依赖服务 [database] 不可用"}
```

---

### 3. Redis 依赖测试

```bash
curl http://localhost:8080/api/test/availability/redis
```

**预期响应（Redis 可用）：**
```json
{"code":200,"message":"Redis 测试通过"}
```

**预期响应（Redis 不可用）：**
```json
{"code":500,"message":"依赖服务 [redis] 不可用"}
```

---

### 4. 多依赖测试

```bash
curl http://localhost:8080/api/test/availability/multi-dependencies
```

**预期：** 同时检测 database 和 redis

---

### 5. 超时控制测试

```bash
# 设置延迟 2 秒，超时时间 1 秒
curl -m 5 http://localhost:8080/api/test/availability/timeout-test?delayMs=2000
```

**预期响应（1 秒后返回）：**
```json
{"code":500,"message":"方法执行超时（超过 1000ms）"}
```

---

### 6. 失败处理策略测试

#### THROW 策略
```bash
curl http://localhost:8080/api/test/availability/throw-exception
```
**预期：** 抛出 `ApiAvailabilityException`

#### RETURN_ERROR 策略
```bash
curl http://localhost:8080/api/test/availability/database
```
**预期：** 返回 `Result.error()`

#### CONTINUE 策略
```bash
curl http://localhost:8080/api/test/availability/continue-on-failure
```
**预期：** 继续执行并返回成功

---

## 🔍 验证切面生效的方法

### 方法 1: 查看日志

启动服务后观察日志：

```
2024-XX-XX INFO  API 可用性检测通过：com.market.controller.ApiAvailabilityTestController.basicTest
2024-XX-XX ERROR API 可用性检测失败：com.market.controller.ApiAvailabilityTestController.databaseTest - 依赖服务 [database] 不可用
```

### 方法 2: 使用 Postman/浏览器

访问测试端点查看响应时间和内容。

### 方法 3: 使用 Actuator 健康检查

如果配置了 Spring Actuator：

```bash
curl http://localhost:8080/actuator/health
```

---

## ❓ 常见问题排查

### Q1: 注解不生效

**解决方案：**
1. 确认类/方法上有 `@ApiAvailable` 注解
2. 确认 `ApiAvailabilityAspect` 类有 `@Component` 注解
3. 确认 Spring 扫描到 aspect 包

### Q2: 404 Not Found

**解决方案：**
1. 确认服务已启动
2. 确认端口是 8080
3. 确认 URL 路径正确：`/api/test/availability/*`

### Q3: 超时测试不超时

**解决方案：**
1. 确认 `delayMs` 参数大于 timeout 值
2. 确认 timeout 单位是毫秒

---

## 📊 测试检查清单

```
□ 单元测试通过
□ 服务启动成功
□ 基础接口返回正常
□ 数据库依赖检测正常
□ Redis 依赖检测正常
□ 超时控制正常（1 秒返回）
□ 失败策略 THROW 正常
□ 失败策略 RETURN_ERROR 正常
□ 失败策略 CONTINUE 正常
□ 日志输出正常
```

---

## 📁 测试相关文件

```
backend/
├── src/main/java/com/market/
│   ├── annotation/
│   │   ├── ApiAvailable.java
│   │   ├── ApiHealthCheck.java
│   │   └── *.java (检测器)
│   ├── aspect/
│   │   └── ApiAvailabilityAspect.java
│   ├── exception/
│   │   └── ApiAvailabilityException.java
│   └── controller/
│       └── ApiAvailabilityTestController.java
└── src/test/java/com/market/
    └── aspect/
        ├── ApiAvailabilityIntegrationTest.java
        └── TEST_GUIDE.md

test-availability.bat (测试脚本)
```

---

## 🎯 测试完成标志

所有测试通过后，你应该看到：

1. ✅ 单元测试输出 "所有测试通过"
2. ✅ 所有 curl 请求返回预期响应
3. ✅ 日志中有正确的检测和错误信息
4. ✅ 超时测试在指定时间内返回

如果以上都满足，说明 API 可用性检测注解功能正常！
