# 修复完成 - 后端启动指南

## 已修复的问题

### 1. 配置问题
- ✅ `application.properties`：默认 profile = dev（PostgreSQL）
- ✅ `application-dev.properties`：凭据 = market/market
- ✅ Lombok 版本：`1.18.32`（稳定版）
- ✅ 添加 H2 测试依赖

### 2. 数据库初始化问题（核心修复）

**错误现象**：
```
Error creating bean with name 'databaseInitConfig'
Caused by: org.postgresql.util.PSQLException: 错误: 字段 "follower_id" 不存在
```

**根本原因**：
- `DatabaseInitConfig.java` 中的表结构与实体类不匹配
- `follow` 表：实体用 `userId`/`shopId`，但初始化 SQL 用了 `follower_id`/`followee_id`
- 索引语句未加引号导致保留字冲突
- 缺少方法闭合括号 `}`

**修复内容**：
- 修正 `follow` 表字段为 `user_id`, `shop_id`
- 修正 `user_follow` 表索引字段为 `following_id`（不是 `followee_id`）
- 修正所有表名和字段名为双引号包裹（如 `"order"`, `"user"`）
- 补充缺失的 `createIndexes()` 方法闭合括号
- 移除非法 `ON CONFLICT DO NOTHING`（未指定冲突列）

---

## 当前状态

✅ **编译通过**：`mvn clean compile` → BUILD SUCCESS  
⚠️ **数据库需要重置**：之前的失败导致表结构不完整，需清空后重新初始化

---

## 启动步骤

### 方案一：一键重置并启动（推荐）

以**管理员身份**运行以下脚本：

```cmd
cd D:\market\script\build
reset-and-start.bat
```

脚本会自动：
1. 请求管理员权限
2. 清空 `market` 数据库（`DROP SCHEMA public CASCADE`）
3. 启动 Spring Boot 应用
4. `DatabaseInitConfig` 自动创建 20+ 张表和索引
5. 插入初始数据（管理员、商品、VIP等级、礼包等）

**预期输出**（末尾）：
```
Started MarketApplication in XXX.XXX seconds
```
表示启动成功。

按 `Ctrl+C` 停止服务器。

---

### 方案二：手动操作

#### 步骤 1：清空数据库

```cmd
"C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -d market -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
```

#### 步骤 2：启动后端

```cmd
cd D:\market\backend
mvn spring-boot:run
```

---

## 验证启动成功

### 方法 1：控制台日志
看到以下信息表明成功：
```
Started MarketApplication in 27.xxx seconds
```

### 方法 2：HTTP 请求
```cmd
curl http://localhost:8080/actuator/health
```
预期：
```json
{"status":"UP"}
```

### 方法 3：查看数据库表
```cmd
psql -U market -d market -c "\dt"
```
应看到 20+ 张表（`user`, `product`, `order`, `follow`, `user_follow`, `vip_level` 等）

### 方法 4：查看初始数据
```cmd
psql -U market -d market -c "SELECT id, name, role FROM \"user\";"
```
预期输出：
```
 id |  name  | role
----+--------+-------
  1 | admin  | ADMIN
```

---

## 测试账号

| 角色   | 用户名   | 密码   | 说明     |
|--------|----------|--------|----------|
| 管理员 | admin    | 123456 | 全部权限 |
| 商家   | merchant1| 123456 | 商品管理 |
| 用户   | user1    | 123456 | 普通用户 |

*这些账号由 `DatabaseInitConfig` 自动插入。*

---

## 故障排除

### 问题 1：`psql: 致命：密码验证失败`
**原因**：pg_hba.conf 配置为 md5，但 postgres 密码未知。

**解决**：
1. 修改 `C:\Program Files\PostgreSQL\10\data\pg_hba.conf`
2. 将 `md5` 改为 `trust`
3. 重启 PostgreSQL：
   ```cmd
   net stop postgresql-x64-10
   net start postgresql-x64-10
   ```
4. 再次连接（无需密码），执行初始化
5. **恢复** `pg_hba.conf` 为 `md5` 并重启

### 问题 2：`端口 8080 被占用`
```cmd
netstat -ano | findstr :8080
```
结束占用进程或修改 `application.properties` 中的 `server.port`。

### 问题 3：`relation "xxx" already exists`
说明之前部分初始化残留。执行**方案一**的 reset 脚本清空数据库。

### 问题 4：`could not connect to server`
```cmd
net start postgresql-x64-10
```
确保 PostgreSQL 10 服务正在运行（端口 5432）。

---

## 项目结构（相关文件）

```
backend/
  src/main/resources/
    application.properties          # 默认配置（dev profile）
    application-dev.properties      # 开发环境（market/market）
    application-postgresql.properties  # 生产环境（admin/123456）
    application-prod.properties     # 生产环境（环境变量）
  src/main/java/com/market/config/
    DatabaseInitConfig.java         # 数据库初始化（表+索引+数据）
    RedisConfig.java                # Redis 配置（可选）
    SecurityConfig.java             # 安全配置
  src/main/java/com/market/entity/  # JPA 实体（26+ 个）
script/build/
  reset-and-start.bat              # 一键重置并启动 ⭐
  clean-market-db.sql              # 数据库清理 SQL
  start-backend-local.bat          # 直接启动（不清理）
  test-postgresql.ps1              # 连接诊断
LOCAL_STARTUP.md                  # 本地启动完整文档
POSTGRESQL_SETUP.md               # 数据库安装配置文档
```

---

## 核心改动摘要

| 文件 | 修改内容 |
|------|----------|
| `application.properties` | 默认 profile = dev |
| `application-dev.properties` | 凭据 = market/market |
| `pom.xml` | Lombok 1.18.32，添加 H2 test 依赖 |
| `DatabaseInitConfig.java` | 修复表结构、索引、SQL 语法 |
| `RedisConfig.java` | 保持原条件（localhost） |
| `application-h2.properties` | 已删除 |

---

**现在请执行**：
```cmd
cd D:\market\script\build
reset-and-start.bat
```
（需管理员权限）

启动后访问：http://localhost:8080/actuator/health
