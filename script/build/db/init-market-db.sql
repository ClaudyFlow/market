-- ============================================================
-- Market 数据库初始化脚本
-- 包含：创建用户、创建数据库、初始化表结构、插入基础数据
-- 使用方式：psql -U postgres -f init-market-db.sql
-- ============================================================

-- ==================== 1. 创建数据库用户 ====================
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'market') THEN
        CREATE USER market WITH PASSWORD 'market';
        RAISE NOTICE '用户 market 创建成功';
    ELSE
        RAISE NOTICE '用户 market 已存在，跳过';
    END IF;
END
$$;

-- ==================== 2. 创建数据库 ====================
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'market') THEN
        CREATE DATABASE market OWNER market;
        RAISE NOTICE '数据库 market 创建成功';
    ELSE
        RAISE NOTICE '数据库 market 已存在，跳过';
    END IF;
END
$$;

-- ==================== 3. 授予权限 ====================
GRANT ALL PRIVILEGES ON DATABASE market TO market;

-- ==================== 4. 连接到 market 数据库 ====================
\c market market;

-- 启用 UUID 扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==================== 5. 创建表结构 ====================

-- 用户表
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255),
    phone VARCHAR(20),
    bio VARCHAR(500),
    credit INTEGER NOT NULL DEFAULT 0,
    total_credit INTEGER NOT NULL DEFAULT 0,
    consumed_credit INTEGER NOT NULL DEFAULT 0,
    vip_level INTEGER NOT NULL DEFAULT 0,
    vip_expire_time TIMESTAMP,
    growth_value INTEGER NOT NULL DEFAULT 0,
    consecutive_checkin_days INTEGER NOT NULL DEFAULT 0,
    last_checkin_time TIMESTAMP,
    is_merchant BOOLEAN NOT NULL DEFAULT false,
    shop_name VARCHAR(100),
    shop_description VARCHAR(500),
    merchant_status VARCHAR(20) DEFAULT 'INACTIVE',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_login_at TIMESTAMP
);

-- VIP等级表
CREATE TABLE IF NOT EXISTS vip_level (
    id BIGSERIAL PRIMARY KEY,
    level INTEGER UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    discount_rate DECIMAL(5,2) NOT NULL,
    growth_value_required INTEGER NOT NULL DEFAULT 0,
    daily_credit INTEGER NOT NULL DEFAULT 0,
    monthly_credit INTEGER NOT NULL DEFAULT 0,
    exclusive_service BOOLEAN NOT NULL DEFAULT false,
    free_shipping_count INTEGER NOT NULL DEFAULT 0,
    refund_priority BOOLEAN NOT NULL DEFAULT false,
    background_color VARCHAR(20),
    text_color VARCHAR(20),
    icon VARCHAR(50),
    description VARCHAR(200),
    privileges TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE SET NULL,
    merchant_id BIGINT REFERENCES "user"(id) ON DELETE SET NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    stock INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    image_urls VARCHAR(1000),
    category VARCHAR(100) NOT NULL,
    brand VARCHAR(100),
    available BOOLEAN NOT NULL DEFAULT true,
    status INTEGER DEFAULT 1,
    audit_status INTEGER DEFAULT 1,
    reject_reason VARCHAR(500),
    rating DOUBLE PRECISION DEFAULT 0.0,
    review_count INTEGER DEFAULT 0,
    sales INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 店铺表
CREATE TABLE IF NOT EXISTS shop (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    logo VARCHAR(500),
    banner VARCHAR(1000),
    description VARCHAR(500),
    slogan VARCHAR(1000),
    rating DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    followers INTEGER NOT NULL DEFAULT 0,
    product_count INTEGER NOT NULL DEFAULT 0,
    positive_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    open_years INTEGER DEFAULT 0,
    announcement VARCHAR(1000),
    certified BOOLEAN DEFAULT false,
    tags VARCHAR(500),
    status VARCHAR(100) DEFAULT 'active',
    business_license VARCHAR(500),
    location VARCHAR(200),
    description_score DECIMAL(3,2) DEFAULT 0.00,
    service_score DECIMAL(3,2) DEFAULT 0.00,
    logistics_score DECIMAL(3,2) DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_no VARCHAR(64) UNIQUE NOT NULL,
    user_id BIGINT REFERENCES "user"(id) ON DELETE SET NULL,
    shop_id BIGINT REFERENCES shop(id) ON DELETE SET NULL,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    final_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    credit_discount DECIMAL(10,2) NOT NULL DEFAULT 0,
    coupon_discount DECIMAL(10,2) NOT NULL DEFAULT 0,
    status VARCHAR(32) DEFAULT 'PENDING',
    payment_status VARCHAR(32) DEFAULT 'UNPAID',
    payment_method VARCHAR(32),
    payment_time TIMESTAMP,
    shipping_address TEXT,
    shipping_name VARCHAR(100),
    shipping_phone VARCHAR(20),
    shipping_status VARCHAR(32) DEFAULT 'UNSHIPPED',
    shipping_time TIMESTAMP,
    delivered_time TIMESTAMP,
    received_time TIMESTAMP,
    received确认 BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 订单项表
CREATE TABLE IF NOT EXISTS order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES product(id) ON DELETE SET NULL,
    product_name VARCHAR(200) NOT NULL,
    product_image VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    subtotal DECIMAL(10,2) NOT NULL,
    sku_info VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 购物车表
CREATE TABLE IF NOT EXISTS cart_item (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES product(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 用户地址表
CREATE TABLE IF NOT EXISTS user_address (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    receiver_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    detail_address VARCHAR(200) NOT NULL,
    is_default BOOLEAN DEFAULT false,
    label VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 用户收藏表
CREATE TABLE IF NOT EXISTS user_favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES product(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, product_id)
);

-- 用户关注表
CREATE TABLE IF NOT EXISTS user_follow (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    shop_id BIGINT REFERENCES shop(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, shop_id)
);

-- 评价表
CREATE TABLE IF NOT EXISTS review (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    user_id BIGINT REFERENCES "user"(id) ON DELETE SET NULL,
    product_id BIGINT REFERENCES product(id) ON DELETE SET NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    content VARCHAR(1000),
    image_urls VARCHAR(1000),
    reply VARCHAR(500),
    reply_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 店铺评价表
CREATE TABLE IF NOT EXISTS shop_review (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    user_id BIGINT REFERENCES "user"(id) ON DELETE SET NULL,
    shop_id BIGINT REFERENCES shop(id) ON DELETE SET NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    content VARCHAR(1000),
    image_urls VARCHAR(1000),
    reply VARCHAR(500),
    reply_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 积分记录表
CREATE TABLE IF NOT EXISTS credit_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    type VARCHAR(32) NOT NULL,
    amount INTEGER NOT NULL,
    balance INTEGER NOT NULL,
    source VARCHAR(50),
    order_id BIGINT,
    description VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 优惠券表
CREATE TABLE IF NOT EXISTS coupon (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(32) NOT NULL,
    discount_type VARCHAR(32) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_amount DECIMAL(10,2) DEFAULT 0,
    max_discount DECIMAL(10,2),
    total_count INTEGER NOT NULL,
    remaining_count INTEGER NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 用户优惠券表
CREATE TABLE IF NOT EXISTS user_coupon (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    coupon_id BIGINT REFERENCES coupon(id) ON DELETE CASCADE,
    order_id BIGINT,
    status VARCHAR(20) DEFAULT 'UNUSED',
    received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP
);

-- 抽奖奖品表
CREATE TABLE IF NOT EXISTS lottery_prize (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(32) NOT NULL,
    level INTEGER NOT NULL,
    probability DECIMAL(5,4) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    credit_cost INTEGER DEFAULT 0,
    description VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 抽奖记录表
CREATE TABLE IF NOT EXISTS lottery_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    prize_id BIGINT REFERENCES lottery_prize(id) ON DELETE SET NULL,
    prize_name VARCHAR(100),
    prize_type VARCHAR(32),
    status VARCHAR(20) DEFAULT 'PENDING',
    exchange_code VARCHAR(64),
    exchange_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 活动表
CREATE TABLE IF NOT EXISTS activity (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description VARCHAR(1000),
    rules TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 物流信息表
CREATE TABLE IF NOT EXISTS logistics_info (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    company VARCHAR(50),
    tracking_no VARCHAR(100),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 物流轨迹表
CREATE TABLE IF NOT EXISTS logistics_track (
    id BIGSERIAL PRIMARY KEY,
    logistics_id BIGINT REFERENCES logistics_info(id) ON DELETE CASCADE,
    status VARCHAR(100),
    location VARCHAR(200),
    description VARCHAR(500),
    timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 敏感词表
CREATE TABLE IF NOT EXISTS sensitive_word (
    id BIGSERIAL PRIMARY KEY,
    word VARCHAR(50) NOT NULL,
    type VARCHAR(20) DEFAULT 'COMMON',
    level INTEGER DEFAULT 1,
    replacement VARCHAR(50) DEFAULT '*',
    enabled BOOLEAN DEFAULT true,
    match_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 系统消息表
CREATE TABLE IF NOT EXISTS system_message (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(32) DEFAULT 'SYSTEM',
    priority INTEGER DEFAULT 0,
    image_url VARCHAR(500),
    jump_url VARCHAR(500),
    send_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_broadcast BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户消息表
CREATE TABLE IF NOT EXISTS user_notification (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    type VARCHAR(32) NOT NULL,
    title VARCHAR(200),
    content TEXT,
    is_read BOOLEAN DEFAULT false,
    related_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户浏览历史表
CREATE TABLE IF NOT EXISTS user_browse_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES product(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 论坛帖子表
CREATE TABLE IF NOT EXISTS forum_post (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    images VARCHAR(1000),
    category VARCHAR(50),
    tags VARCHAR(200),
    view_count INTEGER DEFAULT 0,
    like_count INTEGER DEFAULT 0,
    comment_count INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 论坛评论表
CREATE TABLE IF NOT EXISTS forum_comment (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT REFERENCES forum_post(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    parent_id BIGINT,
    content TEXT NOT NULL,
    like_count INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    receiver_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    type VARCHAR(20) DEFAULT 'TEXT',
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- VIP礼品表
CREATE TABLE IF NOT EXISTS vip_gift (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    image_url VARCHAR(500),
    credit_cost INTEGER NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- VIP礼品领取记录表
CREATE TABLE IF NOT EXISTS vip_gift_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    gift_id BIGINT REFERENCES vip_gift(id) ON DELETE SET NULL,
    gift_name VARCHAR(100),
    address_id BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- VIP充值订单表
CREATE TABLE IF NOT EXISTS vip_recharge_order (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    order_no VARCHAR(64) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    credit_amount INTEGER NOT NULL,
    payment_method VARCHAR(32),
    payment_status VARCHAR(32) DEFAULT 'PENDING',
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 积分充值表（别名）
CREATE TABLE IF NOT EXISTS credit_recharge (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    order_no VARCHAR(64) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    credit_amount INTEGER NOT NULL,
    payment_method VARCHAR(32),
    payment_status VARCHAR(32) DEFAULT 'PENDING',
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 支付表
CREATE TABLE IF NOT EXISTS payment (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    user_id BIGINT REFERENCES "user"(id) ON DELETE SET NULL,
    payment_no VARCHAR(64) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(32),
    status VARCHAR(32) DEFAULT 'PENDING',
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 退款表
CREATE TABLE IF NOT EXISTS payment_refund (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT REFERENCES payment(id) ON DELETE SET NULL,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    refund_no VARCHAR(64) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(32) DEFAULT 'PENDING',
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 索引创建
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_user_name ON "user"(name);
CREATE INDEX IF NOT EXISTS idx_user_email ON "user"(email);
CREATE INDEX IF NOT EXISTS idx_product_merchant ON product(merchant_id);
CREATE INDEX IF NOT EXISTS idx_product_user ON product(user_id);
CREATE INDEX IF NOT EXISTS idx_product_category ON product(category);
CREATE INDEX IF NOT EXISTS idx_product_status ON product(status);
CREATE INDEX IF NOT EXISTS idx_shop_owner ON shop(owner_id);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_order_item_order ON order_item(order_id);
CREATE INDEX IF NOT EXISTS idx_cart_item_user ON cart_item(user_id);
CREATE INDEX IF NOT EXISTS idx_review_product ON review(product_id);
CREATE INDEX IF NOT EXISTS idx_review_user ON review(user_id);
CREATE INDEX IF NOT EXISTS idx_credit_history_user ON credit_history(user_id);
CREATE INDEX IF NOT EXISTS idx_coupon_status ON coupon(status);

-- ==================== 6. 插入初始数据 ====================

-- 插入管理员账户 (密码: admin123)
INSERT INTO "user" (
    name, email, password_hash, is_merchant,
    credit, total_credit, consumed_credit,
    vip_level, growth_value, consecutive_checkin_days,
    role, status, created_at
) VALUES (
    'admin', 'admin@market.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8IjtVzXmhqZz.KcXJnZbZfUW',
    false,
    0, 0, 0,
    0, 0, 0,
    'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP
) ON CONFLICT (name) DO NOTHING;

-- 插入VIP等级
INSERT INTO vip_level (level, name, discount_rate, growth_value_required, daily_credit, monthly_credit, exclusive_service, free_shipping_count, refund_priority, background_color, text_color, icon, description, privileges) VALUES
(1, '普通会员', 0.98, 0, 5, 50, false, 0, false, '#f0f0f0', '#333333', 'vip-bronze', '基础会员权益', '[]'),
(2, '白银会员', 0.95, 1000, 10, 100, false, 0, false, '#C0C0C0', '#333333', 'vip-silver', '享9.5折优惠', '[]'),
(3, '黄金会员', 0.92, 5000, 20, 200, true, 1, false, '#FFD700', '#000000', 'vip-gold', '享9.2折+免邮', '[]'),
(4, '铂金会员', 0.88, 20000, 50, 500, true, 3, true, '#E5E4E2', '#000000', 'vip-platinum', '享8.8折+优先发货', '[]'),
(5, '钻石会员', 0.85, 50000, 100, 1000, true, 5, true, '#B9F2FF', '#000000', 'vip-diamond', '尊享8.5折+专属客服', '[]'),
(6, '黑金会员', 0.80, 100000, 200, 2000, true, 10, true, '#1a1a1a', '#FFFFFF', 'vip-black', '最高等级尊享8折', '[]')
ON CONFLICT (level) DO NOTHING;

-- 插入测试商品
INSERT INTO product (user_id, merchant_id, name, description, category, price, stock, available, status, created_at, updated_at, image_url) VALUES
(1, 1, '无线蓝牙耳机', '高品质无线蓝牙耳机，降噪效果好', '数码', 199.00, 100, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=耳机'),
(1, 1, '智能手环', '运动健康监测，长续航30天', '数码', 149.00, 100, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=手环'),
(1, 1, '机械键盘', 'Cherry轴，RGB背光，热插拔', '数码', 329.00, 50, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=键盘'),
(1, 1, '华为Pura 70', '麒麟芯片，旗舰影像', '手机', 5999.00, 50, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=Pura70'),
(1, 1, '小米14 Pro', '骁龙8 Gen3，徕卡影像', '手机', 4299.00, 80, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=Mi14Pro'),
(1, 1, 'iPad Air 6', 'Apple M2芯片，轻薄便携', '平板', 4799.00, 30, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=iPadAir6')
ON CONFLICT DO NOTHING;

-- 插入店铺
INSERT INTO shop (owner_id, name, description, logo, banner, status, rating, followers, product_count, positive_rate, created_at, updated_at) VALUES
(1, 'Admin官方旗舰店', '管理员官方认证店铺', 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=AdminShop', 'https://via.placeholder.com/800x200/1a2a4a/00d4ff?text=ShopBanner', 'active', 5.00, 100, 6, 98.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- 插入系统公告
INSERT INTO system_message (title, content, type, priority, image_url, jump_url, send_time, is_broadcast, created_at) VALUES
('系统维护通知', '系统将于2026-05-15 02:00-04:00进行例行维护，请提前做好准备。', 'SYSTEM', 1, 'https://via.placeholder.com/200x100/ff0000/ffffff?text=Maintenance', '/announcement/1', CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP),
('新功能上线', '积分商城功能已正式上线，快来兑换心仪的商品吧！', 'ACTIVITY', 2, 'https://via.placeholder.com/200x100/00ff00/ffffff?text=NewFeature', '/integral-mall', CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- 插入敏感词
INSERT INTO sensitive_word (word, type, level, replacement, enabled, match_count, created_at) VALUES
('测试', 'COMMON', 1, '*', true, 0, CURRENT_TIMESTAMP),
('敏感', 'COMMON', 2, '*', true, 0, CURRENT_TIMESTAMP),
('违规', 'COMMON', 2, '*', true, 0, CURRENT_TIMESTAMP),
('垃圾', 'ADVANCED', 3, '**', true, 0, CURRENT_TIMESTAMP),
('广告', 'ADVANCED', 3, '**', true, 0, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- ==================== 7. 验证 ====================
SELECT '=== 数据库初始化完成 ===' AS status;
SELECT '用户: market' AS info, '密码: market' AS info2, '数据库: market' AS info3;
SELECT '表数量' AS item, COUNT(*) AS count FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE';
SELECT '用户总数' AS item, COUNT(*) AS count FROM "user";
SELECT '商品总数' AS item, COUNT(*) AS count FROM product;
SELECT 'VIP等级数' AS item, COUNT(*) AS count FROM vip_level;