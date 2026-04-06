# PostgreSQL 数据库设置指南

## 📊 数据库配置信息

- **主机**: localhost
- **端口**: 5432
- **数据库**: market
- **用户名**: market
- **密码**: market

## ✅ PostgreSQL 状态

PostgreSQL 18 已成功启动并运行在端口 5432。

## 🔧 手动设置数据库步骤

### 步骤 1: 打开 psql 命令行工具

在命令行中执行：
```cmd
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres
```

系统会提示输入 postgres 用户的密码。请输入你安装 PostgreSQL 时设置的密码。

### 步骤 2: 创建用户和数据库

在 psql 提示符下，依次执行以下 SQL 命令：

```sql
-- 1. 创建 market 用户
CREATE ROLE market WITH LOGIN PASSWORD 'market';

-- 2. 创建 market 数据库
CREATE DATABASE market OWNER market;

-- 3. 授予数据库权限
GRANT ALL PRIVILEGES ON DATABASE market TO market;

-- 4. 连接到 market 数据库
\c market

-- 5. 授予 schema 权限
GRANT ALL ON SCHEMA public TO market;

-- 6. 退出 psql
\q
```

### 步骤 3: 执行初始化 SQL 文件

设置完用户和数据库后，执行项目的初始化 SQL：

```cmd
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U market -d market -f "D:\Code\Project\market\script\init.sql"
```

这会创建以下表：
- `payment` - 支付记录表
- `payment_refund` - 退款记录表
- `logistics_info` - 物流信息表
- `logistics_track` - 物流轨迹表

### 步骤 4: 启动后端应用

```cmd
cd D:\Code\Project\market\backend
mvnw spring-boot:run
```

应用启动时会自动创建所有必需的表（通过 `DatabaseInitConfig.java`），包括：
- `user` - 用户表
- `product` - 商品表
- `order` - 订单表
- `order_item` - 订单项表
- `cart_item` - 购物车项表
- `favorite` - 收藏夹表
- `follow` - 关注表
- `review` - 评价表
- `coupon` - 优惠券表
- `user_coupon` - 用户优惠券表
- `credit_history` - 积分历史表
- `lottery_prize` - 抽奖奖品表
- `lottery_record` - 抽奖记录表
- `chat_message` - 聊天消息表
- `user_address` - 地址表
- `announcement` - 公告表
- `vip_level` - VIP 等级表
- `vip_gift` - VIP 礼包表
- `vip_gift_record` - VIP 礼包领取记录表
- `vip_recharge_order` - VIP 充值订单表

## 🚀 快速一键脚本

如果你想自动化整个过程，可以执行以下脚本：

```cmd
D:\Code\Project\market\script\setup-database.bat
```

该脚本会引导你完成所有设置步骤。

## 📝 验证数据库连接

设置完成后，可以测试连接：

```cmd
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U market -d market -c "SELECT current_database(), current_user;"
```

应该显示：
```
 current_database | current_user 
------------------+--------------
 market           | market
(1 row)
```

## 🔍 查看表结构

```cmd
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U market -d market -c "\dt"
```

## 🛑 停止 PostgreSQL

```cmd
"C:\Program Files\PostgreSQL\18\bin\pg_ctl.exe" stop -D "C:\Program Files\PostgreSQL\18\data"
```

## 📚 相关配置文件

- **数据库配置**: `backend/src/main/resources/application-postgresql.properties`
- **主配置**: `backend/src/main/resources/application.properties`（已激活 postgresql profile）
- **初始化 SQL**: `script/init.sql`
- **Java 自动建表**: `backend/src/main/java/com/market/config/DatabaseInitConfig.java`

## ⚠️ 注意事项

1. 确保 PostgreSQL 服务正在运行
2. 应用配置使用 `spring.jpa.hibernate.ddl-auto=update`，会自动创建/更新表结构
3. `DatabaseInitConfig.java` 只在 `dev` profile 下执行
4. 所有 SQL 语句都使用 `IF NOT EXISTS` 确保可重复执行

## 🆘 常见问题

### Q: 忘记 postgres 密码怎么办？
A: 可以编辑 `C:\Program Files\PostgreSQL\18\data\pg_hba.conf`，将 `scram-sha-256` 改为 `trust`，重启 PostgreSQL 后无需密码登录，然后再改回来。

### Q: 端口 5432 被占用怎么办？
A: 使用 `netstat -ano | findstr :5432` 查找占用进程，或修改 PostgreSQL 端口配置。

### Q: 如何查看 PostgreSQL 是否在运行？
A: 执行 `"C:\Program Files\PostgreSQL\18\bin\pg_ctl.exe" status -D "C:\Program Files\PostgreSQL\18\data"`
