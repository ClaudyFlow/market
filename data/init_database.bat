@echo off
chcp 65001 >nul
echo ============================================================
echo Market 电商平台 - 数据库初始化脚本
echo ============================================================

set PGUSER=postgres
set PGPASSWORD=你的 postgres 用户密码
set PGHOST=localhost
set PGPORT=5432

set PSQL="C:\Program Files\PostgreSQL\10\bin\psql.exe"

echo.
echo 步骤 1: 创建 admin 用户和 market 数据库...
%PSQL% -U %PGUSER% -h %PGHOST% -p %PGPORT% -f setup_market_db.sql

if %ERRORLEVEL% neq 0 (
    echo 错误：创建数据库失败！
    pause
    exit /b 1
)

echo.
echo 步骤 2: 在 market 数据库中创建表结构...
%PSQL% -U admin -h %PGHOST% -p %PGPORT% -d market -f database.sql

if %ERRORLEVEL% neq 0 (
    echo 错误：创建表结构失败！
    pause
    exit /b 1
)

echo.
echo ============================================================
echo 数据库初始化完成！
echo 数据库名称：market
echo 用户名：admin
echo 密码：123456
echo ============================================================
pause
