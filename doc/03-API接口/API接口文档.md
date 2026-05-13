# 市场平台 - API 接口文档

## 📋 接口概览

| 分类 | 接口数量 | 说明 |
|------|---------|------|
| **认证** | 4 | 登录、注册、商家注册、获取当前用户 |
| **用户** | 15+ | 个人信息、地址、积分、VIP、签到、浏览历史、通知 |
| **账户** | 10+ | 用户资料、关注/粉丝、收藏、评价、密码修改 |
| **商品** | 15+ | 浏览、搜索、详情、推荐、分类/品牌、CRUD、审核 |
| **购物车** | 10 | 增删改查、选中/全选、库存检查、合并 |
| **订单** | 13 | 创建、列表、详情、取消、支付、退款、评价、物流 |
| **支付** | 6 | 支付单、回调、状态、退款、模拟支付 |
| **物流** | 5 | 快递公司、订单物流、轨迹查询、模拟物流 |
| **优惠券** | 12 | 领取、使用、批量、商家优惠券、模板管理 |
| **店铺** | 14 | 列表、详情、搜索、创建、更新、关注、认证 |
| **收藏关注** | 10 | 商品收藏、店铺关注、CRUD、计数 |
| **评价** | 6 | 发布、列表、详情、评分统计、点赞 |
| **客服聊天** | 6 | WebSocket 消息、历史记录、未读消息 |
| **系统消息** | 4 | 消息列表、未读、已读标记、发送 |
| **积分** | 3 | 积分查询、历史、兑换 |
| **论坛** | 12 | 帖子 CRUD、评论、点赞、热门、精华、搜索 |
| **搜索** | 6 | 综合搜索、商品/帖子/用户搜索、热词、建议 |
| **推荐** | 5 | 猜你喜欢、热门、看了又看、买了又买、店铺推荐 |
| **抽奖** | 3 | 抽奖、记录、奖品列表 |
| **VIP** | 9 | 等级、权益、礼包、领取、充值、记录 |
| **统计** | 6 | 平台统计、订单、商品、用户、论坛、趋势 |
| **上传** | 4 | 图片上传、批量上传、商品图片、删除 |
| **商家** | 20+ | 商品、订单、店铺、优惠券、评价管理 |
| **管理** | 25+ | 用户、商家、商品、订单、优惠券管理 |
| **健康/测试** | 6 | 健康检查、API 可用性测试、注解示例 |
| **总计** | **~200+** | RESTful API + WebSocket |

## 🔐 认证接口

### 1. 用户注册

```http
POST /api/auth/register
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "phone": "13800138000"
}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "name": "testuser",
      "email": "test@example.com"
    }
  },
  "message": "注册成功"
}
```

### 2. 用户登录

```http
POST /api/auth/login
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "testuser",
  "password": "password123"
}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "name": "testuser",
      "avatar": "https://..."
    }
  }
}
```

## 👤 用户接口

### 1. 获取用户信息

```http
GET /api/user/profile
Authorization: Bearer {token}
```

### 2. 更新用户信息

```http
PUT /api/user/profile
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "newname",
  "avatar": "https://...",
  "phone": "13800138000"
}
```

### 3. 修改密码

```http
PUT /api/user/password
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "oldPassword": "oldpass123",
  "newPassword": "newpass123"
}
```

### 4. 获取积分余额

```http
GET /api/user/credit
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "credit": 1000,
    "totalCredit": 5000,
    "consumedCredit": 4000
  }
}
```

### 5. 获取积分历史

```http
GET /api/user/credit/history?page=1&size=10
Authorization: Bearer {token}
```

### 6. 签到打卡

```http
POST /api/user/checkin
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "credit": 10,
    "consecutiveDays": 7,
    "result": "SUCCESS"
  }
}
```

### 7. 获取收货地址列表

```http
GET /api/user/address
Authorization: Bearer {token}
```

### 8. 添加收货地址

```http
POST /api/user/address
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "张三",
  "phone": "13800138000",
  "province": "北京市",
  "city": "北京市",
  "district": "朝阳区",
  "detail": "xxx 小区 1 号楼 101 室",
  "isDefault": true
}
```

## 🛍️ 商品接口

### 1. 获取商品列表

```http
GET /api/product?page=1&size=10&category=数码
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "name": "无线蓝牙耳机",
        "price": 199.00,
        "image": "https://...",
        "sales": 1000
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

### 2. 获取商品详情

```http
GET /api/product/{id}
```

### 3. 搜索商品

```http
GET /api/product/search?keyword=耳机&page=1&size=10
```

### 4. 获取分类列表

```http
GET /api/product/categories
```

**响应**:
```json
{
  "code": 200,
  "data": ["数码电子", "服装鞋帽", "家居生活", ...]
}
```

## 🛒 购物车接口

### 1. 获取购物车

```http
GET /api/cart
Authorization: Bearer {token}
```

### 2. 添加到购物车

```http
POST /api/cart/add
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "productId": 1,
  "quantity": 2
}
```

### 3. 更新购物车数量

```http
PUT /api/cart/update/{id}
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "quantity": 5
}
```

### 4. 删除购物车项

```http
DELETE /api/cart/remove/{id}
Authorization: Bearer {token}
```

### 5. 清空购物车

```http
DELETE /api/cart/clear
Authorization: Bearer {token}
```

### 6. 选中/取消选中购物车项

```http
PUT /api/cart/select/{id}
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "selected": true
}
```

### 7. 获取购物车数量统计

```http
GET /api/cart/total
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "totalQuantity": 5,
    "totalAmount": 999.00
  }
}
```

## 📦 订单接口

### 1. 创建订单

```http
POST /api/user/order
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "items": [
    {"productId": 1, "quantity": 2}
  ],
  "addressId": 1,
  "couponId": null
}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "orderNo": "202603281234567890",
    "totalAmount": 398.00,
    "status": "PENDING"
  }
}
```

### 2. 获取订单列表

```http
GET /api/user/order/list?page=1&size=10&status=PENDING
Authorization: Bearer {token}
```

### 3. 获取订单详情

```http
GET /api/user/order/{id}
Authorization: Bearer {token}
```

### 4. 取消订单

```http
POST /api/user/order/{id}/cancel
Authorization: Bearer {token}
```

### 5. 支付订单

```http
POST /api/user/order/{id}/pay?paymentMethod=ALIPAY
Authorization: Bearer {token}
```

### 6. 确认收货

```http
POST /api/user/order/{id}/confirm
Authorization: Bearer {token}
```

### 7. 删除订单

```http
DELETE /api/user/order/{id}
Authorization: Bearer {token}
```

### 8. 申请退款

```http
POST /api/user/order/{id}/refund
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "reason": "商品质量问题",
  "images": ["url1", "url2"]
}
```

## 🎫 优惠券接口

### 1. 获取可领取优惠券列表

```http
GET /api/coupon/available
```

### 2. 领取优惠券

```http
POST /api/coupon/{id}/claim
Authorization: Bearer {token}
```

### 3. 获取我的优惠券

```http
GET /api/user/coupon?status=UNUSED
Authorization: Bearer {token}
```

### 4. 使用优惠券

```http
POST /api/user/coupon/{id}/use
Authorization: Bearer {token}
```

## ❤️ 收藏关注接口

### 1. 获取收藏列表

```http
GET /api/user/favorite?page=1&size=10
Authorization: Bearer {token}
```

### 2. 添加收藏

```http
POST /api/user/favorite
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "productId": 1
}
```

### 3. 取消收藏

```http
DELETE /api/user/favorite/{id}
Authorization: Bearer {token}
```

### 4. 获取关注列表

```http
GET /api/user/follow?page=1&size=10
Authorization: Bearer {token}
```

## 📝 评价接口

### 1. 发布评价

```http
POST /api/review
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "productId": 1,
  "orderId": 1,
  "score": 5,
  "content": "商品很好，物流也快",
  "images": ["url1", "url2"]
}
```

### 2. 获取商品评价列表

```http
GET /api/review/product/{id}?page=1&size=10
```

### 3. 回复评价

```http
POST /api/review/{id}/reply
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "reply": "感谢亲的好评！"
}
```

## 💬 客服接口

### 1. 发送消息

```http
POST /api/chat/message
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "receiverId": 2,
  "content": "你好，请问这个商品有货吗？"
}
```

### 2. 获取聊天记录

```http
GET /api/chat/messages?userId=2&page=1&size=20
Authorization: Bearer {token}
```

## 🎁 抽奖接口

### 1. 抽奖

```http
POST /api/lottery/draw
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "prizeName": "三等奖：100 元优惠券",
    "prizeType": "coupon",
    "value": 100.00
  }
}
```

### 2. 获取抽奖记录

```http
GET /api/lottery/records?page=1&size=10
Authorization: Bearer {token}
```

## 🗣️ 论坛接口

### 1. 获取帖子列表

```http
GET /api/forum/posts?page=1&size=10
```

### 2. 发布帖子

```http
POST /api/forum/post
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "title": "这个商品怎么样？",
  "content": "有用过的吗？来说说感受"
}
```

### 3. 获取帖子详情

```http
GET /api/forum/post/{id}
```

### 4. 发布评论

```http
POST /api/forum/post/{id}/comment
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "content": "我也想知道"
}
```

## 🏪 商家接口

### 商品管理

```http
# 获取商品列表
GET /api/merchant/product?page=1&size=10

# 获取商品详情
GET /api/merchant/product/{id}

# 创建商品
POST /api/merchant/product

# 更新商品
PUT /api/merchant/product/{id}

# 删除商品
DELETE /api/merchant/product/{id}

# 上架/下架
PUT /api/merchant/product/{id}/status?status=1
```

### 订单管理

```http
# 获取订单列表
GET /api/merchant/order?page=1&size=10&status=PAID

# 获取订单详情
GET /api/merchant/order/{id}

# 发货
POST /api/merchant/order/{id}/ship?trackingNo=123456&carrier=顺丰速运

# 处理退款
POST /api/merchant/order/{id}/refund?approved=true
```

### 优惠券管理

```http
# 获取优惠券列表
GET /api/merchant/coupon?page=1&size=10

# 创建优惠券
POST /api/merchant/coupon

# 更新优惠券
PUT /api/merchant/coupon/{id}

# 删除优惠券
DELETE /api/merchant/coupon/{id}
```

### 数据统计

```http
# 获取销售统计
GET /api/merchant/stats/sales

# 获取订单统计
GET /api/merchant/stats/order

# 获取商品统计
GET /api/merchant/stats/product
```

## 🏠 首页接口

### 1. 获取轮播图

```http
GET /api/home/banners
```

**响应**:
```json
{
  "code": 200,
  "data": [
    {"id": 1, "imageUrl": "...", "link": "...", "title": "..."},
    ...
  ]
}
```

### 2. 获取首页分类

```http
GET /api/home/categories
```

### 3. 获取楼层商品

```http
GET /api/home/floor-products
```

### 4. 获取推荐店铺

```http
GET /api/home/recommended-shops?limit=6
```

### 5. 获取活动信息

```http
GET /api/home/activities
```

### 6. 获取秒杀活动

```http
GET /api/home/flash-sales
```

### 7. 获取品牌专区

```http
GET /api/home/brands
```

### 8. 获取新人专享

```http
GET /api/home/new-user
Authorization: Bearer {token}
```

### 9. 首页猜你喜欢

```http
GET /api/home/recommend?page=1&size=10
Authorization: Bearer {token}
```

### 10. 首页统计概览

```http
GET /api/home/stats
```

### 11. 用户首页统计

```http
GET /api/home/user/stats
Authorization: Bearer {token}
```

---

## 📊 统计接口

### 1. 首页统计概览（公开）

```http
GET /api/statistics/overview
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "totalProducts": 1234,
    "totalOrders": 5678,
    "totalUsers": 9012,
    "totalShops": 345
  }
}
```

### 2. 类目占比统计（公开）

```http
GET /api/statistics/category-distribution
```

### 3. 销售趋势（公开，支持 days 参数）

```http
GET /api/statistics/sales-trend?days=7
```

### 4. 平台统计（管理员）

```http
GET /api/statistics/platform
Authorization: Bearer {token}
```

### 5. 订单统计（管理员）

```http
GET /api/statistics/orders?startTime=2026-04-01T00:00:00&endTime=2026-04-06T23:59:59
Authorization: Bearer {token}
```

### 6. 商品统计（管理员）

```http
GET /api/statistics/products
Authorization: Bearer {token}
```

### 7. 用户统计（管理员）

```http
GET /api/statistics/users
Authorization: Bearer {token}
```

### 8. 论坛统计（管理员）

```http
GET /api/statistics/forum
Authorization: Bearer {token}
```

### 9. 销售趋势（管理员）

```http
GET /api/statistics/sales/trend
Authorization: Bearer {token}
```

---

## 🔧 管理端接口
# 获取用户列表
GET /api/admin/user?page=1&size=10

# 获取用户详情
GET /api/admin/user/{id}

# 封禁用户
POST /api/admin/user/{id}/ban

# 解封用户
POST /api/admin/user/{id}/unban
```

### 商家管理

```http
# 获取商家列表
GET /api/admin/merchant?page=1&size=10

# 审核商家
POST /api/admin/merchant/{id}/audit?approved=true

# 获取商家详情
GET /api/admin/merchant/{id}
```

### 商品管理

```http
# 获取待审核商品
GET /api/admin/product/audit?page=1&size=10

# 审核商品
POST /api/admin/product/{id}/audit?approved=true&rejectReason=...

# 下架商品
POST /api/admin/product/{id}/takeoff?reason=...
```

### 订单管理

```http
# 获取订单列表
GET /api/admin/order?page=1&size=10&status=REFUNDING

# 获取订单详情
GET /api/admin/order/{id}

# 处理退款
POST /api/admin/order/{id}/refund?approved=true

# 获取订单统计
GET /api/admin/order/stats
```

### 数据统计

```http
# 获取平台统计
GET /api/admin/stats/overview

# 获取销售趋势
GET /api/admin/stats/trend?days=30

# 获取商品排行
GET /api/admin/stats/product-rank

# 获取店铺排行
GET /api/admin/stats/shop-rank
```

## ❌ 错误响应

所有接口错误返回统一格式：

```json
{
  "code": 400,
  "message": "错误描述信息",
  "data": null
}
```

### 常见错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证/Token 过期 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 🔑 认证说明

除登录注册外，所有接口需要在 Header 中携带 JWT Token:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Token 有效期为 24 小时，过期后需要重新登录。

---

**最后更新**: 2026 年 4 月 5 日
**API 版本**: v1.1.0
**API 总数**: 200+ RESTful API + WebSocket
