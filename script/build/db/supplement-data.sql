-- 补充初始化缺失的数据
-- 执行此脚本前请确保已运行过 DatabaseInitConfig

BEGIN;

-- ==================== 1. 修复商品状态 ====================
UPDATE "product" 
SET status = 1 
WHERE status IS NULL;

-- 验证更新结果
SELECT COUNT(*) as updated_products FROM "product" WHERE status = 1;

-- ==================== 2. 插入 VIP 等级数据 ====================
INSERT INTO vip_level (
    level, name, discount_rate, growth_value_required,
    daily_credit, monthly_credit, exclusive_service,
    free_shipping_count, refund_priority, background_color,
    text_color, icon, description, privileges
) VALUES
(
    1, '普通会员', 0.98, 0,
    5, 50, false,
    0, false, '#f0f0f0',
    '#333333', 'vip-bronze', '基础会员权益', 
    '{"discount": "0.98", "free_shipping": false}'
) ON CONFLICT (level) DO NOTHING;

INSERT INTO vip_level (
    level, name, discount_rate, growth_value_required,
    daily_credit, monthly_credit, exclusive_service,
    free_shipping_count, refund_priority, background_color,
    text_color, icon, description, privileges
) VALUES
(
    2, '白银会员', 0.95, 1000,
    10, 100, false,
    0, false, '#C0C0C0',
    '#333333', 'vip-silver', '享9.5折优惠', 
    '{"discount": "0.95", "monthly_credit": 100}'
) ON CONFLICT (level) DO NOTHING;

INSERT INTO vip_level (
    level, name, discount_rate, growth_value_required,
    daily_credit, monthly_credit, exclusive_service,
    free_shipping_count, refund_priority, background_color,
    text_color, icon, description, privileges
) VALUES
(
    3, '黄金会员', 0.92, 5000,
    20, 200, true,
    1, false, '#FFD700',
    '#000000', 'vip-gold', '享9.2折优惠+免邮', 
    '{"discount": "0.92", "free_shipping": true, "monthly_credit": 200}'
) ON CONFLICT (level) DO NOTHING;

INSERT INTO vip_level (
    level, name, discount_rate, growth_value_required,
    daily_credit, monthly_credit, exclusive_service,
    free_shipping_count, refund_priority, background_color,
    text_color, icon, description, privileges
) VALUES
(
    4, '铂金会员', 0.88, 20000,
    50, 500, true,
    3, true, '#E5E4E2',
    '#000000', 'vip-platinum', '享8.8折+优先发货+优先退款', 
    '{"discount": "0.88", "free_shipping": true, "refund_priority": true}'
) ON CONFLICT (level) DO NOTHING;

INSERT INTO vip_level (
    level, name, discount_rate, growth_value_required,
    daily_credit, monthly_credit, exclusive_service,
    free_shipping_count, refund_priority, background_color,
    text_color, icon, description, privileges
) VALUES
(
    5, '钻石会员', 0.85, 50000,
    100, 1000, true,
    5, true, '#B9F2FF',
    '#000000', 'vip-diamond', '尊享8.5折+专属客服+无限免邮', 
    '{"discount": "0.85", "free_shipping": true, "vip_service": true}'
) ON CONFLICT (level) DO NOTHING;

INSERT INTO vip_level (
    level, name, discount_rate, growth_value_required,
    daily_credit, monthly_credit, exclusive_service,
    free_shipping_count, refund_priority, background_color,
    text_color, icon, description, privileges
) VALUES
(
    6, '黑金会员', 0.80, 100000,
    200, 2000, true,
    10, true, '#1a1a1a',
    '#FFFFFF', 'vip-black', '最高等级尊享8折+私人定制服务', 
    '{"discount": "0.80", "free_shipping": true, "personal_service": true}'
) ON CONFLICT (level) DO NOTHING;

-- 验证插入结果
SELECT level, name, discount_rate FROM vip_level ORDER BY level;

-- ==================== 3. 插入更多商品数据（确保有可用商品） ====================
INSERT INTO product (
    user_id, name, description, category, 
    price, stock, available, status,
    created_at, updated_at, image_url
)
SELECT 1, '华为 Pura 70', '华为旗舰手机，搭载麒麟芯片', 
       '手机', 5999.00, 50, true, 1,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=Pura70'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '华为 Pura 70');

INSERT INTO product (
    user_id, name, description, category, 
    price, stock, available, status,
    created_at, updated_at, image_url
)
SELECT 1, '小米 14 Pro', '骁龙8 Gen3，徕卡影像', 
       '手机', 4299.00, 80, true, 1,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=Mi14Pro'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '小米 14 Pro');

INSERT INTO product (
    user_id, name, description, category, 
    price, stock, available, status,
    created_at, updated_at, image_url
)
SELECT 1, 'iPad Air 6', 'Apple M2 芯片，轻薄便携', 
       '平板', 4799.00, 30, true, 1,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=iPadAir6'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'iPad Air 6');

INSERT INTO product (
    user_id, name, description, category, 
    price, stock, available, status,
    created_at, updated_at, image_url
)
SELECT 1, 'MacBook Pro 14', 'M3 Pro 芯片，18小时续航', 
       '笔记本', 12999.00, 20, true, 1,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=MacBookPro'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'MacBook Pro 14');

INSERT INTO product (
    user_id, name, description, category, 
    price, stock, available, status,
    created_at, updated_at, image_url
)
SELECT 1, 'Sony WH-1000XM5', '业界顶级降噪耳机', 
       '音频', 2399.00, 100, true, 1,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=SonyXM5'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Sony WH-1000XM5');

-- 验证商品总数
SELECT COUNT(*) as total_products FROM "product" WHERE status = 1;

-- ==================== 4. 插入系统公告 ====================
INSERT INTO system_message (
    title, content, type, priority, 
    image_url, jump_url, send_time, 
    created_at, updated_at
) VALUES (
    '系统维护通知', 
    '系统将于2026-05-07 02:00-04:00进行例行维护，请提前做好准备。', 
    'SYSTEM', 1,
    'https://via.placeholder.com/200x100/ff0000/ffffff?text=Maintenance',
    '/announcement/1',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

INSERT INTO system_message (
    title, content, type, priority, 
    image_url, jump_url, send_time, 
    created_at, updated_at
) VALUES (
    '新功能上线', 
    '积分商城功能已正式上线，快来兑换心仪的商品吧！', 
    'ACTIVITY', 2,
    'https://via.placeholder.com/200x100/00ff00/ffffff?text=NewFeature',
    '/integral-mall',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- 验证公告数量
SELECT COUNT(*) as total_messages FROM "system_message";

-- ==================== 5. 更新商品关联的店铺（确保外键有效） ====================
-- 确保 admin (id=1) 有店铺
INSERT INTO shop (
    owner_id, name, description,
    logo, banner, status,
    rating, followers, product_count, positive_rate,
    created_at, updated_at
)
SELECT 1, 'Admin 官方旗舰店', '管理员官方认证店铺',
       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=AdminShop',
       'https://via.placeholder.com/800x200/1a2a4a/00d4ff?text=ShopBanner',
       'ACTIVE', 5.00, 100, 10, 98.5,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM shop WHERE owner_id = 1);

-- 将 admin 的商品关联到店铺
UPDATE product 
SET merchant_id = 1 
WHERE user_id = 1 AND merchant_id IS NULL;

-- 验证
SELECT p.id, p.name, p.merchant_id, s.name as shop_name 
FROM product p 
LEFT JOIN shop s ON p.merchant_id = s.id 
WHERE p.user_id = 1 LIMIT 5;

COMMIT;
\echo '========== 数据补充完成 =========='
