# 电子商务平台 - 全面测试指南

本指南提供了完整的系统测试流程，包括数据库检查、API冒烟测试、压力测试、安全测试、集成测试和性能基准测试。

## 环境准备

- 后端服务运行在 `http://localhost:8080`
- PostgreSQL数据库运行在5432端口，数据库名 `market`，用户 `market`，密码 `market`
- PowerShell 7+（推荐）或 Windows PowerShell 5.1+

## 测试脚本目录结构

```
market/testing/
├── scripts/
│   ├── api-smoke-test.ps1       # API冒烟测试
│   ├── load-test.ps1            # 压力测试（并发用户）
│   ├── security-test.ps1        # 安全测试（SQL注入/XSS等）
│   ├── integration-test.ps1     # 集成测试（端到端流程）
│   └── performance-test.ps1     # 性能基准测试
├── db-init.sql                  # 数据库初始化脚本
└── README.md                    # 本文件
```

## 1. 数据库准备

### 1.1 数据库完整性检查

运行 `db-integrity-check.sql` 脚本检查数据库完整性：

```sql
-- 在PostgreSQL中执行
\i /path/to/db-integrity-check.sql
```

### 1.2 数据初始化

运行 `init-complete.sql` 脚本初始化测试数据：

```sql
-- 在PostgreSQL中执行
\i /path/to/init-complete.sql
```

此脚本将：
- 清空所有表（级联）
- 插入VIP等级（6个）
- 插入商品（6个）
- 插入店铺、公告、敏感词等

## 2. API冒烟测试

运行基本API功能测试，验证所有核心端点是否正常工作。

```powershell
cd D:\market\testing\scripts
Powershell -ExecutionPolicy Bypass -File .\api-smoke-test.ps1
```

**预期输出**：
- 所有测试通过率 ≥ 80%
- 无500内部服务器错误
- 购物车添加功能正常

## 3. 压力测试

模拟多用户并发请求，测试系统在高负载下的表现。

```powershell
cd D:\market\testing\scripts
Powershell -ExecutionPolicy Bypass -File .\load-test.ps1 -Users 20 -Requests 50
```

**参数说明**：
- `-Users`：并发用户数（默认10）
- `-RequestsPerUser`：每个用户的请求数（默认20）

**监控指标**：
- 总请求数
- 成功率
- 平均/最大响应时间
- 各端点成功率

## 4. 安全测试

执行常见安全漏洞检测（SQL注入、XSS、认证绕过、IDOR等）。

```powershell
cd D:\market\testing\scripts
Powershell -ExecutionPolicy Bypass -File .\security-test.ps1
```

**检测项**：
- SQL注入（搜索参数）
- 反射型XSS
- 认证绕过
- IDOR（越权访问）
- 敏感信息泄露
- 速率限制

**注意**：此脚本执行修改操作（注册用户），请在测试环境运行。

## 5. 集成测试

端到端业务流程测试，模拟完整用户操作。

```powershell
cd D:\market\testing\scripts
Powershell -ExecutionPolicy Bypass -File .\integration-test.ps1
```

**测试流程**：
1. 用户注册 → 2. 登录 → 3. 浏览商品 → 4. 搜索商品 → 5. 查看详情 → 6. 加入购物车 → 7. 更新数量 → 8. 选中商品 → 9. 检查库存 → 10. 创建订单 → 11. 查看订单 → 12. 获取奖品 → 13. 清空购物车

**预期结果**：所有步骤成功完成。

## 6. 性能基准测试

测量各API端点的响应时间和吞吐量。

```powershell
cd D:\market\testing\scripts
Powershell -ExecutionPolicy Bypass -File .\performance-test.ps1
```

**测试指标**：
- 平均响应时间（ms）
- 最小/最大响应时间
- P95响应时间
- 吞吐量（req/s）
- 错误率

## 7. 测试报告

所有测试脚本都会生成JSON格式的报告文件，保存在 `D:/market/testing/` 目录下：

- `api-smoke-test-report.json`
- `load-test-report.json`
- `performance-report.json`

## 常见问题排查

### 1. 端口8080被占用

```powershell
# 查找占用进程
netstat -ano | findstr :8080
# 终止进程
taskkill /F /PID <PID>
```

### 2. 数据库连接失败

确认PostgreSQL服务已启动，且连接参数正确。

### 3. 脚本执行权限

使用 `-ExecutionPolicy Bypass` 参数绕过执行策略限制。

### 4. 购物车添加失败

确保使用查询参数方式（而非JSON Body）传递 `productId` 和 `quantity`。

## 测试通过标准

| 测试类型 | 通过标准 |
|---------|---------|
| API冒烟测试 | 核心端点通过率 ≥ 80% |
| 压力测试 | 成功率 ≥ 95%（并发50用户） |
| 安全测试 | 无高危漏洞（SQL注入/XSS） |
| 集成测试 | 所有步骤100%通过 |
| 性能测试 | 平均响应时间 < 500ms |

## 联系方式

如遇问题，请联系开发团队或提交issue。

---
**更新时间**: 2026-05-06