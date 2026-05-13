-- ============================================================
-- Market 数据库重置脚本
-- 完全清空数据库并重新初始化
-- 使用方式：psql -U postgres -f reset-market-db.sql
-- ============================================================

-- 连接到 market 数据库
\c market market;

-- 禁用外键检查
SET session_replication_role = 'replica';

-- 删除所有表
DO $$
DECLARE
    row record;
BEGIN
    FOR row IN
        SELECT tablename FROM pg_tables
        WHERE schemaname = 'public'
        AND tablename NOT LIKE 'flyway%'
        AND tablename NOT LIKE 'schema_versions%'
    LOOP
        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(row.tablename) || ' CASCADE';
    END LOOP;
END
$$;

-- 恢复外键检查
SET session_replication_role = 'origin';

-- 重置所有序列
DO $$
DECLARE
    row record;
BEGIN
    FOR row IN
        SELECT sequence_name FROM information_schema.sequences
        WHERE sequence_schema = 'public'
    LOOP
        EXECUTE 'ALTER SEQUENCE ' || quote_ident(row.sequence_name) || ' RESTART WITH 1';
    END LOOP;
END
$$;

-- 显示完成
SELECT '数据库已完全重置，准备重新初始化...' AS status;

-- 执行初始化
\i init-market-db.sql