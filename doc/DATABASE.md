# Market 平台数据库设计文档

## 数据库说明
- **开发环境**: H2 内存数据库
- **生产环境**: PostgreSQL 14+
- **字符集**: UTF-8

## 核心表结构

### 1. 用户表 (user)
存储所有用户账户信息（包括普通用户、商家、管理员）

```sql
CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(255),
    role VARCHAR(20) DEFAULT 'USER', -- USER, MERCHANT, ADMIN
    status VARCHAR(20) DEFAULT 'ACTIVE',
    
    -- 商家相关字段
    is_merchant BOOLEAN DEFAULT FALSE,
    merchant_status VARCHAR(20), -- PENDING, ACTIVE, BANNED
    shop_name VARCHAR(100),
    shop_description VARCHAR(500),
    
    -- 会员相关字段
    vip_level INTEGER DEFAULT 0,
    vip_expire_time TIMESTAMP,
    growth_value INTEGER DEFAULT 0,
    
    -- 积分相关字段
    credit INTEGER DEFAULT 0,
    total_credit INTEGER DEFAULT 0,
    consumed_credit INTEGER DEFAULT 0,
    
    -- 签到相关字段
    last_checkin_time TIMESTAMP,
    consecutive_checkin_days INTEGER DEFAULT 0,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_login_at TIMESTAMP
);
```

### 2. 商品表 (product)
```sql
CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    stock INTEGER NOT NULL DEFAULT 0,
    category VARCHAR(100) NOT NULL,
    brand VARCHAR(100),
    image_url VARCHAR(500),
    image_urls VARCHAR(1000),
    
    -- 状态字段
    available BOOLEAN DEFAULT TRUE,
    status INTEGER DEFAULT 1, -- 0-下架，1-上架
    audit_status INTEGER DEFAULT 1, -- 0-待审核，1-通过，2-拒绝
    reject_reason VARCHAR(500),
    
    -- 统计字段
    rating DOUBLE PRECISION DEFAULT 0.0,
    review_count INTEGER DEFAULT 0,
    sales INTEGER DEFAULT 0,
    
    -- 关联字段
    merchant_id BIGINT REFERENCES "user"(id),
    user_id BIGINT REFERENCES "user"(id),
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

### 3. 订单表 (order)
```sql
CREATE TABLE "order" (
    id BIGSERIAL PRIMARY KEY,
    order_no VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    merchant_id BIGINT REFERENCES "user"(id),
    
    -- 金额字段
    total_amount DECIMAL(10,2) NOT NULL,
    
    -- 状态字段
    status VARCHAR(50) NOT NULL, -- PENDING, PAID, SHIPPED, COMPLETED, CANCELLED
    
    -- 物流字段
    shipping_address VARCHAR(500),
    tracking_no VARCHAR(100),
    carrier VARCHAR(50),
    
    -- 支付字段
    payment_method VARCHAR(50),
    paid_at TIMESTAMP,
    
    -- 时间字段
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    shipped_at TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP
);
```

### 4. 订单明细表 (order_item)
```sql
CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES "order"(id),
    product_id BIGINT NOT NULL REFERENCES product(id),
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL
);
```

### 5. 商品评价表 (product_review)
```sql
CREATE TABLE product_review (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    user_name VARCHAR(50),
    user_avatar VARCHAR(500),
    product_id BIGINT NOT NULL REFERENCES product(id),
    product_name VARCHAR(200),
    order_id BIGINT NOT NULL,
    merchant_id BIGINT REFERENCES "user"(id),
    
    -- 评价内容
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    content VARCHAR(2000),
    images VARCHAR(2000),
    
    -- 商家回复
    merchant_reply VARCHAR(1000),
    reply_time TIMESTAMP,
    
    -- 审核状态
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

### 6. 优惠券表 (coupon)
```sql
CREATE TABLE coupon (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL, -- FIXED, PERCENT
    discount_value DECIMAL(10,2) NOT NULL,
    min_purchase DECIMAL(10,2),
    max_discount DECIMAL(10,2),
    
    -- 数量字段
    total_count INTEGER NOT NULL,
    remain_count INTEGER NOT NULL,
    used_count INTEGER NOT NULL DEFAULT 0,
    
    -- 有效期
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    
    -- 适用范围
    scope VARCHAR(50), -- ALL, CATEGORY, PRODUCT
    category_ids TEXT,
    product_ids TEXT,
    
    -- 状态
    status VARCHAR(20) NOT NULL, -- ACTIVE, INACTIVE, EXPIRED
    
    -- 创建者
    merchant_id BIGINT REFERENCES "user"(id),
    platform_id BIGINT REFERENCES "user"(id),
    
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

### 7. 用户优惠券表 (user_coupon)
```sql
CREATE TABLE user_coupon (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    coupon_id BIGINT NOT NULL REFERENCES coupon(id),
    status VARCHAR(255) NOT NULL, -- UNUSED, USED, EXPIRED
    order_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    obtained_at TIMESTAMP,
    used_at TIMESTAMP,
    
    UNIQUE(user_id, coupon_id)
);
```

### 8. 购物车表 (cart_item)
```sql
CREATE TABLE cart_item (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    product_id BIGINT NOT NULL REFERENCES product(id),
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### 9. 关注表 (follow)
```sql
CREATE TABLE follow (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    shop_id BIGINT NOT NULL REFERENCES "user"(id),
    shop_name VARCHAR(255) NOT NULL,
    shop_avatar VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, shop_id)
);
```

### 10. 收藏表 (user_favorite)
```sql
CREATE TABLE user_favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    product_id BIGINT NOT NULL REFERENCES product(id),
    product_name VARCHAR(200),
    product_image VARCHAR(500),
    shop_id BIGINT,
    shop_name VARCHAR(100),
    product_price DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, product_id)
);
```

### 11. 浏览历史表 (user_browse_history)
```sql
CREATE TABLE user_browse_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    product_id BIGINT NOT NULL REFERENCES product(id),
    product_name VARCHAR(200),
    product_image VARCHAR(500),
    shop_id BIGINT,
    shop_name VARCHAR(100),
    product_price DECIMAL(10,2),
    browse_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, product_id)
);
```

### 12. VIP 等级表 (vip_level)
```sql
CREATE TABLE vip_level (
    id BIGSERIAL PRIMARY KEY,
    level INTEGER NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    growth_value_required INTEGER NOT NULL,
    discount_rate DECIMAL(3,2) NOT NULL,
    daily_credit INTEGER NOT NULL,
    monthly_credit INTEGER NOT NULL,
    free_shipping_count INTEGER NOT NULL,
    refund_priority BOOLEAN NOT NULL,
    exclusive_service BOOLEAN NOT NULL,
    icon VARCHAR(500),
    background_color VARCHAR(20),
    text_color VARCHAR(20),
    privileges TEXT
);
```

### 13. 系统消息表 (system_message)
```sql
CREATE TABLE system_message (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content VARCHAR(5000) NOT NULL,
    type VARCHAR(20) NOT NULL, -- SYSTEM, ORDER, COUPON, VIP
    priority INTEGER NOT NULL DEFAULT 1,
    is_broadcast BOOLEAN NOT NULL DEFAULT FALSE,
    target_user_ids VARCHAR(2000),
    image_url VARCHAR(500),
    jump_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    send_time TIMESTAMP NOT NULL
);
```

### 14. 用户消息表 (message_receive)
```sql
CREATE TABLE message_receive (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    message_id BIGINT NOT NULL REFERENCES system_message(id),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, message_id)
);
```

### 15. 聊天消息表 (chat_message)
```sql
CREATE TABLE chat_message (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL REFERENCES "user"(id),
    receiver_id BIGINT NOT NULL REFERENCES "user"(id),
    content VARCHAR(2000) NOT NULL,
    type VARCHAR(20) NOT NULL, -- TEXT, IMAGE, FILE, SYSTEM
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sender_id ON chat_message(sender_id);
CREATE INDEX idx_receiver_id ON chat_message(receiver_id);
CREATE INDEX idx_created_at ON chat_message(created_at);
```

### 16. 论坛帖子表 (forum_post)
```sql
CREATE TABLE forum_post (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    user_name VARCHAR(50),
    user_avatar VARCHAR(500),
    title VARCHAR(200) NOT NULL,
    content VARCHAR(10000) NOT NULL,
    category VARCHAR(50),
    tags VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    view_count INTEGER NOT NULL DEFAULT 0,
    like_count INTEGER NOT NULL DEFAULT 0,
    comment_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

### 17. 论坛评论表 (forum_comment)
```sql
CREATE TABLE forum_comment (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES forum_post(id),
    parent_id BIGINT REFERENCES forum_comment(id),
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    user_name VARCHAR(50),
    user_avatar VARCHAR(500),
    content VARCHAR(2000) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    like_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### 18. 抽奖表 (lottery_prize)
```sql
CREATE TABLE lottery_prize (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    image VARCHAR(255),
    type INTEGER NOT NULL, -- 0-积分，1-优惠券，2-商品，3-实物
    weight INTEGER NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### 19. 抽奖记录表 (lottery_record)
```sql
CREATE TABLE lottery_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    prize_id BIGINT NOT NULL REFERENCES lottery_prize(id),
    prize_name VARCHAR(100) NOT NULL,
    prize_type INTEGER NOT NULL,
    cost INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### 20. 积分历史表 (credit_history)
```sql
CREATE TABLE credit_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    credit_change INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    reason VARCHAR(100) NOT NULL,
    related_order_id VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## 索引设计

### 商品表索引
```sql
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_product_merchant ON product(merchant_id);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_audit ON product(audit_status);
CREATE INDEX idx_product_created ON product(created_at);
```

### 订单表索引
```sql
CREATE INDEX idx_order_user ON "order"(user_id);
CREATE INDEX idx_order_merchant ON "order"(merchant_id);
CREATE INDEX idx_order_status ON "order"(status);
CREATE INDEX idx_order_created ON "order"(created_at);
CREATE INDEX idx_order_no ON "order"(order_no);
```

## 数据初始化

系统启动时会自动执行 `data.sql` 脚本，创建以下基础数据：
1. 管理员账户：admin / 123456
2. 测试商家账户：merchant1 / 123456
3. 测试用户账户：user1 / 123456
4. 测试商品数据（5 个商品）
5. 测试优惠券数据（3 张优惠券）
6. VIP 等级配置（6 个等级）
7. 系统消息模板
