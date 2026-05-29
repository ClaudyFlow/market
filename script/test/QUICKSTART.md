# 快速启动指南

## 方式一：本地 PostgreSQL（推荐）

### 1. 初始化数据库

```cmd
cd D:\market\script\build
init-postgresql-local.bat
```

该脚本会：
- 临时启用 trust 认证
- 创建用户 `market`（密码 `market`）
- 创建数据库 `market`
- 恢复安全配置

### 2. 启动后端

```cmd
cd D:\market\backend
mvn spring-boot:run
```

### 3. 启动前端

```cmd
cd D:\market\frontend
npm install
npm run dev
```

---

## 方式二：使用已有服务

如果本地已有 PostgreSQL 和 Redis 服务：

### 1. 配置数据库连接

编辑 `backend/src/main/resources/application.properties`：
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/market
spring.datasource.username=market
spring.datasource.password=market
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 2. 启动后端

```cmd
cd D:\market\backend
mvn spring-boot:run
```

---

## 账号信息

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 所有权限 |
| 商家 | merchant1 | 123456 | 商品管理 |
| 用户 | user1 | 123456 | 普通用户 |

---

## 验证后端状态

```cmd
curl http://localhost:8080/actuator/health
```

预期输出：
```json
{"status":"UP"}
```

---

## 停止服务

按 `Ctrl+C` 停止后端或前端

---

## 遇到问题？

### 问题：端口 8080 被占用

**解决**：
```cmd
netstat -ano | findstr :8080
```
找到占用进程并结束，或修改 `application.properties` 中的 `server.port`。

### 问题：数据库连接失败

**解决**：
1. 确认 PostgreSQL 运行：`net start postgresql-x64-18`
2. 检查数据库是否存在：`psql -U market -d market -c "\dt"`
3. 查看日志：`D:\market\log\market.*.log`

### 问题：Redis 连接失败

**解决**：
1. 确认 Redis 运行：`redis-cli ping`
2. 如需密码：`redis-cli -a 123456 ping`

### 更多帮助

详细文档见 `POSTGRESQL_SETUP.md`