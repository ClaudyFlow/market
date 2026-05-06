# 手动执行以下命令来重置并启动后端

## 步骤 1：管理员权限清理数据库

```powershell
# 设置环境变量
$env:PGPASSWORD = "market"

# 清理 public schema
& "C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -d market -c "DROP SCHEMA IF EXISTS public CASCADE; CREATE SCHEMA public;"
```

## 步骤 2：启动后端

```powershell
cd D:\market\backend
mvn spring-boot:run -DskipTests
```

## 预期输出

看到以下信息表示成功：
```
Started MarketApplication in XXX.XXX seconds
```

按 `Ctrl+C` 停止服务器。

## 验证

```powershell
# 测试健康检查
curl http://localhost:8080/actuator/health
```

预期：`{"status":"UP"}`

---

**注意**：如果 PostgreSQL 提示密码错误，需要先以 trust 模式连接。参见 `POSTGRESQL_SETUP.md`。
