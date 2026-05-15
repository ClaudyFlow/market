-- 数据补充最终版 V2（匹配实际表结构 + 正确外键）

-- ==================== 0. 清空所有数据 ====================
TRUNCATE TABLE cart_item CASCADE;
TRUNCATE TABLE user_browse_history CASCADE;
TRUNCATE TABLE order_item CASCADE;
TRUNCATE TABLE "order" CASCADE;
TRUNCATE TABLE product CASCADE;
TRUNCATE TABLE vip_level CASCADE;
TRUNCATE TABLE shop CASCADE;
TRUNCATE TABLE system_message CASCADE;
TRUNCATE TABLE sensitive_word CASCADE;

-- ==================== 1. 插入 VIP 等级 ====================
INSERT INTO vip_level (
    level, name, discount_rate, growth_value_required,
    daily_credit, monthly_credit, exclusive_service,
    free_shipping_count, refund_priority, background_color,
    text_color, icon, description, privileges
) VALUES
(1, '普通会员', 0.98, 0,    5,  50,  false, 0, false, '#f0f0f0',    '#333333', 'vip-bronze',  '基础会员权益', '[]'),
(2, '白银会员', 0.95, 1000, 10, 100,  false, 0, false, '#C0C0C0',    '#333333', 'vip-silver',  '享9.5折优惠', '[]'),
(3, '黄金会员', 0.92, 5000, 20, 200,  true,  1, false, '#FFD700',    '#000000', 'vip-gold',    '享9.2折+免邮', '[]'),
(4, '铂金会员', 0.88, 20000, 50, 500,  true,  3,  true, '#E5E4E2',   '#000000', 'vip-platinum','享8.8折+优先发货', '[]'),
(5, '钻石会员', 0.85, 50000, 100,1000, true,  5,  true, '#B9F2FF',   '#000000', 'vip-diamond', '尊享8.5折+专属客服', '[]'),
(6, '黑金会员', 0.80, 100000, 200,2000, true, 10,  true, '#1a1a1a',   '#FFFFFF', 'vip-black',   '最高等级尊享8折', '[]');

-- ==================== 2. 插入商品 ====================
-- 明确指定 merchant_id = NULL (后续再更新)
INSERT INTO product (
    user_id, name, description, category, 
    price, stock, available, status,
    created_at, updated_at, image_url, merchant_id
) VALUES
(1, '无线蓝牙耳机', '高品质无线蓝牙耳机，降噪效果好', '数码', 199.00, 100, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=耳机', NULL),
(1, '智能手环',     '运动健康监测，长续航30天',       '数码', 149.00, 100, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=手环', NULL),
(1, '机械键盘',     'Cherry 轴，RGB 背光，热插拔',     '数码', 329.00,  50, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=键盘', NULL),
(1, '华为 Pura 70','麒麟芯片，旗舰影像',             '手机', 5999.00, 50, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=Pura70', NULL),
(1, '小米 14 Pro', '骁龙8 Gen3，徕卡影像',          '手机', 4299.00, 80, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=Mi14Pro', NULL),
(1, 'iPad Air 6',  'Apple M2 芯片，轻薄便携',        '平板', 4799.00, 30, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=iPadAir6', NULL);

-- ==================== 3. 插入店铺 ====================
INSERT INTO shop (
    owner_id, name, description,
    logo, banner, status,
    rating, followers, product_count, positive_rate,
    created_at, updated_at
)
VALUES 
(1, 'Admin 官方旗舰店', '管理员官方认证店铺',
 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=AdminShop',
 'https://via.placeholder.com/800x200/1a2a4a/00d4ff?text=ShopBanner',
 'ACTIVE', 5.00, 100, 10, 98.5,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 将商品的 merchant_id 设置为发布者的 user_id (即 user_id 本身)
-- 因为 merchant_id 引用的是 user 表，而不是 shop 表
UPDATE product p
SET merchant_id = p.user_id
WHERE p.merchant_id IS NULL;

-- ==================== 4. 插入系统公告 ====================
INSERT INTO system_message (
    title, content, type, priority, 
    image_url, jump_url, send_time, 
    is_broadcast, created_at
) VALUES 
('系统维护通知', 
 '系统将于2026-05-07 02:00-04:00进行例行维护，请提前做好准备。', 
 'SYSTEM', 1,
 'https://via.placeholder.com/200x100/ff0000/ffffff?text=Maintenance',
 '/announcement/1',
 CURRENT_TIMESTAMP,
 true, CURRENT_TIMESTAMP),
('新功能上线', 
 '积分商城功能已正式上线，快来兑换心仪的商品吧！', 
 'ACTIVITY', 2,
 'https://via.placeholder.com/200x100/00ff00/ffffff?text=NewFeature',
 '/integral-mall',
 CURRENT_TIMESTAMP,
 true, CURRENT_TIMESTAMP);

-- ==================== 5. 插入敏感词 ====================
INSERT INTO sensitive_word (
    word, type, level, replacement, enabled, match_count, created_at
) VALUES
('测试', 'COMMON', 1, '*', true, 0, CURRENT_TIMESTAMP),
('敏感', 'COMMON', 2, '*', true, 0, CURRENT_TIMESTAMP),
('违规', 'COMMON', 2, '*', true, 0, CURRENT_TIMESTAMP),
('垃圾', 'ADVANCED', 3, '**', true, 0, CURRENT_TIMESTAMP),
('广告', 'ADVANCED', 3, '**', true, 0, CURRENT_TIMESTAMP);

-- ==================== 验证 ====================
SELECT '商品总数' as item, COUNT(*) as count FROM product WHERE status = 1
UNION ALL SELECT 'VIP等级', COUNT(*) FROM vip_level
UNION ALL SELECT '店铺', COUNT(*) FROM shop
UNION ALL SELECT '公告', COUNT(*) FROM system_message
UNION ALL SELECT '敏感词', COUNT(*) FROM sensitive_word;

-- 验证商品关联
SELECT p.id, p.name, p.merchant_id, s.name as shop_name 
FROM product p LEFT JOIN shop s ON p.merchant_id = s.id 
LIMIT 3;

-- 验证 VIP 等级
SELECT level, name, discount_rate FROM vip_level ORDER BY level;
