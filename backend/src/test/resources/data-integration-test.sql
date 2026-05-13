-- 联调测试数据库初始化脚本

-- 插入测试用户
INSERT INTO users (id, username, password, email, phone, nickname, avatar, role, status, created_at, updated_at)
VALUES 
(1, 'testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'test@example.com', '13800138000', '测试用户', 'https://example.com/avatar.jpg', 'USER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'merchant1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'merchant@example.com', '13800138001', '测试商家', 'https://example.com/merchant.jpg', 'MERCHANT', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@example.com', '13800138002', '管理员', 'https://example.com/admin.jpg', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入测试商品分类
INSERT INTO categories (id, name, description, icon, sort_order, status, created_at, updated_at)
VALUES 
(1, '数码产品', '手机、电脑、平板等数码产品', '📱', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '服饰鞋包', '服装、鞋子、箱包等', '👕', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '图书文具', '图书、教材、文具等', '📚', 3, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入测试商品
INSERT INTO products (id, title, description, price, original_price, stock, category_id, seller_id, status, audit_status, created_at, updated_at)
VALUES 
(1, 'iPhone 14 Pro', '99新，使用一年，配件齐全', 5999.00, 8999.00, 1, 1, 2, 'ON_SALE', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'MacBook Air M2', '全新未拆封，教育优惠购买', 7999.00, 9499.00, 1, 1, 2, 'ON_SALE', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Nike运动鞋', '42码，全新带吊牌', 399.00, 699.00, 5, 2, 2, 'ON_SALE', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入测试商品图片
INSERT INTO product_images (id, product_id, image_url, is_primary, sort_order, created_at)
VALUES 
(1, 1, 'https://example.com/iphone1.jpg', true, 1, CURRENT_TIMESTAMP),
(2, 1, 'https://example.com/iphone2.jpg', false, 2, CURRENT_TIMESTAMP),
(3, 2, 'https://example.com/macbook1.jpg', true, 1, CURRENT_TIMESTAMP),
(4, 3, 'https://example.com/nike1.jpg', true, 1, CURRENT_TIMESTAMP);

-- 插入测试店铺
INSERT INTO shops (id, name, description, logo, seller_id, status, created_at, updated_at)
VALUES 
(1, '测试数码店', '专业二手数码销售', 'https://example.com/shop1.jpg', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入测试优惠券
INSERT INTO coupons (id, name, description, type, discount, min_purchase, total_count, used_count, start_time, end_time, status, created_at, updated_at)
VALUES 
(1, '新用户专享券', '满100减20', 'FIXED', 20.00, 100.00, 1000, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '数码品类券', '数码产品满1000减100', 'FIXED', 100.00, 1000.00, 500, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' DAY, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
