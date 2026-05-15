@echo off
chcp 936 >nul
title 重置数据库并启动后端

echo ========================================
echo 市场平台 - 数据库重置与启动
echo ========================================
echo.
echo 此脚本将:
echo   1. 完全清空 market 数据库
echo   2. 重新创建所有表结构和索引
echo   3. 插入初始数据（管理员、商品、VIP等级等）
echo   4. 启动 Spring Boot 后端
echo.
echo 警告：所有现有数据将被永久删除！
echo ========================================
echo.

:: 检查管理员权限
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo [错误] 需要管理员权限！
    echo 请右键点击此脚本 -> 以管理员身份运行
    pause
    exit /b 1
)

:: 检查 PostgreSQL 10 是否运行
sc query postgresql-x64-10 | find "RUNNING" >nul
if %errorLevel% neq 0 (
    echo [错误] PostgreSQL 10 未运行！
    echo 请先启动服务: net start postgresql-x64-10
    pause
    exit /b 1
)

echo [1/4] 备份并清理数据库...
echo    SQL: DROP SCHEMA public CASCADE; CREATE SCHEMA public;
echo.

set PGPASSWORD=market
"C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -d market -c "DROP SCHEMA IF EXISTS public CASCADE; CREATE SCHEMA public;" 2>&1

if %errorLevel% neq 0 (
    echo   警告: 清理可能失败，尝试强制清理...
    "C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -d market -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;" 2>&1
)

echo.
echo [2/4] 验证数据库已清空...
"C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -d market -c "\dt" 2>&1 | find "No relations found" >nul
if %errorLevel% equ 0 (
    echo   ✓ 数据库已清空
) else (
    echo   ! 数据库可能还有残留表，继续...
)

echo.
echo [3/4] 启动后端（将自动创建表结构和初始数据）...
echo.
echo   应用将使用 dev profile
echo   数据库: jdbc:postgresql://localhost:5432/market
echo   用户: market/market
echo.
echo   DatabaseInitConfig 会执行:
echo     - 创建 20+ 张表
echo     - 创建索引
echo     - 插入管理员、商品、VIP等级等初始数据
echo.
echo   首次初始化需要 10-30 秒
echo ========================================
echo.

cd /d "%~dp0..\backend"

:: 设置 Maven 选项（避免测试）
set MAVEN_OPTS=-Xmx1024m

mvn spring-boot:run -DskipTests

echo.
echo ========================================
echo 应用已停止
echo ========================================
echo.
echo 验证数据:
echo   psql -U market -d market -c "\dt"
echo   应该看到 20+ 张表
echo.
echo 查看初始用户:
echo   psql -U market -d market -c "SELECT id, name, role FROM \"user\";"
echo.
pause
