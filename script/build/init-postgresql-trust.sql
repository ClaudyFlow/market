-- 临时信任本地连接的初始化脚本
-- 用于解决密码未知或认证失败问题

-- 先修改 pg_hba.conf 为 trust 模式（需要管理员权限）
-- 此脚本应在 trust 模式下运行，完成初始化后再改回 md5

-- 创建 market 用户（如果不存在）
CREATE USER IF NOT EXISTS market WITH PASSWORD 'market' CREATEDB;

-- 创建 market 数据库（如果不存在）
SELECT 'CREATE DATABASE market OWNER market'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'market')\gexec

-- 授予权限
GRANT ALL PRIVILEGES ON DATABASE market TO market;

-- 连接到 market 数据库
\c market;

-- 扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 验证
SELECT '数据库初始化成功！' AS status;
SELECT current_user AS current_user, version() AS postgresql_version;
