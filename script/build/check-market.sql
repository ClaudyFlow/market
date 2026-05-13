-- 快速验证脚本：检查 market 用户和数据库
-- 使用方法: psql -U postgres -f check-market.sql

-- 检查用户是否存在
SELECT '检查用户...' AS step;
SELECT rolname FROM pg_roles WHERE rolname = 'market';

-- 检查数据库是否存在
SELECT '检查数据库...' AS step;
SELECT datname FROM pg_database WHERE datname = 'market';

-- 尝试连接 market 数据库
SELECT '尝试连接 market 数据库...' AS step;
SET ROLE market;
SELECT current_user, version();
RESET ROLE;

-- 如果用户或数据库不存在，创建它们
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'market') THEN
        RAISE NOTICE '创建用户 market...';
        EXECUTE 'CREATE USER market WITH PASSWORD ''market'' CREATEDB';
    ELSE
        RAISE NOTICE '用户 market 已存在';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'market') THEN
        RAISE NOTICE '创建数据库 market...';
        EXECUTE 'CREATE DATABASE market OWNER market';
    ELSE
        RAISE NOTICE '数据库 market 已存在';
    END IF;
END
$$;

-- 最终验证
SELECT '最终验证:' AS step;
SELECT 'market 用户可以连接' AS can_connect
WHERE EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'market');

GRANT ALL PRIVILEGES ON DATABASE market TO market;

SELECT '初始化完成' AS status;
