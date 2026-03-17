-- ============================================================
-- PostgreSQL 数据库连接测试脚本 (适用于 pgAdmin)
-- ============================================================
-- 使用方法：
-- 1. 在 pgAdmin 中打开查询工具
-- 2. 选择目标数据库
-- 3. 粘贴并执行此脚本
-- ============================================================

-- ==================== 1. 基础连接测试 ====================

-- 测试 1: 检查数据库连接
SELECT current_database() AS database_name,
       current_user AS user_name,
       version() AS postgresql_version;

-- 测试 2: 检查所有表是否创建成功
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- ==================== 2. 表结构测试 ====================

-- 测试 3: 查看 user 表结构
SELECT
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'user'
ORDER BY ordinal_position;

-- 测试 4: 查看所有表的记录数
SELECT 'user' AS table_name, COUNT(*) AS row_count FROM user
UNION ALL
SELECT 'product', COUNT(*) FROM product
UNION ALL
SELECT 'order', COUNT(*) FROM order
UNION ALL
SELECT 'order_item', COUNT(*) FROM order_item
UNION ALL
SELECT 'cart_item', COUNT(*) FROM cart_item
UNION ALL
SELECT 'favorite', COUNT(*) FROM favorite
UNION ALL
SELECT 'follow', COUNT(*) FROM follow
UNION ALL
SELECT 'review', COUNT(*) FROM review
UNION ALL
SELECT 'coupon', COUNT(*) FROM coupon
UNION ALL
SELECT 'category', COUNT(*) FROM category
UNION ALL
SELECT 'admin', COUNT(*) FROM admin;

-- ==================== 3. 数据插入测试 ====================

-- 测试 5: 插入测试用户
INSERT INTO user (name, email, password_hash, credit, vip_level)
VALUES (
    'test_user_' || EXTRACT(EPOCH FROM NOW())::INTEGER,
    'test' || EXTRACT(EPOCH FROM NOW())::INTEGER || '@example.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS',
    100,
    1
) RETURNING id, name, email, created_at;

-- 测试 6: 插入测试分类
INSERT INTO category (name, level, sort_order)
VALUES ('测试分类', 1, 999)
RETURNING id, name, created_at;

-- 测试 7: 插入测试商品
INSERT INTO product (name, description, price, stock, category_name, available)
VALUES (
    '测试商品 ' || EXTRACT(EPOCH FROM NOW())::INTEGER,
    '这是一个测试商品',
    99.99,
    100,
    '测试分类',
    TRUE
) RETURNING id, name, price, created_at;

-- ==================== 4. 关联查询测试 ====================

-- 测试 8: 测试用户 - 订单关联查询
SELECT
    u.id AS user_id,
    u.name AS user_name,
    COUNT(o.id) AS order_count,
    COALESCE(SUM(o.total_amount), 0) AS total_spent
FROM user u
LEFT JOIN order o ON u.id = o.user_id
GROUP BY u.id, u.name
ORDER BY u.id DESC
LIMIT 5;

-- 测试 9: 测试商品 - 收藏关联查询
SELECT
    p.id AS product_id,
    p.name AS product_name,
    p.price,
    COUNT(f.id) AS favorite_count
FROM product p
LEFT JOIN favorite f ON p.id = f.product_id
GROUP BY p.id, p.name, p.price
ORDER BY favorite_count DESC
LIMIT 5;

-- 测试 10: 测试商品 - 评价关联查询
SELECT
    p.id AS product_id,
    p.name AS product_name,
    COUNT(r.id) AS review_count,
    COALESCE(AVG(r.rating), 0) AS avg_rating
FROM product p
LEFT JOIN review r ON p.id = r.product_id
GROUP BY p.id, p.name
ORDER BY review_count DESC
LIMIT 5;

-- ==================== 5. 视图测试 ====================

-- 测试 11: 测试商品统计视图
SELECT * FROM product_stats_view LIMIT 5;

-- 测试 12: 测试用户订单统计视图
SELECT * FROM user_order_stats_view LIMIT 5;

-- ==================== 6. 索引测试 ====================

-- 测试 13: 查看所有索引
SELECT
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY tablename, indexname;

-- ==================== 7. 触发器测试 ====================

-- 测试 14: 查看触发器
SELECT
    trigger_name,
    event_manipulation AS event,
    event_object_table AS table_name,
    action_timing AS timing,
    action_statement AS definition
FROM information_schema.triggers
WHERE trigger_schema = 'public'
ORDER BY event_object_table, trigger_name;

-- ==================== 8. 性能测试 ====================

-- 测试 15: 查询执行计划分析
-- 注意：在 pgAdmin 中，EXPLAIN ANALYZE 会执行查询并显示计划
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM user WHERE name = 'admin';

EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM product WHERE available = TRUE AND category_name = '电子产品';

-- ==================== 9. 清理测试数据 ====================

-- 清理测试数据（可选执行）
-- 注意：如果不需要保留测试数据，可以取消注释以下语句
/*
-- 删除关联数据（注意外键约束顺序）
DELETE FROM cart_item WHERE user_id IN (SELECT id FROM user WHERE name LIKE 'test_user_%');
DELETE FROM favorite WHERE user_id IN (SELECT id FROM user WHERE name LIKE 'test_user_%');
DELETE FROM follow WHERE user_id IN (SELECT id FROM user WHERE name LIKE 'test_user_%');
DELETE FROM review WHERE user_id IN (SELECT id FROM user WHERE name LIKE 'test_user_%');
DELETE FROM order WHERE user_id IN (SELECT id FROM user WHERE name LIKE 'test_user_%');
DELETE FROM product WHERE category_name = '测试分类';
DELETE FROM category WHERE name = '测试分类';
DELETE FROM user WHERE name LIKE 'test_user_%';

-- 清理场景测试数据
DELETE FROM cart_item WHERE user_id IN (SELECT id FROM user WHERE name = 'scenario_test_user');
DELETE FROM favorite WHERE user_id IN (SELECT id FROM user WHERE name = 'scenario_test_user');
DELETE FROM review WHERE user_id IN (SELECT id FROM user WHERE name = 'scenario_test_user');
DELETE FROM order_item WHERE order_id IN (SELECT id FROM order WHERE user_id IN (SELECT id FROM user WHERE name = 'scenario_test_user'));
DELETE FROM order WHERE user_id IN (SELECT id FROM user WHERE name = 'scenario_test_user');
DELETE FROM product WHERE name LIKE '场景测试商品%';
DELETE FROM user WHERE name = 'scenario_test_user';
*/

-- ==================== 10. 完整功能测试场景 ====================

-- 场景测试：用户注册 -> 浏览商品 -> 加入购物车 -> 下单 -> 评价
-- 注意：如果表名或字段名与实际情况不符，请相应调整
DO $$
DECLARE
    test_user_id BIGINT;
    test_product_id BIGINT;
    test_order_id BIGINT;
    test_category_name VARCHAR(50) := '电子产品'; -- 请确保此分类存在
BEGIN
    -- 创建用户
    INSERT INTO user (name, email, password_hash, credit)
    VALUES ('scenario_test_user', 'scenario@test.com', 'hash123', 0)
    RETURNING id INTO test_user_id;

    -- 创建商品（确保分类存在）
    INSERT INTO product (name, price, stock, category_name)
    VALUES ('场景测试商品', 199.99, 50, test_category_name)
    RETURNING id INTO test_product_id;

    -- 加入购物车
    INSERT INTO cart_item (user_id, product_id, quantity)
    VALUES (test_user_id, test_product_id, 2);

    -- 创建订单
    INSERT INTO order (order_no, user_id, total_amount, actual_amount, status)
    VALUES ('ORD' || EXTRACT(EPOCH FROM NOW())::TEXT, test_user_id, 399.98, 399.98, 'PENDING')
    RETURNING id INTO test_order_id;

    -- 添加订单项
    INSERT INTO order_item (order_id, product_id, product_name, quantity, price, subtotal)
    VALUES (test_order_id, test_product_id, '场景测试商品', 2, 199.99, 399.98);

    -- 创建评价
    INSERT INTO review (user_id, product_id, order_id, rating, content, user_name, product_name, product_price)
    VALUES (test_user_id, test_product_id, test_order_id, 5, '很好的商品！', 'scenario_test_user', '场景测试商品', 199.99);

    RAISE NOTICE '场景测试完成！用户 ID: %, 商品 ID: %, 订单 ID: %', test_user_id, test_product_id, test_order_id;
END $$;

-- 查看场景测试结果
SELECT
    u.name AS user_name,
    p.name AS product_name,
    o.order_no,
    o.status,
    r.rating,
    r.content
FROM user u
JOIN order o ON u.id = o.user_id
JOIN order_item oi ON o.id = oi.order_id
JOIN product p ON oi.product_id = p.id
LEFT JOIN review r ON o.id = r.order_id
WHERE u.name = 'scenario_test_user';

-- ============================================================
-- 测试完成！
-- ============================================================
