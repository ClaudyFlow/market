@echo off
chcp 936 >nul
title 启动市场平台后端（本地模式）

echo ========================================
echo 市场平台后端 - 本地启动
echo ========================================
echo.

echo [步骤 1/4] 检查 PostgreSQL 服务...
sc query postgresql-x64-18 | find "RUNNING" >nul
if %errorLevel% equ 0 (
    echo   PostgreSQL 18 正在运行
    set "PSQL=C:\Program Files\PostgreSQL\18\bin\psql.exe"
    set "PG_VERSION=18"
) else (
    sc query postgresql-x64-10 | find "RUNNING" >nul
    if %errorLevel% equ 0 (
        echo   PostgreSQL 10 正在运行
        set "PSQL=C:\Program Files\PostgreSQL\10\bin\psql.exe"
        set "PG_VERSION=10"
    ) else (
        echo   错误: PostgreSQL 未运行！
        echo   请先启动服务：
        echo     net start postgresql-x64-18
        echo   或
        echo     net start postgresql-x64-10
        pause
        exit /b 1
    )
)

echo [步骤 2/4] 验证 market 数据库连接...
echo    测试用户: market / market
echo.

set PGPASSWORD=market
%PSQL% -U market -d market -c "SELECT current_user, version();" 2>&1 | find "market" >nul
if %errorLevel% equ 0 (
    echo   ✓ 数据库连接成功
) else (
    echo   ✗ 连接失败，尝试创建用户和数据库...
    echo.
    echo   请在新终端以管理员身份运行以下命令：
    echo     cd /d D:\market
    echo     script\build\init-postgresql-local.bat
    echo.
    echo   或手动执行 SQL:
    echo     psql -U postgres -c "CREATE USER market WITH PASSWORD 'market' CREATEDB;"
    echo     psql -U postgres -c "CREATE DATABASE market OWNER market;"
    echo.
    pause
    exit /b 1
)

echo [步骤 3/4] 检查后端配置...
type backend\src\main\resources\application-dev.properties | find "spring.datasource.username=market" >nul
if %errorLevel% equ 0 (
    echo   ✓ 配置文件正确 (market/market)
) else (
    echo   ✗ 配置文件不匹配
    pause
    exit /b 1
)

echo [步骤 4/4] 启动 Spring Boot 应用...
echo.
echo   注意: Redis 和 RabbitMQ 未启动不影响核心功能
echo         （它们被配置为可选服务）
echo.
echo   首次启动会下载依赖，可能需要 2-5 分钟...
echo.
echo   按 Ctrl+C 停止服务器
echo ========================================
echo.

cd backend
mvn spring-boot:run

pause
