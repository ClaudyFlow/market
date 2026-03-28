# 市场平台 - 快速启动指南

## 📋 项目概述

市场平台是一个全栈电商系统，包含三个端口：
- **用户端** (User) - 普通用户购物平台
- **商家端** (Merchant) - 商家管理后台
- **管理端** (Admin) - 平台运营管理后台

## 🏗️ 技术架构

| 层级 | 技术栈 |
|------|--------|
| **前端** | Vue 3 + TypeScript + Element Plus + Pinia + Vue Router |
| **后端** | Spring Boot 3.4.0 + Spring Security + Spring Data JPA |
| **数据库** | PostgreSQL |
| **缓存** | Redis (可选) |
| **认证** | JWT Token |

## 📦 文件统计

| 项目 | 文件数 | 说明 |
|------|--------|------|
| **Frontend** | 143 | 93 个 Vue 组件 + 43 个 TS 文件 + 4 个 CSS + 3 个 HTML |
| **Backend** | 161 | 156 个 Java 文件 + 4 个 properties + 1 个 pom.xml |
| **总计** | **304** | 正经代码文件 |

## 🚀 快速启动

### 环境要求

- JDK 21+
- PostgreSQL 14+
- Redis 6+ (可选)
- Maven 3.6+
- Node.js 18+
- Git 2.20.0+

### 1. 数据库配置

#### 1.1 创建 PostgreSQL 数据库

```sql
CREATE DATABASE market;
CREATE USER admin WITH PASSWORD '123456';
GRANT ALL PRIVILEGES ON DATABASE market TO admin;
```

#### 1.2 修改数据库配置

编辑 `backend/src/main/resources/application-dev.properties`:

```properties
# 数据库配置
spring.datasource.url=jdbc:postgresql://localhost:5432/market
spring.datasource.username=admin
spring.datasource.password=123456
```

### 2. 后端启动

#### 2.1 编译后端

```bash
cd D:\Code\Java_Code\market
mvn clean compile
```

#### 2.2 运行后端

```bash
# 方式 1: 使用 Maven
mvn spring-boot:run

# 方式 2: 打包后运行
mvn clean package
java -jar target/market-platform-1.0.0.jar
```

后端服务运行在：`http://localhost:8080`

#### 2.3 验证后端

访问：`http://localhost:8080/api/home`

### 3. 前端启动

#### 3.1 安装依赖

```bash
cd D:\Code\Java_Code\market\frontend
npm install
```

#### 3.2 启动开发服务器

```bash
npm run dev
```

前端访问地址：
- **用户端**: `http://localhost:5173`
- **商家端**: `http://localhost:5173/merchant`
- **管理端**: `http://localhost:5173/admin`

#### 3.3 构建生产版本

```bash
npm run build
```

构建输出目录：`nginx/html/`

## 📁 项目结构

```
market/
├── backend/                    # Java 后端
│   └── src/main/java/com/market/
│       ├── config/             # 配置类 (安全、JWT、数据库初始化)
│       ├── controller/         # REST API 控制器 (31 个)
│       ├── dto/                # 数据传输对象
│       ├── entity/             # JPA 实体类 (31 个)
│       ├── exception/          # 异常处理
│       ├── interceptor/        # 拦截器
│       ├── repository/         # 数据访问层 (27 个)
│       ├── security/           # JWT 认证
│       ├── service/            # 业务逻辑层 (26 个接口 + 实现)
│       └── task/               # 定时任务
│
├── frontend/                   # Vue 前端
│   ├── src/
│   │   ├── user/               # 用户端 (约 40+ 文件)
│   │   │   ├── api/            # API 接口
│   │   │   ├── components/     # 组件
│   │   │   ├── router/         # 路由
│   │   │   ├── stores/         # 状态管理
│   │   │   ├── views/          # 页面
│   │   │   └── util/           # 工具函数
│   │   ├── admin/              # 管理端 (约 30+ 文件)
│   │   ├── merchant/           # 商家端 (约 25+ 文件)
│   │   └── common/             # 公共模块 (约 10+ 文件)
│   ├── index.html              # 用户端入口
│   ├── admin.html              # 管理端入口
│   └── merchant.html           # 商家端入口
│
├── doc/                        # 项目文档
├── docker-compose.yml          # Docker 编排
├── Dockerfile                  # Docker 镜像
└── pom.xml                     # Maven 配置
```

## 🔧 配置说明

### 后端配置

#### application-dev.properties (开发环境)

```properties
# 服务器配置
server.port=8080

# 数据库配置
spring.datasource.url=jdbc:postgresql://localhost:5432/market
spring.datasource.username=admin
spring.datasource.password=123456

# JPA 配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT 配置
jwt.secret=market-platform-secret-key-2024
jwt.expiration=86400000

# 邮件配置
spring.mail.host=smtp.qq.com
spring.mail.username=your-email@qq.com
spring.mail.password=your-auth-code
```

### 前端配置

#### vite.config.ts

```typescript
export default defineConfig({
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## 🎯 核心功能模块

### 用户端功能

| 模块 | 说明 | API 数量 |
|------|------|---------|
| **商品浏览** | 首页、分类、搜索、详情 | 5 |
| **购物车** | 添加、删除、修改数量 | 5 |
| **订单管理** | 创建、支付、取消、确认收货 | 8 |
| **用户中心** | 个人信息、地址管理 | 6 |
| **收藏关注** | 商品收藏、店铺关注 | 4 |
| **积分系统** | 积分查询、签到、兑换 | 5 |
| **优惠券** | 领取、使用优惠券 | 4 |
| **抽奖** | 积分抽奖 | 2 |
| **论坛** | 发帖、评论 | 4 |
| **客服** | 在线客服聊天 | 3 |

### 商家端功能

| 模块 | 说明 | API 数量 |
|------|------|---------|
| **商品管理** | CRUD、上下架、审核 | 6 |
| **订单处理** | 发货、退款处理 | 5 |
| **优惠券** | 创建、发放优惠券 | 4 |
| **店铺管理** | 店铺信息配置 | 2 |
| **数据统计** | 销售统计、订单分析 | 3 |
| **客服** | 用户消息回复 | 2 |

### 管理端功能

| 模块 | 说明 | API 数量 |
|------|------|---------|
| **用户管理** | 用户列表、详情、封禁 | 4 |
| **商家管理** | 商家审核、店铺管理 | 4 |
| **商品管理** | 商品审核、下架 | 4 |
| **订单监控** | 订单列表、退款审核 | 5 |
| **数据统计** | 销售统计、用户分析 | 4 |
| **系统设置** | 平台配置、公告管理 | 3 |

## 📊 数据库表结构

系统包含以下核心表：

| 表名 | 说明 | 记录数 |
|------|------|--------|
| `user` | 用户表 | - |
| `product` | 商品表 | - |
| `order` | 订单表 | - |
| `order_item` | 订单项表 | - |
| `cart_item` | 购物车项表 | - |
| `favorite` | 收藏表 | - |
| `follow` | 关注表 | - |
| `review` | 评价表 | - |
| `coupon` | 优惠券表 | - |
| `user_coupon` | 用户优惠券表 | - |
| `credit_history` | 积分历史表 | - |
| `lottery_prize` | 抽奖奖品表 | - |
| `lottery_record` | 抽奖记录表 | - |
| `chat_message` | 聊天消息表 | - |
| `user_address` | 用户地址表 | - |
| `vip_level` | VIP 等级表 | 6 |
| `vip_gift` | VIP 礼包表 | 11 |
| `vip_gift_record` | VIP 礼包领取记录 | - |
| `vip_recharge_order` | VIP 充值订单表 | - |

## 🔐 默认账户

### 管理员账户

- **用户名**: `admin`
- **密码**: `admin123`
- **邮箱**: `admin@market.com`

> 注意：首次启动时会自动创建管理员账户

## 🧪 测试验证

### 1. 后端测试

```bash
# 测试用户注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"testuser","email":"test@example.com","password":"password123"}'

# 测试用户登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"name":"testuser","password":"password123"}'
```

### 2. 前端测试

1. 访问 `http://localhost:5173`
2. 点击登录/注册
3. 浏览商品
4. 添加到购物车
5. 创建订单

## 🐛 常见问题

### 1. 后端启动失败

**问题**: 数据库连接失败

**解决**:
- 检查 PostgreSQL 是否启动
- 确认数据库配置正确
- 验证用户名密码

### 2. 前端构建失败

**问题**: 依赖安装失败

**解决**:
```bash
# 清理缓存
npm cache clean --force

# 删除 node_modules
rm -rf node_modules package-lock.json

# 重新安装
npm install
```

### 3. 跨域问题

**问题**: 前端无法访问后端 API

**解决**:
- 检查 `vite.config.ts` 中的代理配置
- 确认后端 CORS 配置正确

## 📝 下一步计划

### 待完善功能

1. **支付集成**
   - 支付宝支付
   - 微信支付
   - 银联支付

2. **物流跟踪**
   - 快递鸟 API 集成
   - 物流状态实时更新

3. **商品评价**
   - 图片上传
   - 评价回复

4. **推荐系统**
   - 基于用户行为推荐
   - 热门商品排行

5. **消息推送**
   - WebSocket 实时通知
   - 邮件通知

## 📄 许可证

MIT License - 查看 [LICENSE](LICENSE) 文件

## 📧 联系方式

- **Email**: 2481036245@qq.com
- **GitHub**: https://github.com/market-platform

---

**最后更新**: 2026 年 3 月 28 日
**版本**: v1.0.0
