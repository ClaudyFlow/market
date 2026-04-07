@echo off
echo 正在创建 market 数据库用户...

REM 动态查找 psql 路径
where psql >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到 psql 命令
    echo 请确保 PostgreSQL 已安装并添加到系统 PATH
    echo.
    echo 你也可以手动执行以下SQL命令:
    echo.
    echo CREATE USER market WITH PASSWORD 'market';
    echo CREATE DATABASE market OWNER market;
    echo GRANT ALL PRIVILEGES ON DATABASE market TO market;
    echo.
    pause
    exit /b 1
)

psql -U postgres -f "%~dp0\create-market-user.sql"
if %errorlevel% neq 0 (
    echo.
    echo 执行失败! 请确保:
    echo 1. PostgreSQL 服务正在运行
    echo 2. 使用正确的 postgres 用户密码
    echo.
    echo 你也可以手动执行以下SQL命令:
    echo.
    echo CREATE USER market WITH PASSWORD 'market';
    echo CREATE DATABASE market OWNER market;
    echo GRANT ALL PRIVILEGES ON DATABASE market TO market;
    echo.
    pause
    exit /b 1
)
echo.
echo 创建完成!
pause
