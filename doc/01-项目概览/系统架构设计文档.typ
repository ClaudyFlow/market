// 系统架构设计文档 - Typst 格式

#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
)

#v(85mm)
#align(center)[#text(26pt, font: "SimHei")[购物商城系统\
  系统架构设计文档]]
#pagebreak()

#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——系统架构设计文档]]
    #place(top, dy: 30mm)[#line(length: 100%, stroke: 0.5pt)]
  ],
  footer: [
    #place(bottom, dy: -25mm)[#line(length: 100%, stroke: 0.5pt)]
    #place(bottom + left, dy: -20mm)[#text(9pt, font: "SimSun")[#datetime.today().display()]]
    #place(bottom + right, dy: -20mm)[#context text(9pt, font: "SimSun", counter(page).display())]
  ],
)

#set text(font: "SimSun", size: 10.5pt)
#set par(leading: 1.25em, first-line-indent: 2em)

= 1. 概述

#h(2.0em)购物商城系统是一个基于现代 Web 技术的全栈电商平台，采用前后端分离架构。

== 1.1 设计原则
#h(2.0em)——高内聚、低耦合\
#h(2.0em)——RESTful API 设计\
#h(2.0em)——前后端独立部署\
#h(2.0em)——安全性优先

= 2. 系统架构

== 2.1 整体架构

#h(2.0em)客户端（浏览器）通过 HTTP/HTTPS 访问 Nginx 反向代理，Nginx 将请求分发到前端静态文件或后端 API 服务。后端服务连接 PostgreSQL 数据库和 Redis 缓存。

== 2.2 技术栈

=== 前端技术
#align(center)[#table(
  columns: (3fr, 2fr, 5fr),
  stroke: 0.5pt,
  [技术], [版本], [用途],
  [Vue], [3.4.0], [核心框架],
  [TypeScript], [5.9.3], [类型安全],
  [Element Plus], [2.4.4], [UI组件库],
  [Vite], [5.x], [构建工具],
  [Pinia], [2.1.7], [状态管理],
  [Axios], [1.6.2], [HTTP客户端],
)]

=== 后端技术
#align(center)[#table(
  columns: (3fr, 2fr, 5fr),
  stroke: 0.5pt,
  [技术], [版本], [用途],
  [Spring Boot], [3.4.0], [核心框架],
  [Java], [21 LTS], [编程语言],
  [Spring Security], [6.x], [安全框架],
  [Spring Data JPA], [3.x], [ORM框架],
  [PostgreSQL JDBC], [42.7.x], [数据库驱动],
  [Redis], [7.x], [缓存服务],
  [JWT (jjwt)], [0.12.5], [Token认证],
)]

= 3. 模块设计

== 3.1 前端模块
#h(2.0em)——用户端: 26个页面\
#h(2.0em)——商家端: 10个页面\
#h(2.0em)——管理端: 9个页面\
#h(2.0em)——通用组件: 50+ 组件\
#h(2.0em)——API层: 21个模块，212+ 接口\
#h(2.0em)——状态管理: 8 Stores (Pinia)\
#h(2.0em)——类型定义: 15 TypeScript 类型

== 3.2 后端模块
#h(2.0em)——认证模块: AuthController, JWT, Security\
#h(2.0em)——商品模块: ProductController, Category, Brand\
#h(2.0em)——订单模块: OrderController, OrderService\
#h(2.0em)——购物车模块: CartController, CartService\
#h(2.0em)——用户模块: UserController, Address, Profile\
#h(2.0em)——优惠券模块: CouponController\
#h(2.0em)——通知模块: UserNotificationController\
#h(2.0em)——论坛模块: ForumController, Post, Comment\
#h(2.0em)——客服模块: ChatController, WebSocket\
#h(2.0em)——支付模块: PaymentController, Refund\
#h(2.0em)——物流模块: LogisticsController\
#h(2.0em)——搜索模块: SearchController\
#h(2.0em)——推荐模块: RecommendationController\
#h(2.0em)——抽奖模块: LotteryController\
#h(2.0em)——VIP模块: VipLevelController\
#h(2.0em)——统计模块: StatisticsController\
#h(2.0em)——上传模块: UploadController

= 4. 安全架构

== 4.1 认证流程
#h(2.0em)用户登录 → 验证身份 → 生成JWT → 返回Token → 后续请求携带Token → 验证Token

== 4.2 权限控制
#h(2.0em)——公开接口: /api/auth/**, /api/product/**\
#h(2.0em)——认证接口: 需要 JWT Token\
#h(2.0em)——管理员接口: /api/admin/** 需要 ADMIN 角色

#v(2cm)
#align(center)[#text(10pt, font: "SimSun")[文档版本：v1.1.0 | 最后更新：#datetime.today().display()]]
