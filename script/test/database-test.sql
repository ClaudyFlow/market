-- 数据库功能完整测试脚本
-- 用于测试数据库连接、建表、增删改查、事务等基本功能
-- 请根据实际数据库调整连接方式，此脚本假设在正确的数据库上下文中执行

SELECT '开始数据库功能测试...' AS message;

-- 测试1: 创建测试表
SELECT '测试1: 创建测试表' AS message;
DROP TABLE IF EXISTS db_test_table;
CREATE TABLE db_test_table (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INTEGER,
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
SELECT '表创建成功' AS message;

-- 测试2: 插入数据
SELECT '测试2: 插入数据' AS message;
INSERT INTO db_test_table (name, age, email, is_active) VALUES 
('张三', 25, 'zhangsan@example.com', TRUE),
('李四', 30, 'lisi@example.com', FALSE),
('王五', 35, 'wangwu@example.com', TRUE);
SELECT '插入了 3 条记录' AS message;

-- 测试3: 查询数据
SELECT '测试3: 查询数据' AS message;
SELECT * FROM db_test_table;

-- 测试4: 更新数据
SELECT '测试4: 更新数据' AS message;
UPDATE db_test_table SET age = 26 WHERE name = '张三';
SELECT '更新张三的年龄' AS message;

-- 测试5: 查询更新后的数据
SELECT '测试5: 查询更新后的数据' AS message;
SELECT * FROM db_test_table WHERE name = '张三';

-- 测试6: 删除数据
SELECT '测试6: 删除数据' AS message;
DELETE FROM db_test_table WHERE name = '李四';
SELECT '删除了李四的记录' AS message;

-- 测试7: 查询剩余数据
SELECT '测试7: 查询剩余数据' AS message;
SELECT COUNT(*) AS remaining_count FROM db_test_table;

-- 测试8: 测试事务 (注意：实际事务测试可能需要在交互中进行)
SELECT '测试8: 测试事务 (注意：实际事务测试可能需要在交互中进行)' AS message;
SELECT '事务测试说明: 在支持事务的数据库中，可测试BEGIN/COMMIT/ROLLBACK' AS message;

-- 测试9: 清理测试表
SELECT '测试9: 清理测试表' AS message;
DROP TABLE db_test_table;
SELECT '测试表已删除' AS message;

SELECT '所有数据库功能测试完成！' AS message;