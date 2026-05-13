-- PostgreSQL 数据库初始化脚本
-- 用于创建 market 平台的数据库用户和数据库

-- 创建数据库用户 market（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'market') THEN
        CREATE USER market WITH PASSWORD 'market';
        RAISE NOTICE '创建用户: market';
    ELSE
        RAISE NOTICE '用户 market 已存在，跳过创建';
    END IF;
END
$$;

-- 创建 market 数据库（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'market') THEN
        CREATE DATABASE market OWNER market;
        RAISE NOTICE '创建数据库: market';
    ELSE
        RAISE NOTICE '数据库 market 已存在，跳过创建';
    END IF;
END
$$;

-- 授予 market 用户对 market 数据库的所有权限
GRANT ALL PRIVILEGES ON DATABASE market TO market;

-- 连接到 market 数据库执行额外配置
\c market;

-- 启用 UUID 扩展（如果需要）
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 显示完成信息
SELECT '数据库初始化完成！' AS message;
SELECT '用户: market' AS username, '密码: market' AS password, '数据库: market' AS database;
