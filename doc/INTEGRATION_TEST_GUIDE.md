# 前后端联调测试文档

## 📋 目录

- [概述](#概述)
- [测试环境](#测试环境)
- [快速开始](#快速开始)
- [测试用例](#测试用例)
- [测试报告](#测试报告)
- [故障排查](#故障排查)
- [扩展指南](#扩展指南)

---

## 概述

### 什么是联调测试？

联调测试（Integration Testing）是验证前后端系统协同工作的测试方法。本项目的联调测试覆盖：

1. **API 端点验证** - 确保所有 API 端点可正常访问
2. **数据格式验证** - 验证前后端数据格式兼容性
3. **认证授权验证** - 测试 JWT 认证和权限控制
4. **业务流程验证** - 测试完整的业务流程链路

### 测试范围

| 模块 | 测试内容 | 文件位置 |
|------|---------|---------|
| 认证模块 | 注册、登录、JWT 验证、权限控制 | `backend/src/test/java/com/market/integration/AuthIntegrationTest.java` |
| 商品模块 | CRUD、搜索、收藏、推荐、浏览历史 | `backend/src/test/java/com/market/integration/ProductIntegrationTest.java` |
| 订单模块 | 创建、支付、发货、确认、评价 | `backend/src/test/java/com/market/integration/OrderIntegrationTest.java` |
| 前端 API | 使用 Axios 模拟前端调用 | `integration-test.js` |
| 测试套件 | 统一测试执行和报告 | `backend/src/test/java/com/market/integration/AllIntegrationTestSuite.java` |

### 技术栈

- **后端测试**: JUnit 5 + Spring MockMvc
- **前端测试**: Node.js + Axios
- **测试数据库**: H2 内存数据库
- **报告生成**: HTML 可视化报告

---

## 测试环境

### 环境要求

```
✅ Java 21+
✅ Maven 3.8+
✅ Node.js 18+
✅ 内存: 至少 4GB 可用
```

### 配置文件

#### 1. 后端测试配置

文件: `backend/src/test/resources/application-integration.properties`

```properties
# 使用 H2 内存数据库
spring.datasource.url=jdbc:h2:mem:integrationdb;DB_CLOSE_DELAY=-1

# 服务器端口
server.port=8081

# CORS 配置
spring.web.cors.allowed-origins=http://localhost:5173,http://localhost:3000

# JWT 配置
jwt.secret=integration-test-secret-key-2026
jwt.expiration=7200000

# 禁用 Redis
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
```

#### 2. 测试数据初始化

文件: `backend/src/test/resources/data-integration-test.sql`

自动插入测试数据：
- 3 个测试用户（用户、商家、管理员）
- 3 个商品分类
- 3 个测试商品
- 1 个测试店铺
- 2 个测试优惠券

---

## 快速开始

### 方式一：一键运行（推荐）

#### Windows

```bash
run-integration-tests.bat
```

#### Linux/Mac

```bash
chmod +x run-integration-tests.sh
./run-integration-tests.sh
```

**执行流程：**
1. ✅ 检查环境（Java、Maven、Node.js）
2. 🧹 清理之前的测试数据
3. 🚀 启动后端服务（测试模式）
4. 🧪 执行联调测试
5. 📊 生成测试报告

### 方式二：分步运行

#### 1. 运行后端集成测试

```bash
cd backend
mvn test -Dtest=*IntegrationTest
```

运行所有集成测试类：

```bash
mvn test -Dtest=AuthIntegrationTest
mvn test -Dtest=ProductIntegrationTest
mvn test -Dtest=OrderIntegrationTest
```

#### 2. 运行前端联调测试

```bash
# 安装依赖
npm install axios

# 确保后端运行在 8080 端口
# 然后运行测试
node integration-test.js
```

#### 3. 使用 NPM 脚本

```bash
# 运行后端集成测试
npm run test:backend

# 运行前端联调测试
npm run test:integration

# 运行所有测试
npm run test:all

# 查看测试报告
npm run report
```

### 方式三：IDE 运行

在 IntelliJ IDEA 或 Eclipse 中：

1. 打开 `backend/src/test/java/com/market/integration/`
2. 右键点击测试类
3. 选择 "Run 'AuthIntegrationTest'"

---

## 测试用例

### 1. 认证模块测试 (AuthIntegrationTest)

| 序号 | 测试用例 | 验证内容 | 预期结果 |
|------|---------|---------|---------|
| 1 | 用户注册 | 数据格式、业务逻辑 | 返回 200，用户信息正确 |
| 2 | 用户登录 | JWT Token 生成 | 返回 Token 和 RefreshToken |
| 3 | 商家登录 | 角色权限 | 返回商家 Token |
| 4 | 管理员登录 | 管理员权限 | 返回管理员 Token |
| 5 | 获取用户信息 | 认证拦截器 | 返回用户信息，不含密码 |
| 6 | 未认证访问 | 安全拦截 | 返回 401 Unauthorized |
| 7 | 错误密码登录 | 错误处理 | 返回 401 |
| 8 | 重复用户名 | 业务校验 | 返回 400 |

**示例代码：**

```java
@Test
@DisplayName("用户登录 - 验证JWT token生成")
void testUserLogin() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setUsername("testuser");
    request.setPassword("testpassword");

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.token").exists());
}
```

### 2. 商品模块测试 (ProductIntegrationTest)

| 序号 | 测试用例 | 验证内容 | 预期结果 |
|------|---------|---------|---------|
| 1 | 商品列表 | 分页、筛选 | 返回分页数据 |
| 2 | 商品详情 | 详细信息 | 返回商品完整信息 |
| 3 | 搜索商品 | 关键词搜索 | 返回匹配结果 |
| 4 | 推荐商品 | 推荐算法 | 返回推荐列表 |
| 5 | 商品分类 | 分类列表 | 返回所有分类 |
| 6 | 创建商品 | 商家权限 | 创建成功，返回商品ID |
| 7 | 更新商品 | 商品信息修改 | 更新成功 |
| 8 | 收藏商品 | 收藏功能 | 收藏成功 |
| 9 | 收藏状态 | 状态查询 | 返回已收藏 |
| 10 | 浏览记录 | 浏览历史 | 添加成功 |
| 11 | 未登录收藏 | 权限拦截 | 返回 401 |
| 12 | 热门商品 | 热门列表 | 返回热门商品 |
| 13 | 批量查询 | 批量接口 | 返回指定商品 |

**测试数据流：**

```
登录获取Token → 查询商品列表 → 查看商品详情 → 搜索商品 → 
收藏商品 → 添加浏览记录 → 创建新商品 → 更新商品
```

### 3. 订单模块测试 (OrderIntegrationTest)

| 序号 | 测试用例 | 验证内容 | 预期结果 |
|------|---------|---------|---------|
| 1 | 创建订单 | 订单创建流程 | 返回订单ID和订单号 |
| 2 | 订单列表 | 分页查询 | 返回订单列表 |
| 3 | 订单详情 | 详细信息 | 返回订单完整信息 |
| 4 | 支付订单 | 支付流程 | 支付成功 |
| 5 | 支付状态 | 状态查询 | 返回已支付 |
| 6 | 商家订单 | 商家权限 | 返回商家订单列表 |
| 7 | 商家发货 | 发货流程 | 发货成功，更新物流 |
| 8 | 确认收货 | 收货流程 | 订单状态更新 |
| 9 | 提交评价 | 评价功能 | 评价成功 |
| 10 | 取消订单 | 取消流程 | 订单取消 |
| 11 | 订单统计 | 统计数据 | 返回统计信息 |
| 12 | 未登录访问 | 权限拦截 | 返回 401 |
| 13 | 访问他人订单 | 数据权限 | 返回 403 |

**订单状态流：**

```
创建订单 → 支付订单 → 商家发货 → 用户确认收货 → 提交评价
                ↓
           取消订单 (任意时刻)
```

### 4. 前端联调测试 (integration-test.js)

前端测试脚本覆盖 25 个测试场景：

```javascript
// 测试示例
await runTest('用户登录 - 获取 Token', async () => {
    const response = await api.post('/auth/login', {
        username: 'testuser',
        password: 'testpassword'
    });
    if (response.data.code !== 200 || !response.data.data.token) {
        throw new Error('登录失败');
    }
    global.authToken = response.data.data.token;
});
```

**测试覆盖：**
- ✅ 健康检查
- ✅ 用户认证（注册、登录）
- ✅ 商品操作（查询、搜索、收藏）
- ✅ 订单流程（创建、支付、取消）
- ✅ 用户功能（通知、浏览历史）
- ✅ 安全验证（未授权访问、错误处理）
- ✅ 数据格式验证（价格格式、分页结构）

---

## 测试报告

### HTML 可视化报告

运行测试后自动生成 `integration-test-report.html`，包含：

- 📊 **统计概览** - 总数、通过、失败、通过率
- 📈 **进度条** - 可视化通过率
- 📝 **测试详情** - 每个测试的执行结果和错误信息

### 报告示例

```
┌─────────────────────────────────────────┐
│  🔗 前后端联调测试报告                    │
│  生成时间: 2026-04-07 14:30:00           │
│  后端地址: http://localhost:8080         │
├─────────────────────────────────────────┤
│  总测试数: 25    通过: 24    失败: 1     │
│  通过率: 96%                            │
├─────────────────────────────────────────┤
│  ✅ [120ms] 健康检查 - 应用启动验证      │
│  ✅ [230ms] 用户登录 - 获取 Token        │
│  ❌ [45ms]  创建订单 - 地址不存在        │
│     错误: 收货地址不存在                 │
└─────────────────────────────────────────┘
```

### 查看报告

```bash
# Windows
start integration-test-report.html

# Mac
open integration-test-report.html

# Linux
xdg-open integration-test-report.html
```

---

## 故障排查

### 常见问题

#### 1. 后端启动失败

**错误信息：**
```
Port 8080 was already in use
```

**解决方案：**
```bash
# Windows - 查找并关闭占用端口的进程
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

#### 2. 测试数据库初始化失败

**错误信息：**
```
Table "USERS" not found
```

**解决方案：**
- 检查 `data-integration-test.sql` 中的表名是否与实际表结构匹配
- 确认 JPA 的 `ddl-auto=create-drop` 配置正确

#### 3. JWT Token 无效

**错误信息：**
```
JWT signature does not match
```

**解决方案：**
- 确认 `application-integration.properties` 中的 `jwt.secret` 与主配置一致
- 检查 Token 是否过期

#### 4. CORS 错误

**错误信息：**
```
Access to XMLHttpRequest has been blocked by CORS policy
```

**解决方案：**
- 确认后端 CORS 配置允许前端地址
- 检查请求头是否正确

#### 5. 前端测试脚本连接失败

**错误信息：**
```
connect ECONNREFUSED 127.0.0.1:8080
```

**解决方案：**
- 确认后端服务已启动
- 检查 `BACKEND_URL` 环境变量是否正确

### 调试技巧

#### 1. 查看详细日志

```bash
# 后端调试日志
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=integration -Dlogging.level.com.market=DEBUG
```

#### 2. 单独运行某个测试

```bash
# 只运行认证测试
mvn test -Dtest=AuthIntegrationTest

# 运行特定测试方法
mvn test -Dtest=AuthIntegrationTest#testUserLogin
```

#### 3. 使用 Postman 手动测试

导入测试用例到 Postman：

```json
{
  "name": "用户登录",
  "request": {
    "method": "POST",
    "url": "http://localhost:8080/api/auth/login",
    "header": { "Content-Type": "application/json" },
    "body": {
      "username": "testuser",
      "password": "testpassword"
    }
  }
}
```

---

## 扩展指南

### 添加新的测试用例

#### 1. 后端集成测试

在 `backend/src/test/java/com/market/integration/` 创建新测试类：

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NewModuleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    @DisplayName("测试新功能")
    void testNewFeature() throws Exception {
        mockMvc.perform(get("/api/new-endpoint"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
```

#### 2. 前端联调测试

在 `integration-test.js` 的 `runAllTests()` 函数中添加：

```javascript
await runTest('新功能测试', async () => {
    const response = await api.get('/new-endpoint');
    if (response.data.code !== 200) {
        throw new Error('新功能测试失败');
    }
});
```

### 测试数据管理

#### 清理测试数据

测试结束后清理测试数据：

```sql
-- 清理测试用户
DELETE FROM users WHERE username LIKE 'integration_%';

-- 清理测试商品
DELETE FROM products WHERE title LIKE '测试商品%';

-- 清理测试订单
DELETE FROM orders WHERE remark LIKE '联调测试%';
```

#### 使用测试 Fixture

创建测试数据工厂类：

```java
public class TestDataFactory {
    
    public static User createTestUser() {
        User user = new User();
        user.setUsername("test_user_" + System.currentTimeMillis());
        user.setPassword("Test123456!");
        user.setEmail("test" + System.currentTimeMillis() + "@test.com");
        return user;
    }
    
    public static Product createTestProduct(Long sellerId) {
        Product product = new Product();
        product.setTitle("Test Product " + System.currentTimeMillis());
        product.setPrice(new BigDecimal("99.99"));
        product.setSellerId(sellerId);
        return product;
    }
}
```

### 性能测试

使用 Apache JMeter 或 k6 进行性能测试：

```javascript
// k6 性能测试示例
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 10,
  duration: '30s',
};

export default function () {
  const res = http.get('http://localhost:8080/api/product');
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
  sleep(1);
}
```

### CI/CD 集成

在 GitHub Actions 中运行联调测试：

```yaml
name: Integration Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  integration-tests:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: password
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Run Backend Integration Tests
        run: |
          cd backend
          mvn test -Dtest=*IntegrationTest
      
      - name: Run Frontend Integration Tests
        run: |
          npm install axios
          node integration-test.js
        env:
          BACKEND_URL: http://localhost:8080
```

---

## 最佳实践

### 1. 测试命名规范

```
{模块}IntegrationTest.java
test{功能场景}()
```

### 2. 测试顺序

```
@BeforeAll → 认证测试 → 查询测试 → 创建测试 → 更新测试 → 删除测试 → @AfterAll
```

### 3. 测试数据隔离

- 使用独立的测试数据库（H2）
- 每个测试使用不同的测试数据
- 测试结束后清理数据

### 4. 断言策略

```java
// ✅ 好的断言
.andExpect(status().isOk())
.andExpect(jsonPath("$.code").value(200))
.andExpect(jsonPath("$.data.id").exists())
.andExpect(jsonPath("$.data.password").doesNotExist())

// ❌ 避免的断言
.andExpect(status().isOk()) // 只断言状态码不够
```

### 5. 错误处理测试

```java
@Test
void testNotFound() throws Exception {
    mockMvc.perform(get("/api/product/999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").exists());
}
```

---

## 附录

### A. 测试配置文件清单

| 文件 | 用途 |
|------|------|
| `application-integration.properties` | 后端联调测试配置 |
| `data-integration-test.sql` | 测试数据初始化 |
| `integration-test.js` | 前端联调测试脚本 |
| `run-integration-tests.bat` | Windows 一键测试脚本 |
| `run-integration-tests.sh` | Linux/Mac 一键测试脚本 |

### B. 测试文件清单

| 文件 | 测试数 | 覆盖模块 |
|------|--------|---------|
| `AuthIntegrationTest.java` | 8 | 认证、授权 |
| `ProductIntegrationTest.java` | 13 | 商品管理 |
| `OrderIntegrationTest.java` | 13 | 订单流程 |
| `AllIntegrationTestSuite.java` | 3 | 健康检查、CORS |
| `integration-test.js` | 25 | 前端API调用 |

### C. 常用命令速查

```bash
# 运行所有后端集成测试
mvn test -Dtest=*IntegrationTest

# 运行单个测试类
mvn test -Dtest=AuthIntegrationTest

# 运行单个测试方法
mvn test -Dtest=AuthIntegrationTest#testUserLogin

# 运行前端联调测试
node integration-test.js

# 查看测试报告
start integration-test-report.html

# 清理测试数据
mvn clean
```

### D. 联系与支持

- 📧 问题反馈: 提交 Issue
- 💬 讨论: 项目 Discussions
- 📖 文档: `/doc` 目录

---

**最后更新**: 2026-04-07  
**维护者**: Market Development Team
