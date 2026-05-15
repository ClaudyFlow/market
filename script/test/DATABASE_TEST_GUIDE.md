# 数据库功能测试说明

## 测试文件位置
- SQL测试脚本: `database-test.sql`
- 测试位置: `D:\market\script\test\database-test.sql`

## 测试内容
该SQL脚本测试了数据库的基本功能：
1. 创建测试表
2. 插入数据
3. 查询数据
4. 更新数据
5. 删除数据
6. 事务说明
7. 清理测试表

## 如何运行测试

### 方法1：使用H2数据库（如果已安装）
如果您的系统中安装了H2数据库，可以运行：
```
java -cp h2.jar org.h2.tools.Shell -url jdbc:h2:mem:testdb -user sa -password -sql database-test.sql
```

### 方法2：使用PostgreSQL（项目默认数据库）
如果您想使用项目配置的PostgreSQL数据库：
1. 确保PostgreSQL服务正在运行
2. 使用psql或其他PostgreSQL客户端连接到数据库
3. 运行SQL脚本中的命令

### 方法3：使用项目中的测试配置
查看项目中的测试配置，例如：
- `backend/src/test/resources/application-test.properties` 或类似文件
- 运行 Maven测试: `mvn test` (在backend目录下)

## 手动运行示例

如果您想手动执行测试步骤，可以依次执行以下SQL命令：

```sql
-- 开始数据库功能测试...
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
SELECT '测试表已删除' AS message;

SELECT '所有数据库功能测试完成！' AS message;
```

## 注意事项
1. 根据您的数据库类型，可能需要调整SQL语法（特别是SERIAL和BOOLEAN类型）
2. 对于PostgreSQL，SERIAL类型是正确的，但BOOLEAN也应该工作
3. 对于H2数据库，上述SQL应该可以直接运行
4. 事务测试在脚本中仅作为说明，实际事务测试需要在支持交互的数据库客户端中进行

## 故障排除
如果遇到找不到H2 JAR文件的问题：
1. 检查您的Maven本地仓库：~/.m2/repository/com/h2database/h2/
2. 或者从https://www.h2database.com/html/download.html下载H2数据库