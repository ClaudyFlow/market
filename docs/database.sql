-- ============================================================
-- Market 电商平台 - PostgreSQL 数据库脚本
-- ============================================================
-- 数据库：market_db
-- 版本：1.1.0
-- 创建日期：2026-03-16
-- 更新日期：2026-03-26
-- ============================================================

-- 创建数据库
-- CREATE DATABASE market_db;

-- 连接到数据库
-- \c market_db;

-- ============================================================
-- 1. 用户相关表
-- ============================================================

-- 用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255),
    points INTEGER NOT NULL DEFAULT 0,
    total_points INTEGER NOT NULL DEFAULT 0,
    consumed_points INTEGER NOT NULL DEFAULT 0,
    vip_level INTEGER NOT NULL DEFAULT 0,
    vip_expire_time TIMESTAMP,
    growth_value INTEGER NOT NULL DEFAULT 0,
    consecutive_checkin_days INTEGER NOT NULL DEFAULT 0,
    last_checkin_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户索引
CREATE INDEX idx_users_name ON users(name);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_vip_level ON users(vip_level);

-- 用户地址表
CREATE TABLE user_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_name VARCHAR(100) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    province VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL,
    detail_address VARCHAR(255) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_addresses_user_id ON user_addresses(user_id);

-- 积分历史记录表
CREATE TABLE points_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    points_change INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    reason VARCHAR(100) NOT NULL,
    related_order_id VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_points_history_user_id ON points_history(user_id);
CREATE INDEX idx_points_history_created_at ON points_history(created_at);

-- ============================================================
-- 2. 商品相关表
-- ============================================================

-- 商品分类表
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    level INTEGER NOT NULL DEFAULT 1,
    sort_order INTEGER NOT NULL DEFAULT 0,
    icon_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_parent_id ON categories(parent_id);
CREATE INDEX idx_categories_level ON categories(level);

-- 商品表
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    original_price DECIMAL(10, 2),
    stock INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    images TEXT[],
    category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    category_name VARCHAR(100) NOT NULL,
    brand VARCHAR(100),
    sales_count INTEGER NOT NULL DEFAULT 0,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    rating_avg DECIMAL(3, 2) DEFAULT 0,
    rating_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_category_name ON products(category_name);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_sales ON products(sales_count);
CREATE INDEX idx_products_available ON products(available);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_name ON products(name);

-- 商品规格表
CREATE TABLE product_skus (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sku_name VARCHAR(100) NOT NULL,
    sku_value VARCHAR(100) NOT NULL,
    price_adjustment DECIMAL(10, 2) DEFAULT 0,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_skus_product_id ON product_skus(product_id);

-- 商品轮播图表
CREATE TABLE product_banners (
    id BIGSERIAL PRIMARY KEY,
    image_url VARCHAR(500) NOT NULL,
    link_url VARCHAR(500),
    title VARCHAR(100),
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 3. 订单相关表
-- ============================================================

-- 订单表
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_no VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    shipping_fee DECIMAL(10, 2) DEFAULT 0,
    actual_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    payment_time TIMESTAMP,
    shipping_address TEXT,
    receiver_name VARCHAR(100),
    receiver_phone VARCHAR(20),
    remark VARCHAR(500),
    cancel_reason VARCHAR(255),
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_order_no ON orders(order_no);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- 订单项表
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    product_name VARCHAR(200) NOT NULL,
    product_image VARCHAR(500),
    sku_info VARCHAR(255),
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- 订单物流表
CREATE TABLE order_shippings (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    shipping_company VARCHAR(100),
    shipping_no VARCHAR(100),
    shipping_status VARCHAR(50),
    shipped_at TIMESTAMP,
    received_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_shippings_order_id ON order_shippings(order_id);

-- ============================================================
-- 4. 购物车表
-- ============================================================

-- 购物车项表
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    selected BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, product_id)
);

CREATE INDEX idx_cart_items_user_id ON cart_items(user_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);

-- ============================================================
-- 5. 收藏与关注表
-- ============================================================

-- 商品收藏表
CREATE TABLE favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, product_id)
);

CREATE INDEX idx_favorites_user_id ON favorites(user_id);
CREATE INDEX idx_favorites_product_id ON favorites(product_id);

-- 店铺关注表
CREATE TABLE follows (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    shop_id BIGINT NOT NULL,
    shop_name VARCHAR(200) NOT NULL,
    shop_avatar VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, shop_id)
);

CREATE INDEX idx_follows_user_id ON follows(user_id);
CREATE INDEX idx_follows_shop_id ON follows(shop_id);

-- ============================================================
-- 6. 评价相关表
-- ============================================================

-- 商品评价表
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    content TEXT,
    images TEXT[],
    user_name VARCHAR(50) NOT NULL,
    user_avatar VARCHAR(255),
    product_name VARCHAR(200) NOT NULL,
    product_image VARCHAR(500),
    product_price DECIMAL(10, 2),
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    reply_content TEXT,
    reply_time TIMESTAMP,
    helpful_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reviews_user_id ON reviews(user_id);
CREATE INDEX idx_reviews_product_id ON reviews(product_id);
CREATE INDEX idx_reviews_order_id ON reviews(order_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);
CREATE INDEX idx_reviews_created_at ON reviews(created_at);

-- 评价图片表
CREATE TABLE review_images (
    id BIGSERIAL PRIMARY KEY,
    review_id BIGINT NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_review_images_review_id ON review_images(review_id);

-- ============================================================
-- 7. 优惠券相关表
-- ============================================================

-- 优惠券模板表
CREATE TABLE coupons (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL,
    min_purchase DECIMAL(10, 2),
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    total_count INTEGER NOT NULL DEFAULT 100,
    used_count INTEGER NOT NULL DEFAULT 0,
    per_user_limit INTEGER NOT NULL DEFAULT 1,
    applicable_categories TEXT[],
    applicable_products BIGINT[],
    active BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_coupons_type ON coupons(type);
CREATE INDEX idx_coupons_active ON coupons(active);
CREATE INDEX idx_coupons_valid_period ON coupons(valid_from, valid_to);

-- 用户优惠券表
CREATE TABLE user_coupons (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    coupon_id BIGINT NOT NULL REFERENCES coupons(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'UNUSED',
    used_at TIMESTAMP,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, coupon_id, order_id)
);

CREATE INDEX idx_user_coupons_user_id ON user_coupons(user_id);
CREATE INDEX idx_user_coupons_status ON user_coupons(status);
CREATE INDEX idx_user_coupons_valid_to ON user_coupons(valid_to);

-- ============================================================
-- 8. 商家相关表
-- ============================================================

-- 商家表
CREATE TABLE merchants (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    shop_name VARCHAR(200) NOT NULL,
    shop_logo VARCHAR(500),
    shop_description TEXT,
    shop_rating DECIMAL(3, 2) DEFAULT 0,
    follower_count INTEGER NOT NULL DEFAULT 0,
    product_count INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    contact_name VARCHAR(100),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    business_license VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_merchants_user_id ON merchants(user_id);
CREATE INDEX idx_merchants_status ON merchants(status);

-- 商家公告表
CREATE TABLE merchant_announcements (
    id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_top BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_merchant_announcements_merchant_id ON merchant_announcements(merchant_id);

-- ============================================================
-- 9. 管理员相关表
-- ============================================================

-- 管理员表
CREATE TABLE admins (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    real_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar_url VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
    permissions TEXT[],
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 系统公告表
CREATE TABLE system_announcements (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'NOTICE',
    target_user_type VARCHAR(50),
    is_top BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    publish_time TIMESTAMP,
    view_count INTEGER NOT NULL DEFAULT 0,
    created_by BIGINT REFERENCES admins(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_system_announcements_type ON system_announcements(type);
CREATE INDEX idx_system_announcements_active ON system_announcements(is_active);

-- 管理员操作日志表
CREATE TABLE admin_operation_logs (
    id BIGSERIAL PRIMARY KEY,
    admin_id BIGINT REFERENCES admins(id) ON DELETE SET NULL,
    operation_type VARCHAR(50) NOT NULL,
    operation_module VARCHAR(50) NOT NULL,
    operation_description VARCHAR(500),
    request_method VARCHAR(10),
    request_url VARCHAR(255),
    request_params TEXT,
    response_status INTEGER,
    ip_address VARCHAR(50),
    user_agent TEXT,
    execution_time INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_admin_operation_logs_admin_id ON admin_operation_logs(admin_id);
CREATE INDEX idx_admin_operation_logs_type ON admin_operation_logs(operation_type);
CREATE INDEX idx_admin_operation_logs_created_at ON admin_operation_logs(created_at);

-- ============================================================
-- 10. 系统配置表
-- ============================================================

-- 系统配置表
CREATE TABLE system_settings (
    id BIGSERIAL PRIMARY KEY,
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT,
    setting_type VARCHAR(50) NOT NULL DEFAULT 'STRING',
    description VARCHAR(255),
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 验证码表
CREATE TABLE verification_codes (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_verification_codes_email ON verification_codes(email);
CREATE INDEX idx_verification_codes_expires_at ON verification_codes(expires_at);

-- ============================================================
-- 11. 抽奖相关表
-- ============================================================

-- 抽奖奖品表
CREATE TABLE lottery_prize (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    type INTEGER NOT NULL, -- 1=积分，2=实物
    weight INTEGER NOT NULL, -- 权重
    image VARCHAR(255),
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lottery_prize_type ON lottery_prize(type);
CREATE INDEX idx_lottery_prize_available ON lottery_prize(available);

-- 抽奖记录表
CREATE TABLE lottery_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    prize_id BIGINT NOT NULL REFERENCES lottery_prize(id) ON DELETE CASCADE,
    prize_name VARCHAR(100) NOT NULL,
    prize_type INTEGER NOT NULL, -- 1=积分，2=实物
    cost INTEGER NOT NULL, -- 消耗积分
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lottery_record_user_id ON lottery_record(user_id);
CREATE INDEX idx_lottery_record_created_at ON lottery_record(created_at);

-- ============================================================
-- 12. 聊天消息相关表
-- ============================================================

-- 聊天消息表
CREATE TABLE chat_message (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content VARCHAR(2000) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'TEXT', -- TEXT=文本，IMAGE=图片，SYSTEM=系统，FILE=文件
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_chat_message_sender_id ON chat_message(sender_id);
CREATE INDEX idx_chat_message_receiver_id ON chat_message(receiver_id);
CREATE INDEX idx_chat_message_created_at ON chat_message(created_at);

-- ============================================================
-- 13. 触发器函数
-- ============================================================

-- 更新 updated_at 字段的通用函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为需要自动更新 updated_at 的表创建触发器
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

DROP TRIGGER IF EXISTS update_products_updated_at ON products;
CREATE TRIGGER update_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

DROP TRIGGER IF EXISTS update_orders_updated_at ON orders;
CREATE TRIGGER update_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

DROP TRIGGER IF EXISTS update_user_addresses_updated_at ON user_addresses;
CREATE TRIGGER update_user_addresses_updated_at
    BEFORE UPDATE ON user_addresses
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

DROP TRIGGER IF EXISTS update_categories_updated_at ON categories;
CREATE TRIGGER update_categories_updated_at
    BEFORE UPDATE ON categories
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

DROP TRIGGER IF EXISTS update_cart_items_updated_at ON cart_items;
CREATE TRIGGER update_cart_items_updated_at
    BEFORE UPDATE ON cart_items
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

DROP TRIGGER IF EXISTS update_reviews_updated_at ON reviews;
CREATE TRIGGER update_reviews_updated_at
    BEFORE UPDATE ON reviews
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

DROP TRIGGER IF EXISTS update_coupons_updated_at ON coupons;
CREATE TRIGGER update_coupons_updated_at
    BEFORE UPDATE ON coupons
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

DROP TRIGGER IF EXISTS update_merchants_updated_at ON merchants;
CREATE TRIGGER update_merchants_updated_at
    BEFORE UPDATE ON merchants
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

DROP TRIGGER IF EXISTS update_admins_updated_at ON admins;
CREATE TRIGGER update_admins_updated_at
    BEFORE UPDATE ON admins
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

DROP TRIGGER IF EXISTS update_system_settings_updated_at ON system_settings;
CREATE TRIGGER update_system_settings_updated_at
    BEFORE UPDATE ON system_settings
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

-- ============================================================
-- 14. 视图
-- ============================================================

-- 商品统计视图
CREATE OR REPLACE VIEW product_stats_view AS
SELECT
    p.id,
    p.name,
    p.price,
    p.sales_count,
    p.rating_avg,
    p.rating_count,
    p.stock,
    p.available,
    c.name AS category_name,
    COUNT(DISTINCT f.id) AS favorite_count
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN favorites f ON p.id = f.product_id
GROUP BY p.id, p.name, p.price, p.sales_count, p.rating_avg, p.rating_count, p.stock, p.available, c.name;

-- 用户订单统计视图
CREATE OR REPLACE VIEW user_order_stats_view AS
SELECT
    u.id AS user_id,
    u.name AS user_name,
    COUNT(DISTINCT o.id) AS total_orders,
    COALESCE(SUM(o.actual_amount), 0) AS total_spent,
    COUNT(DISTINCT CASE WHEN o.status = 'COMPLETED' THEN o.id END) AS completed_orders,
    COUNT(DISTINCT CASE WHEN o.status = 'PENDING' THEN o.id END) AS pending_orders
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.name;

-- ============================================================
-- 15. 注释
-- ============================================================

COMMENT ON TABLE users IS '用户表 - 存储用户基本信息、积分、VIP 状态等';
COMMENT ON TABLE user_addresses IS '用户地址表 - 存储用户的收货地址';
COMMENT ON TABLE points_history IS '积分历史记录表 - 存储用户积分变动记录';
COMMENT ON TABLE categories IS '商品分类表 - 存储商品分类信息';
COMMENT ON TABLE products IS '商品表 - 存储商品信息、价格、库存等';
COMMENT ON TABLE product_skus IS '商品规格表 - 存储商品的不同规格（如颜色、尺寸）';
COMMENT ON TABLE product_banners IS '商品轮播图表 - 存储首页轮播图';
COMMENT ON TABLE orders IS '订单表 - 存储订单主表信息';
COMMENT ON TABLE order_items IS '订单项表 - 存储订单中的商品明细';
COMMENT ON TABLE order_shippings IS '订单物流表 - 存储订单物流信息';
COMMENT ON TABLE cart_items IS '购物车项表 - 存储用户购物车商品';
COMMENT ON TABLE favorites IS '商品收藏表 - 存储用户收藏的商品';
COMMENT ON TABLE follows IS '店铺关注表 - 存储用户关注的店铺';
COMMENT ON TABLE reviews IS '商品评价表 - 存储用户对商品的评价';
COMMENT ON TABLE review_images IS '评价图片表 - 存储评价的图片';
COMMENT ON TABLE coupons IS '优惠券模板表 - 存储优惠券发行信息';
COMMENT ON TABLE user_coupons IS '用户优惠券表 - 存储用户领取的优惠券';
COMMENT ON TABLE merchants IS '商家表 - 存储入驻商家信息';
COMMENT ON TABLE merchant_announcements IS '商家公告表 - 存储商家发布的公告';
COMMENT ON TABLE admins IS '管理员表 - 存储后台管理员信息';
COMMENT ON TABLE system_announcements IS '系统公告表 - 存储系统公告';
COMMENT ON TABLE admin_operation_logs IS '管理员操作日志表 - 存储管理员操作记录';
COMMENT ON TABLE system_settings IS '系统配置表 - 存储系统配置信息';
COMMENT ON TABLE verification_codes IS '验证码表 - 存储邮箱验证码';
COMMENT ON TABLE lottery_prize IS '抽奖奖品表 - 存储抽奖活动的奖品信息';
COMMENT ON TABLE lottery_record IS '抽奖记录表 - 存储用户的抽奖历史记录';
COMMENT ON TABLE chat_message IS '聊天消息表 - 存储用户与客服之间的聊天消息';

-- ============================================================
-- 16. 初始化数据
-- ============================================================

-- 插入默认管理员账户 (密码：admin123)
INSERT INTO admins (username, password_hash, real_name, role, is_active)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', '系统管理员', 'SUPER_ADMIN', TRUE)
ON CONFLICT (username) DO NOTHING;

-- 插入默认商品分类
INSERT INTO categories (name, parent_id, level, sort_order, is_active) VALUES
('电子产品', NULL, 1, 1, TRUE),
('服装鞋帽', NULL, 1, 2, TRUE),
('家居生活', NULL, 1, 3, TRUE),
('美食生鲜', NULL, 1, 4, TRUE),
('图书文娱', NULL, 1, 5, TRUE),
('手机数码', (SELECT id FROM categories WHERE name = '电子产品'), 2, 1, TRUE),
('电脑办公', (SELECT id FROM categories WHERE name = '电子产品'), 2, 2, TRUE),
('男装', (SELECT id FROM categories WHERE name = '服装鞋帽'), 2, 1, TRUE),
('女装', (SELECT id FROM categories WHERE name = '服装鞋帽'), 2, 2, TRUE),
('家具家装', (SELECT id FROM categories WHERE name = '家居生活'), 2, 1, TRUE)
ON CONFLICT DO NOTHING;

-- 插入系统配置
INSERT INTO system_settings (setting_key, setting_value, setting_type, description, is_public) VALUES
('site_name', 'Market 电商平台', 'STRING', '网站名称', TRUE),
('site_logo', '/images/logo.png', 'STRING', '网站 Logo', TRUE),
('default_avatar', '/images/default-avatar.png', 'STRING', '默认头像', TRUE),
('checkin_base_points', '10', 'INTEGER', '签到基础积分', TRUE),
('checkin_extra_points', '5', 'INTEGER', '连续签到额外积分', TRUE),
('order_points_rate', '0.01', 'DECIMAL', '订单积分返还比例', FALSE),
('free_shipping_threshold', '99.00', 'DECIMAL', '包邮门槛', TRUE),
('max_cart_items', '100', 'INTEGER', '购物车最大商品数', TRUE)
ON CONFLICT (setting_key) DO NOTHING;

-- 插入示例优惠券
INSERT INTO coupons (name, type, discount_value, min_purchase, valid_from, valid_to, total_count, per_user_limit, active, description) VALUES
('新人优惠券', 'FIXED', 20.00, 100.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 1000, 1, TRUE, '新用户注册专享'),
('满减优惠券', 'FIXED', 50.00, 300.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '15 days', 500, 2, TRUE, '全场通用'),
('折扣优惠券', 'PERCENT', 0.9, 50.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '7 days', 200, 1, TRUE, '9 折优惠')
ON CONFLICT DO NOTHING;

-- 插入示例抽奖奖品
INSERT INTO lottery_prize (name, description, type, weight, image, available) VALUES
('10 积分', '奖励 10 积分', 1, 50, '/images/prize_10.png', TRUE),
('50 积分', '奖励 50 积分', 1, 30, '/images/prize_50.png', TRUE),
('100 积分', '奖励 100 积分', 1, 15, '/images/prize_100.png', TRUE),
('500 积分', '奖励 500 积分', 1, 4, '/images/prize_500.png', TRUE),
('神秘大奖', '神秘实物奖品', 2, 1, '/images/prize_mystery.png', TRUE)
ON CONFLICT DO NOTHING;

-- ============================================================
-- 17. 验证
-- ============================================================

-- 检查所有表是否创建成功
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- 显示创建的表数量
SELECT COUNT(*) AS total_tables_created
FROM information_schema.tables
WHERE table_schema = 'public';

-- ============================================================
-- 数据库初始化完成！
-- ============================================================
