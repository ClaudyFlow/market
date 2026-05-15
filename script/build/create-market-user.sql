-- 创建 market 数据库用户
CREATE USER market WITH PASSWORD 'market';

-- 创建 market 数据库
CREATE DATABASE market OWNER market;

-- 授予权限
GRANT ALL PRIVILEGES ON DATABASE market TO market;
