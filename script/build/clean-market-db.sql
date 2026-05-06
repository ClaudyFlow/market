-- 完全清理 market 数据库（慎用！）
-- 此脚本会删除数据库中所有表和数据

\connect market;

-- 禁用外键检查（用于完全清空）
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

-- 重置序列
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

-- 确认清理完成
SELECT '数据库已完全清空' AS status;
