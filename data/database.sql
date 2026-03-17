-- ============================================================
-- Market 电商平台 - PostgreSQL 数据库脚本
-- ============================================================
-- 数据库：market
-- 版本：1.0.0
-- 创建日期：2026-03-16
-- ============================================================

-- 创建数据库
-- CREATE DATABASE market;

-- 连接到数据库
-- \c market;

-- ============================================================
-- 1. 用户相关表
-- ============================================================

-- 用户表
CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255),
    credit INTEGER NOT NULL DEFAULT 0,
    total_credit INTEGER NOT NULL DEFAULT 0,
    consumed_credit INTEGER NOT NULL DEFAULT 0,
    vip_level INTEGER NOT NULL DEFAULT 0,
    vip_expire_time TIMESTAMP,
    growth_value INTEGER NOT NULL DEFAULT 0,
    consecutive_checkin_days INTEGER NOT NULL DEFAULT 0,
    last_checkin_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户索引
CREATE INDEX idx_user_name ON "user"(name);
CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_user_vip_level ON "user"(vip_level);

-- 用户地址表
CREATE TABLE user_address (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
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

CREATE INDEX idx_user_address_user_id ON user_address(user_id);

-- 积分历史记录表
CREATE TABLE credit_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    credit_change INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    reason VARCHAR(100) NOT NULL,
    related_order_id VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credit_history_user_id ON credit_history(user_id);
CREATE INDEX idx_credit_history_created_at ON credit_history(created_at);

-- ============================================================
-- 2. 商品相关表
-- ============================================================

-- 商品分类表
CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT REFERENCES category(id) ON DELETE SET NULL,
    level INTEGER NOT NULL DEFAULT 1,
    sort_order INTEGER NOT NULL DEFAULT 0,
    icon_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_category_parent_id ON category(parent_id);
CREATE INDEX idx_category_level ON category(level);

-- 商品表
CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    original_price DECIMAL(10, 2),
    stock INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    images TEXT[],
    category_id BIGINT REFERENCES category(id) ON DELETE SET NULL,
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

CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_category_name ON product(category_name);
CREATE INDEX idx_product_price ON product(price);
CREATE INDEX idx_product_sales ON product(sales_count);
CREATE INDEX idx_product_available ON product(available);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_name ON product(name);

-- 商品规格表
CREATE TABLE product_sku (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    sku_name VARCHAR(100) NOT NULL,
    sku_value VARCHAR(100) NOT NULL,
    price_adjustment DECIMAL(10, 2) DEFAULT 0,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_sku_product_id ON product_sku(product_id);

-- 商品轮播图表
CREATE TABLE product_banner (
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
CREATE TABLE "order" (
    id BIGSERIAL PRIMARY KEY,
    order_no VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE RESTRICT,
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

CREATE INDEX idx_order_user_id ON "order"(user_id);
CREATE INDEX idx_order_order_no ON "order"(order_no);
CREATE INDEX idx_order_status ON "order"(status);
CREATE INDEX idx_order_created_at ON "order"(created_at);

-- 订单项表
CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES "order"(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE RESTRICT,
    product_name VARCHAR(200) NOT NULL,
    product_image VARCHAR(500),
    sku_info VARCHAR(255),
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL
);

CREATE INDEX idx_order_item_order_id ON order_item(order_id);
CREATE INDEX idx_order_item_product_id ON order_item(product_id);

-- 订单物流表
CREATE TABLE order_shipping (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES "order"(id) ON DELETE CASCADE,
    shipping_company VARCHAR(100),
    shipping_no VARCHAR(100),
    shipping_status VARCHAR(50),
    shipped_at TIMESTAMP,
    received_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_shipping_order_id ON order_shipping(order_id);

-- ============================================================
-- 4. 购物车表
-- ============================================================

-- 购物车项表
CREATE TABLE cart_item (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    selected BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, product_id)
);

CREATE INDEX idx_cart_item_user_id ON cart_item(user_id);
CREATE INDEX idx_cart_item_product_id ON cart_item(product_id);

-- ============================================================
-- 5. 收藏与关注表
-- ============================================================

-- 商品收藏表
CREATE TABLE favorite (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, product_id)
);

CREATE INDEX idx_favorite_user_id ON favorite(user_id);
CREATE INDEX idx_favorite_product_id ON favorite(product_id);

-- 店铺关注表
CREATE TABLE follow (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    shop_id BIGINT NOT NULL,
    shop_name VARCHAR(200) NOT NULL,
    shop_avatar VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, shop_id)
);

CREATE INDEX idx_follow_user_id ON follow(user_id);
CREATE INDEX idx_follow_shop_id ON follow(shop_id);

-- ============================================================
-- 6. 评价相关表
-- ============================================================

-- 商品评价表
CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    order_id BIGINT REFERENCES "order"(id) ON DELETE SET NULL,
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

CREATE INDEX idx_review_user_id ON review(user_id);
CREATE INDEX idx_review_product_id ON review(product_id);
CREATE INDEX idx_review_order_id ON review(order_id);
CREATE INDEX idx_review_rating ON review(rating);
CREATE INDEX idx_review_created_at ON review(created_at);

-- 评价图片表
CREATE TABLE review_image (
    id BIGSERIAL PRIMARY KEY,
    review_id BIGINT NOT NULL REFERENCES review(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_review_image_review_id ON review_image(review_id);

-- ============================================================
-- 7. 优惠券相关表
-- ============================================================

-- 优惠券模板表
CREATE TABLE coupon (
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

CREATE INDEX idx_coupon_type ON coupon(type);
CREATE INDEX idx_coupon_active ON coupon(active);
CREATE INDEX idx_coupon_valid_period ON coupon(valid_from, valid_to);

-- 用户优惠券表
CREATE TABLE user_coupon (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    coupon_id BIGINT NOT NULL REFERENCES coupon(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'UNUSED',
    used_at TIMESTAMP,
    order_id BIGINT REFERENCES "order"(id) ON DELETE SET NULL,
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, coupon_id, order_id)
);

CREATE INDEX idx_user_coupon_user_id ON user_coupon(user_id);
CREATE INDEX idx_user_coupon_status ON user_coupon(status);
CREATE INDEX idx_user_coupon_valid_to ON user_coupon(valid_to);

-- ============================================================
-- 8. 商家相关表
-- ============================================================

-- 商家表
CREATE TABLE merchant (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id) ON DELETE SET NULL,
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

CREATE INDEX idx_merchant_user_id ON merchant(user_id);
CREATE INDEX idx_merchant_status ON merchant(status);

-- 商家公告表
CREATE TABLE merchant_announcement (
    id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT NOT NULL REFERENCES merchant(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_top BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_merchant_announcement_merchant_id ON merchant_announcement(merchant_id);

-- ============================================================
-- 9. 管理员相关表
-- ============================================================

-- 管理员表
CREATE TABLE admin (
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
CREATE TABLE system_announcement (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'NOTICE',
    target_user_type VARCHAR(50),
    is_top BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    publish_time TIMESTAMP,
    view_count INTEGER NOT NULL DEFAULT 0,
    created_by BIGINT REFERENCES admin(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_system_announcement_type ON system_announcement(type);
CREATE INDEX idx_system_announcement_active ON system_announcement(is_active);

-- 管理员操作日志表
CREATE TABLE admin_operation_log (
    id BIGSERIAL PRIMARY KEY,
    admin_id BIGINT REFERENCES admin(id) ON DELETE SET NULL,
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

CREATE INDEX idx_admin_operation_log_admin_id ON admin_operation_log(admin_id);
CREATE INDEX idx_admin_operation_log_type ON admin_operation_log(operation_type);
CREATE INDEX idx_admin_operation_log_created_at ON admin_operation_log(created_at);

-- ============================================================
-- 10. 系统配置表
-- ============================================================

-- 系统配置表
CREATE TABLE system_setting (
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
CREATE TABLE verification_code (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_verification_code_email ON verification_code(email);
CREATE INDEX idx_verification_code_expires_at ON verification_code(expires_at);

-- ============================================================
-- 11. 触发器函数
-- ============================================================

-- 更新 user 表的 updated_at 字段
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为需要自动更新 updated_at 的表创建触发器
CREATE TRIGGER update_user_updated_at
    BEFORE UPDATE ON "user"
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER update_product_updated_at
    BEFORE UPDATE ON product
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER update_order_updated_at
    BEFORE UPDATE ON "order"
    FOR EACH ROW
    EXECUTE PROCEDURE update_updated_at_column();

-- ============================================================
-- 12. 初始化数据
-- ============================================================

-- 插入默认管理员账户 (密码：admin123)
INSERT INTO admin (username, password_hash, real_name, role, is_active)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', '系统管理员', 'SUPER_ADMIN', TRUE);

-- 插入默认商品分类
INSERT INTO category (name, parent_id, level, sort_order, is_active) VALUES
('电子产品', NULL, 1, 1, TRUE),
('服装鞋帽', NULL, 1, 2, TRUE),
('家居生活', NULL, 1, 3, TRUE),
('美食生鲜', NULL, 1, 4, TRUE),
('图书文娱', NULL, 1, 5, TRUE),
('手机数码', 1, 2, 1, TRUE),
('电脑办公', 1, 2, 2, TRUE),
('男装', 2, 2, 1, TRUE),
('女装', 2, 2, 2, TRUE),
('家具家装', 3, 2, 1, TRUE);

-- 插入系统配置
INSERT INTO system_setting (setting_key, setting_value, setting_type, description, is_public) VALUES
('site_name', 'Market 电商平台', 'STRING', '网站名称', TRUE),
('site_logo', '/images/logo.png', 'STRING', '网站 Logo', TRUE),
('default_avatar', '/images/default-avatar.png', 'STRING', '默认头像', TRUE),
('checkin_base_credit', '10', 'INTEGER', '签到基础积分', TRUE),
('checkin_extra_credit', '5', 'INTEGER', '连续签到额外积分', TRUE),
('order_credit_rate', '0.01', 'DECIMAL', '订单积分返还比例', FALSE),
('free_shipping_threshold', '99.00', 'DECIMAL', '包邮门槛', TRUE),
('max_cart_items', '100', 'INTEGER', '购物车最大商品数', TRUE);

-- 插入示例优惠券
INSERT INTO coupon (name, type, discount_value, min_purchase, valid_from, valid_to, total_count, per_user_limit, active, description) VALUES
('新人优惠券', 'FIXED', 20.00, 100.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 1000, 1, TRUE, '新用户注册专享'),
('满减优惠券', 'FIXED', 50.00, 300.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '15 days', 500, 2, TRUE, '全场通用'),
('折扣优惠券', 'PERCENT', 0.9, 50.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '7 days', 200, 1, TRUE, '9 折优惠');

-- ============================================================
-- 13. 视图
-- ============================================================

-- 商品统计视图
CREATE VIEW product_stats_view AS
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
FROM product p
LEFT JOIN category c ON p.category_id = c.id
LEFT JOIN favorite f ON p.id = f.product_id
GROUP BY p.id, p.name, p.price, p.sales_count, p.rating_avg, p.rating_count, p.stock, p.available, c.name;

-- 用户订单统计视图
CREATE VIEW user_order_stats_view AS
SELECT
    u.id AS user_id,
    u.name AS user_name,
    COUNT(DISTINCT o.id) AS total_orders,
    COALESCE(SUM(o.actual_amount), 0) AS total_spent,
    COUNT(DISTINCT CASE WHEN o.status = 'COMPLETED' THEN o.id END) AS completed_orders,
    COUNT(DISTINCT CASE WHEN o.status = 'PENDING' THEN o.id END) AS pending_orders
FROM "user" u
LEFT JOIN "order" o ON u.id = o.user_id
GROUP BY u.id, u.name;

-- ============================================================
-- 14. 注释
-- ============================================================

COMMENT ON TABLE "user" IS '用户表 - 存储用户基本信息、积分、VIP 状态等';
COMMENT ON TABLE product IS '商品表 - 存储商品信息、价格、库存等';
COMMENT ON TABLE "order" IS '订单表 - 存储订单主表信息';
COMMENT ON TABLE order_item IS '订单项表 - 存储订单中的商品明细';
COMMENT ON TABLE cart_item IS '购物车项表 - 存储用户购物车商品';
COMMENT ON TABLE favorite IS '商品收藏表 - 存储用户收藏的商品';
COMMENT ON TABLE follow IS '店铺关注表 - 存储用户关注的店铺';
COMMENT ON TABLE review IS '商品评价表 - 存储用户对商品的评价';
COMMENT ON TABLE coupon IS '优惠券模板表 - 存储优惠券发行信息';
COMMENT ON TABLE user_coupon IS '用户优惠券表 - 存储用户领取的优惠券';
COMMENT ON TABLE credit_history IS '积分历史记录表 - 存储用户积分变动记录';
COMMENT ON TABLE merchant IS '商家表 - 存储入驻商家信息';
COMMENT ON TABLE admin IS '管理员表 - 存储后台管理员信息';
