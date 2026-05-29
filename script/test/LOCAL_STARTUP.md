# 后端启动完整指南

## 当前状态

✅ **编译通过**：`mvn clean compile` → BUILD SUCCESS

✅ **配置文件**：
- `application.properties`：默认 profile = dev
- `application-dev.properties`：`market/market` 凭据
- DatabaseInitConfig：仅在 dev 运行，自动建表

⚠️ **本地 PostgreSQL**：已安装 PostgreSQL 10（端口 5432）和 18（端口 5433），但 `market` 用户/数据库可能不存在

---

## 快速启动方案

### 方案 1：本地 PostgreSQL + 直接启动（推荐）

#### 步骤 1：确保 PostgreSQL 10 在运行（端口 5432）

```cmd
net start postgresql-x64-10
```

验证服务：
```cmd
sc query postgresql-x64-10
```

#### 步骤 2：创建 market 用户和数据库

**选项 A：使用 pgAdmin 4（图形化，最简单）**

1. 打开 pgAdmin 4（开始菜单）
2. 连接到 "PostgreSQL 10"
3. 打开 Query Tool（查询工具）
4. 执行：
```sql
CREATE USER market WITH PASSWORD 'market' CREATEDB;
CREATE DATABASE market OWNER market;
GRANT ALL PRIVILEGES ON DATABASE market TO market;
```

**选项 B：使用命令行（可能需要 postgres 密码）**

如果 `psql` 可以连接：
```cmd
set PGPASSWORD=你的postgres密码
"C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -c "CREATE USER market WITH PASSWORD 'market' CREATEDB;"
"C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -c "CREATE DATABASE market OWNER market;"
```

**选项 C：自动脚本（需管理员权限 + pg_hba.conf 配置为 trust）**

见 `script\build\init-pg10-trust.bat`

#### 步骤 3：验证连接

```cmd
set PGPASSWORD=market
"C:\Program Files\PostgreSQL\10\bin\psql.exe" -U market -d market -c "SELECT 'OK' AS status;"
```

预期输出包含 `OK`。

#### 步骤 4：启动后端

```cmd
cd D:\market\backend
mvn spring-boot:run
```

首次启动会下载依赖（3-5 分钟）。日志末尾应有：
```
Started MarketApplication in XXX seconds
```

#### 步骤 5：测试 API

```cmd
curl http://localhost:8080/actuator/health
```

预期输出：
```json
{"status":"UP"}
```

---

## 故障排除

### 问题 1：`psql: 致命：密码验证失败`

**原因**：pg_hba.conf 要求密码，但 postgres 用户密码未知或错误

**解决**：
1. 打开 `C:\Program Files\PostgreSQL\10\data\pg_hba.conf`
2. 找到 `local   all             all                                     md5` 行
3. 将 `md5` 改为 `trust`
4. 保存并重启 PostgreSQL：
   ```cmd
   net stop postgresql-x64-10
   net start postgresql-x64-10
   ```
5. 现在可以无密码连接：`psql -U postgres`
6. 创建 market 用户和数据库
7. **重要**：将 pg_hba.conf 改回 `md5` 并重启，恢复安全

### 问题 2：`FATAL: 数据库 "market" 不存在`

**解决**：创建数据库（见步骤 2）

### 问题 3：端口 8080 被占用

**解决**：
```cmd
netstat -ano | findstr :8080
```
记录 PID，然后在任务管理器结束该进程，或修改 `application.properties` 中的 `server.port`。

### 问题 4：Maven 依赖下载慢/失败

**解决**：配置国内镜像源（~/.m2/settings.xml）：
```xml
<mirror>
    <id>aliyunmaven</id>
    <mirrorOf>*</mirrorOf>
    <name>阿里云公共仓库</name>
    <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

---

## 配置文件说明

### 开发环境（默认）
- 文件：`application-dev.properties`
- 数据库：PostgreSQL 5432
- 用户/密：`market/market`
- JWT Secret：`market-platform-secret-key-2024-secure-enough`
- Redis：可选（localhost:6379）
- RabbitMQ：禁用（默认）

### 生产环境
- 文件：`application-prod.properties`
- 所有敏感信息通过环境变量注入
- DDL 策略：`validate`（不自动修改表）
- 监控：Actuator 端点暴露

---

## 下一步

启动成功后，可以：
1. 访问 API 文档（如有 Swagger）
2. 使用测试账号登录
3. 查看日志文件：`D:\market\log\market.*.log`

---

## 文件索引

| 文件 | 用途 |
|------|------|
| `POSTGRESQL_SETUP.md` | 完整数据库安装配置文档 |
| `QUICKSTART.md` | 快速启动指南 |
| `script/build/start-all.bat` | 完整启动脚本（检查+启动） |
| `script/build/init-db.bat` | 仅初始化数据库 |
| `script/build/init-pg10-trust.bat` | 自动修复并初始化（管理员） |
| `script/build/test-postgresql.ps1` | 连接诊断脚本 |