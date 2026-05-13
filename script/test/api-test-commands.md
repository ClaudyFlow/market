# API 测试 cURL 命令参考

## 基础信息
- 后端地址: `http://localhost:8080`
- 认证方式: Bearer Token (JWT)

---

## 1. 用户认证

### 注册
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "testuser",
    "email": "test@example.com",
    "password": "Test123456",
    "confirmPassword": "Test123456"
  }'
```

### 登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "name": "testuser",
    "password": "Test123456"
  }'
```
响应包含 `token` 和 `user` 信息。

---

## 2. 用户信息 (需认证)

```bash
curl -X GET http://localhost:8080/api/user/info \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## 3. 商品相关 (公开接口)

### 商品列表（分页）
```bash
curl -X GET "http://localhost:8080/api/product?page=1&size=10"
```

### 商品详情
```bash
curl -X GET http://localhost:8080/api/product/1
```

### 商品搜索
```bash
curl -X GET "http://localhost:8080/api/product/search?keyword=蓝牙"
```

### 商品分类（示例）
```bash
curl -X GET "http://localhost:8080/api/product?category=数码"
```

---

## 4. 购物车 (需认证)

### 查看购物车
```bash
curl -X GET http://localhost:8080/api/cart \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

### 添加商品到购物车 (query 参数)
```bash
curl -X POST "http://localhost:8080/api/cart/add?productId=1&quantity=2" \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## 5. VIP 等级 (需认证)

```bash
curl -X GET http://localhost:8080/api/user/vip/levels \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## 6. 抽奖 (需认证)

### 获取奖品列表
```bash
curl -X GET http://localhost:8080/api/lottery/prizes \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

### 执行抽奖
```bash
curl -X POST http://localhost:8080/api/lottery/draw \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## 7. 订单 (需认证)

### 创建订单
```bash
curl -X POST http://localhost:8080/api/order \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      { "productId": 1, "quantity": 1 }
    ],
    "shippingAddress": "北京市海淀区",
    "remark": "测试订单"
  }'
```

### 订单列表
```bash
curl -X GET http://localhost:8080/api/order \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

### 订单详情
```bash
curl -X GET http://localhost:8080/api/order/1 \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## 8. 消息通知 (需认证)

```bash
curl -X GET http://localhost:8080/api/message/list \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## 9. 店铺公告 (公开)

```bash
curl -X GET http://localhost:8080/api/shop/1/announcement
```

---

## 说明

- 某些接口（如商品详情、添加购物车）可能因实体序列化问题返回 500 错误，需要后端优化 DTO 或 Jackson 配置。
- 商品列表可能为空（需在 `DatabaseInitConfig` 中插入 `status=1` 的商品）。
- VIP 等级数据可能为空（需初始化 `vip_level` 表）。
- Token 有效期为 24 小时（配置见 `application-dev.properties`）。
