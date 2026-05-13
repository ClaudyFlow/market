-- 数据库完整性检查脚本
-- 执行时间：2026-05-06
-- 目的：验证所有关键表的数据完整性

\echo '========== 1. 用户表检查 =========='
SELECT id, name, email, is_merchant, status, vip_level 
FROM "user" 
ORDER BY id;

\echo '========== 2. 商品表检查 =========='
SELECT id, name, price, stock, available, status, merchant_id 
FROM "product" 
ORDER BY id;

\echo '========== 3. VIP 等级表检查 =========='
SELECT * FROM "vip_level" ORDER BY level;

\echo '========== 4. 抽奖奖品表检查 =========='
SELECT id, name, type, weight, available, stock, total_stock 
FROM "lottery_prize" 
ORDER BY id;

\echo '========== 5. 店铺表检查 =========='
SELECT id, name, owner_id, status, rating FROM "shop" ORDER BY id;

\echo '========== 6. 敏感词表检查 =========='
SELECT COUNT(*) as total_words FROM "sensitive_word";
SELECT type, COUNT(*) as count FROM "sensitive_word" GROUP BY type;

\echo '========== 7. 公告表检查 =========='
SELECT COUNT(*) as total_announcements FROM "system_message";
SELECT type, COUNT(*) FROM "system_message" GROUP BY type;

\echo '========== 8. 外键完整性检查 =========='
-- 检查商品关联的商户是否存在
SELECT p.id, p.name, p.merchant_id, u.name as merchant_name
FROM "product" p
LEFT JOIN "user" u ON p.merchant_id = u.id
WHERE p.merchant_id IS NOT NULL AND u.id IS NULL;

-- 检查店铺关联的商户是否存在
SELECT s.id, s.name, s.owner_id, u.name as owner_name
FROM "shop" s
LEFT JOIN "user" u ON s.owner_id = u.id
WHERE u.id IS NULL;
