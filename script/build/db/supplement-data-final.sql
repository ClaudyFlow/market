-- 数据补充脚本 (修复外键顺序)
-- 按依赖顺序执行：先清空购物车，再重建商品

-- ==================== 0. 清理购物车和浏览历史（解除商品外键依赖） ====================
TRUNCATE TABLE "cart_item" CASCADE;
TRUNCATE TABLE "user_browse_history" CASCADE;

-- ==================== 1. 重建商品表 ====================
-- 清空并重新插入商品（确保 status=1）
TRUNCATE TABLE "product" RESTART IDENTITY;

INSERT INTO product (
    user_id, name, description, category, 
    price, stock, available, status,
    created_at, updated_at, image_url
) VALUES
(1, '无线蓝牙耳机', '高品质无线蓝牙耳机，降噪效果好', '数码', 199.00, 100, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=耳机'),
(1, '智能手环',     '运动健康监测，长续航30天',       '数码', 149.00, 100, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=手环'),
(1, '机械键盘',     'Cherry 轴，RGB 背光，热插拔',     '数码', 329.00,  50, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=键盘'),
(1, '华为 Pura 70','麒麟芯片，旗舰影像',             '手机', 5999.00, 50, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=Pura70'),
(1, '小米 14 Pro', '骁龙8 Gen3，徕卡影像',          '手机', 4299.00, 80, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=Mi14Pro'),
(1, 'iPad Air 6',  'Apple M2 芯片，轻薄便携',        '平板', 4799.00, 30, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=iPadAir6');

-- 重置商品ID序列
SELECT setval(pg_get_serial_sequence('product', 'id'), (SELECT MAX(id) FROM product));

-- 验证
SELECT COUNT(*) as total_products, SUM(stock) as total_stock FROM "product" WHERE status = 1;

-- ==================== 2. 插入 VIP 等级 ====================
TRUNCATE TABLE "vip_level" RESTART IDENTITY;

INSERT INTO vip_level (
    level, name, discount_rate, growth_value_required,
    daily_credit, monthly_credit, exclusive_service,
    free_shipping_count, refund_priority, background_color,
    text_color, icon, description, privileges
) VALUES
(1, '普通会员', 0.98, 0,    5,  50,  false, 0, false, '#f0f0f0', '#333333', 'vip-bronze',  '基础会员权益', '[]'),
(2, '白银会员', 0.95, 1000, 10, 100,  false, 0, false, '#C0C0C0', '#333333', 'vip-silver',  '享9.5折优惠', '[]'),
(3, '黄金会员', 0.92, 5000, 20, 200,  true,  1, false, '#FFD700', '#000000', 'vip-gold',    '享9.2折+免邮', '[]'),
(4, '铂金会员', 0.88, 20000, 50, 500,  true,  3,  true, '#E5E4E2', '#000000', 'vip-platinum','享8.8折+优先发货', '[]'),
(5, '钻石会员', 0.85, 50000, 100,1000, true,  5,  true, '#B9F2FF', '#000000', 'vip-diamond', '尊享8.5折+专属客服', '[]'),
(6, '黑金会员', 0.80, 100000, 200,2000, true, 10,  true, '#1a1a1a', '#FFFFFF', 'vip-black',   '最高等级尊享8折', '[]');

SELECT level, name, discount_rate FROM vip_level ORDER BY level;

-- ==================== 3. 清理重复店铺并更新商品商户关联 ====================
-- 删除重复的 admin 店铺（保留 owner_id=1 的那条）
DELETE FROM "shop" 
WHERE id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY name ORDER BY 
            CASE WHEN owner_id = 1 THEN 0 ELSE 1 END, id) as rn
        FROM "shop"
        WHERE name = 'admin的店铺'
    ) t WHERE rn > 1
);

-- 插入/更新 admin 店铺
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
WHERE NOT EXISTS (SELECT 1 FROM shop WHERE owner_id = 1)
RETURNING id;

-- 关联商品到 shop
UPDATE product p
SET merchant_id = s.id
FROM shop s
WHERE p.user_id = 1 AND s.owner_id = 1 AND p.merchant_id IS NULL;

-- 验证
SELECT p.id, p.name, p.merchant_id, s.name as shop_name 
FROM product p 
LEFT JOIN shop s ON p.merchant_id = s.id 
LIMIT 5;

-- ==================== 4. 插入系统公告 ====================
TRUNCATE TABLE "system_message" RESTART IDENTITY;

INSERT INTO system_message (title, content, type, priority, image_url, jump_url, send_time, created_at, updated_at)
VALUES 
('系统维护通知', '系统将于2026-05-07 02:00-04:00进行例行维护，请提前做好准备。', 'SYSTEM', 1, 'https://via.placeholder.com/200x100/ff0000/ffffff?text=Maintenance', '/announcement/1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('新功能上线', '积分商城功能已正式上线，快来兑换心仪的商品吧！', 'ACTIVITY', 2, 'https://via.placeholder.com/200x100/00ff00/ffffff?text=NewFeature', '/integral-mall', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SELECT COUNT(*) as total_messages FROM "system_message";

-- ==================== 5. 插入敏感词 ====================
TRUNCATE TABLE "sensitive_word" RESTART IDENTITY;

INSERT INTO sensitive_word (word, type, level, replacement, created_at) VALUES
('测试', 'COMMON', 1, '*', CURRENT_TIMESTAMP),
('敏感', 'COMMON', 2, '*', CURRENT_TIMESTAMP),
('违规', 'COMMON', 2, '*', CURRENT_TIMESTAMP),
('垃圾', 'ADVANCED', 3, '**', CURRENT_TIMESTAMP),
('广告', 'ADVANCED', 3, '**', CURRENT_TIMESTAMP);

SELECT type, COUNT(*) as count FROM "sensitive_word" GROUP BY type;

-- ==================== 6. 总体验证 ====================
SELECT '商品总数' as item, COUNT(*) as count FROM "product" WHERE status = 1
UNION ALL
SELECT 'VIP等级', COUNT(*) FROM "vip_level"
UNION ALL
SELECT '公告数量', COUNT(*) FROM "system_message"
UNION ALL
SELECT '敏感词数', COUNT(*) FROM "sensitive_word"
UNION ALL
SELECT '店铺数量', COUNT(*) FROM "shop";

\echo '========== 数据补充完成，系统已就绪 =========='
