# 后端启动问题修复指南

## 问题诊断

### 已确认的环境状态:
- ✅ PostgreSQL 已安装（版本 10 和 18）
- ✅ PostgreSQL 服务正在运行
- ✅ 端口 5432 正在监听
- ❌ 命令行工具无法连接（认证配置问题）

## 解决方案

### 方案一：使用 Docker Compose（推荐⭐）

**优点**：一键启动，无需配置数据库，完全隔离环境

1. 启动 Docker Desktop（如果未运行）

2. 执行启动脚本：
   ```cmd
   cd D:\market\script\build
   start-docker.bat
   ```
   或手动执行：
   ```cmd
   cd D:\market\depend
   docker-compose up -d
   ```

3. 等待所有服务启动（约 30-60 秒）

4. 验证服务状态：
   ```cmd
   docker-compose ps
   ```

5. 启动后端（**注意：需要使用 Docker 网络配置**）:
   ```cmd
   cd D:\market\backend
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```
   或修改本地配置使用 Docker 网络：
   ```properties
   # backend/src/main/resources/application-docker.properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/market
   spring.datasource.username=admin
   spring.datasource.password=123456
   ```

6. 访问应用：http://localhost:8080

**注意**：Docker Compose 已在 `depend/docker-compose.yml` 中定义：
- PostgreSQL: `admin/123456`, 端口 `5432`
- Redis: 密码 `123456`, 端口 `6379`
- RabbitMQ: `guest/guest`, 管理界面 `15672`
- 后端: 端口 `8080`

---

### 方案二：修复本地 PostgreSQL 连接

如果不想使用 Docker，需要修复本地 PostgreSQL 的认证配置。

#### 步骤 1: 修改 pg_hba.conf

1. 找到配置文件：
   ```
   C:\Program Files\PostgreSQL\18\data\pg_hba.conf
   （如果是 10 版本，路径中的 18 改为 10）
   ```

2. 用记事本以**管理员身份**打开 `pg_hba.conf`

3. 找到以下行（通常在文件末尾附近）：
   ```
   # IPv4 local connections:
   host    all             all             127.0.0.1/32            md5
   # IPv6 local connections:
   host    all             all             ::1/128                 md5
   ```

4. 将 `md5` 改为 `trust`：
   ```
   host    all             all             127.0.0.1/32            trust
   host    all             all             ::1/128                 trust
   ```

5. 保存文件

#### 步骤 2: 重启 PostgreSQL 服务

```cmd
net stop postgresql-x64-18
net start postgresql-x64-18
```
（如果使用 10 版本，将 `18` 改为 `10`）

#### 步骤 3: 运行初始化脚本

```cmd
cd D:\market\script\build
init-postgresql-local.bat
```

或手动执行：
```cmd
cd D:\market
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d postgres -f script\build\init-postgresql.sql
```

#### 步骤 4: 恢复安全配置（重要！）

初始化完成后，**务必**将 `pg_hba.conf` 中的 `trust` 改回 `md5`，然后重启 PostgreSQL：

```cmd
net stop postgresql-x64-18
net start postgresql-x64-18
```

#### 步骤 5: 验证连接

```cmd
set PGPASSWORD=market
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U market -d market -c "SELECT '连接成功' AS status;"
```

#### 步骤 6: 启动后端

```cmd
cd backend
mvn spring-boot:run
```

---

### 方案三：使用 pgAdmin 图形化工具

1. 下载安装 pgAdmin 4（随 PostgreSQL 安装包一起安装或单独下载）

2. 连接本地 PostgreSQL（默认无密码或使用安装时设置的密码）

3. 执行以下 SQL：
   ```sql
   CREATE USER market WITH PASSWORD 'market' CREATEDB;
   CREATE DATABASE market OWNER market;
   GRANT ALL PRIVILEGES ON DATABASE market TO market;
   ```

4. 验证后端配置文件：
   ```properties
   # backend/src/main/resources/application-dev.properties
   spring.datasource.username=market
   spring.datasource.password=market
   ```

---

## 常见错误与解决

### 错误 1: `password authentication failed for user "market"`

**原因**：用户不存在或密码错误

**解决**：
1. 以 postgres 用户登录：`psql -U postgres`
2. 检查用户：`\du`
3. 重置密码：`ALTER USER market WITH PASSWORD 'market';`

### 错误 2: `database "market" does not exist`

**原因**：数据库未创建

**解决**：
```sql
CREATE DATABASE market OWNER market;
```

### 错误 3: `could not connect to server: Connection refused`

**原因**：PostgreSQL 未运行或端口被占用

**解决**：
```cmd
net start postgresql-x64-18
# 或检查端口
netstat -ano | findstr :5432
```

### 错误 4: `FATAL: no pg_hba.conf entry for host`

**原因**：pg_hba.conf 未允许本地连接

**解决**：参考**方案二**修改配置

---

## 快速验证

运行验证脚本检查一切正常：

```cmd
cd D:\market\script\build
diagnose-postgresql.bat
```

---

## 生产环境配置

生产环境应使用 **application-prod.properties** 配置，通过环境变量注入敏感信息：

```yaml
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/market
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=your_secure_password
JWT_SECRET=your_jwt_secret_here
MAIL_USERNAME=your_email@qq.com
MAIL_PASSWORD=your_app_password
```

或使用 Docker Compose 的生产配置（已包含在 docker-compose.yml 中）。

---

## 需要帮助？

- 查看日志：`D:\market\log\` 目录
- 后端日志：`market.*.log`
- 错误日志：`error.*.log`

遇到问题请提供完整的错误信息以便进一步诊断。
