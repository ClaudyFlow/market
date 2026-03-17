# 市场平台 (Market Platform)

一个基于 Vue 3 和 Spring Boot 3 的现代化全栈电商平台，采用前后端分离架构，支持完整的电商购物流程。

## 📋 项目概述

市场平台是一个现代化的电商解决方案，提供商品管理、购物车、订单管理、用户认证等核心功能。系统采用前后端分离架构，支持多端访问（Web、移动端）。

## 🏗️ 系统架构

### 技术栈

| 层级 | 技术 |
|------|------|
| **前端** | Vue 3 (组合式 API) + Element Plus + Pinia + Vue Router + Axios |
| **后端** | Java 21 + Spring Boot 3.4.0 + Spring Security + Spring Data JPA |
| **数据库** | SQLite 3.45+ |
| **认证** | JWT Token |
| **构建工具** | Vite (前端) / Maven (后端) |
| **部署** | Nginx + Docker |

### 项目结构

```
market/
├── backend/              # Java 后端 (Spring Boot)
│   └── src/
│       ├── main/java/com/market/
│       │   ├── config/       # 安全配置、JWT 配置、数据库初始化
│       │   ├── controller/   # REST API 控制器
│       │   ├── dto/          # 数据传输对象
│       │   ├── entity/       # JPA 实体类
│       │   ├── repository/   # 数据访问层
│       │   ├── security/     # JWT 认证相关
│       │   └── service/      # 业务逻辑层
│       └── main/resources/   # 配置文件
├── frontend/             # 前端 (Vue 3)
│   ├── src/
│   │   ├── user/         # 用户端应用
│   │   ├── admin/        # 管理后台
│   │   ├── merchant/     # 商家后台
│   │   └── common/       # 公共模块
│   ├── index.html        # 用户端入口
│   ├── admin.html        # 管理后台入口
│   └── merchant.html     # 商家后台入口
├── doc/                  # 项目文档 (.typ 源文件)
├── script/               # 辅助脚本
├── pom.xml               # Maven 配置
└── start_backend.bat     # 后端启动脚本
```

## ✨ 功能特性

### 前端功能

#### 1. 用户端 (frontend/src/user)
- **商品浏览**：网格布局展示，响应式设计
- **商品搜索**：支持名称、描述、分类的模糊搜索
- **购物车管理**：添加商品、数量调整、移除商品、实时计算
- **订单管理**：订单创建、订单查看、状态跟踪
- **用户认证**：登录、注册、退出
- **积分系统**：积分查询、积分历史、签到打卡
- **优惠券**：优惠券领取和使用

#### 2. 管理后台 (frontend/src/admin)
- **用户管理**：用户列表、用户详情
- **商品管理**：商品 CRUD、库存管理
- **订单管理**：订单处理、状态更新
- **数据统计**：销售统计、用户分析

#### 3. 商家后台 (frontend/src/merchant)
- **商品管理**：商家专属商品管理
- **订单处理**：订单发货、退款处理
- **店铺管理**：店铺信息配置

### 后端功能

#### 核心模块

| 模块 | 说明 |
|------|------|
| **AuthController** | 用户认证（登录、注册） |
| **UserController** | 用户信息管理 |
| **ProductController** | 商品 CRUD、搜索 |
| **CartController** | 购物车操作 |
| **OrderController** | 订单创建、查询、状态管理 |
| **PointsController** | 积分管理、签到 |
| **EmailController** | 邮箱验证、验证码发送 |

#### 实体类

- `User` - 用户信息
- `Product` - 商品信息
- `Order` / `OrderItem` - 订单及订单项
- `CartItem` - 购物车项
- `Coupon` / `UserCoupon` - 优惠券
- `UserPointsInfo` / `PointsHistory` - 积分及历史记录

#### 安全特性

- JWT Token 认证
- Spring Security 权限控制
- 邮箱验证（格式验证、MX 记录验证）
- 验证码发送

## 🚀 快速开始

### 环境要求

**开发环境：**
- JDK 21+
- SQLite 3.45+
- Maven 3.6+
- Node.js 18+
- Git 2.20.0+

### 后端启动

```bash
# 方式 1：使用启动脚本 (Windows)
start_backend.bat

# 方式 2：使用 Maven
mvn spring-boot:run

# 方式 3：打包后运行
mvn clean package
java -jar target/market-platform-1.0.0.jar
```

后端服务运行在：`http://localhost:8080`

### 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端访问地址：`http://localhost:5173`

### Docker 部署

```bash
# 使用 Docker Compose 一键部署
docker-compose up -d

# 访问应用
# 前端：http://localhost:80
# 后端 API：http://localhost:8080
```

## 📚 文档

详细文档位于 `doc/` 目录：

| 文档 | 说明 |
|------|------|
| 项目简介.typ | 项目概述、架构、技术栈 |
| 前端功能介绍.typ | 前端功能模块详解 |
| 前端接口文档.typ | 组件接口规范、API 设计 |
| 部署配置指南.typ | 部署步骤、配置优化 |
| 数据库结构文档.typ | 数据库表结构、SQL 脚本 |
| 手动测试检查清单.typ | 测试用例、检查项 |

## 📦 API 接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录

### 用户接口
- `GET /api/user/profile` - 获取用户信息
- `PUT /api/user/profile` - 更新用户信息

### 商品接口
- `GET /api/product` - 获取商品列表
- `GET /api/product/{id}` - 获取商品详情
- `POST /api/product` - 创建商品
- `PUT /api/product/{id}` - 更新商品
- `DELETE /api/product/{id}` - 删除商品

### 购物车接口
- `GET /api/cart` - 获取购物车
- `POST /api/cart/items` - 添加商品到购物车
- `PUT /api/cart/items/{id}` - 更新购物车项
- `DELETE /api/cart/items/{id}` - 删除购物车项

### 订单接口
- `GET /api/order` - 获取订单列表
- `GET /api/order/{id}` - 获取订单详情
- `POST /api/order` - 创建订单
- `PUT /api/order/{id}/status` - 更新订单状态

### 积分接口
- `GET /api/credit` - 获取积分余额
- `GET /api/credit/history` - 获取积分历史
- `POST /api/credit/redeem` - 积分兑换

## 🔧 配置说明

### 后端配置

配置文件位于 `backend/src/main/resources/application.properties`（需自行创建）

```properties
# 服务器配置
server.port=8080

# 数据库配置
spring.datasource.url=jdbc:sqlite:data/market.db
spring.jpa.hibernate.ddl-auto=update

# JWT 配置
jwt.secret=your-secret-key
jwt.expiration=86400000

# 邮件配置
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your-email
spring.mail.password=your-password
```

## 📝 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0.0 | 2026-03-09 | 初始版本：基础电商功能、Vue 3 前端、Spring Boot 3.4.0 后端 |
| v1.1.0 | 规划中 | 支付集成、物流跟踪、商品评价 |
| v1.2.0 | 规划中 | 移动端 App 完善、社交分享、优惠券系统 |
| v2.0.0 | 规划中 | 微服务架构、AI 推荐、多商户支持 |

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request 来帮助改进项目！

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📧 联系方式

- 📧 Email: contact@market.com
- 🌐 Website: https://market.com
- 🐙 GitHub: https://github.com/market-platform
- 📚 Docs: https://docs.market.com

---

**最后更新：** 2026 年 3 月 14 日  
**文档版本：** v1.0.0
