@echo off
chcp 936 >nul
title 清理并重新初始化数据库

echo ========================================
echo 警告：此操作将删除 market 数据库中的所有数据！
echo ========================================
echo.
echo 将要执行的操作:
echo   1. 备份当前数据库（可选）
echo   2. 删除所有表、索引、序列
echo   3. 重新创建表结构和索引
echo   4. 插入初始数据（管理员、商品、VIP等级等）
echo.
set /p confirm="确认继续？输入 YES 继续: "
if /i not "%confirm%"=="YES" (
    echo 操作已取消
    pause
    exit /b 0
)

echo.
echo [步骤 1/3] 清理数据库...
echo   执行清理脚本...
"C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -d market -f "..\..\script\build\clean-market-db.sql" 2>&1

if %errorLevel% neq 0 (
    echo 警告: 清理脚本可能失败，尝试直接DROP...
    "C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -d market -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;" 2>nul
)

echo.
echo [步骤 2/3] 重新初始化（通过启动后端自动执行）...
echo   DatabaseInitConfig 将在应用启动时创建表结构和初始数据
echo.
echo   注意：将使用 dev profile，配置为 market/market
echo.
echo [步骤 3/3] 启动应用进行初始化...
cd /d "%~dp0..\backend"

echo   执行: mvn spring-boot:run
echo.
echo   如果看到 "Started MarketApplication in XXX seconds" 表示成功
echo.
echo   按 Ctrl+C 停止服务器（初始化完成后）
echo ========================================
echo.

mvn spring-boot:run

echo.
echo ========================================
echo 初始化完成！
echo 可以验证数据:
echo   psql -U market -d market -c "\dt"
echo ========================================
pause
