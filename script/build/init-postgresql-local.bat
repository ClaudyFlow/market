@echo off
chcp 65001 >nul
title PostgreSQL 本地数据库初始化

echo ========================================
echo   市场平台 - 本地 PostgreSQL 初始化
echo ========================================
echo.
echo 此脚本将:
echo   1. 设置 postgres 用户密码
echo   2. 创建 market 用户和数据库
echo   3. 验证连接
echo.

:: 检查管理员权限
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo [错误] 请以管理员身份运行此脚本！
    pause
    exit /b 1
)

:: 查找 PostgreSQL 18 的 psql
set "PSQL=C:\Program Files\PostgreSQL\18\bin\psql.exe"
if not exist "%PSQL%" (
    set "PSQL=C:\Program Files\PostgreSQL\10\bin\psql.exe"
)

if not exist "%PSQL%" (
    echo [错误] 未找到 psql.exe
    pause
    exit /b 1
)

echo [步骤 1/4] 设置 postgres 用户密码...
echo   注意: 如果 PostgreSQL 是全新安装，postgres 用户可能无密码
echo   这里我们将密码设置为: admin123
echo.

rem 尝试以 trust 模式连接（无需密码）并修改密码
echo   正在连接 PostgreSQL...
"%PSQL%" -U postgres -d postgres -c "ALTER USER postgres WITH PASSWORD 'admin123';" 2>nul
if %errorLevel% equ 0 (
    echo   ✓ postgres 密码已设置为: admin123
) else (
    echo   ! 可能需要先以信任方式连接，请手动执行:
    echo     1. 打开 pg_hba.conf
    echo     2. 将 local 和 host 连接方式改为 "trust"
    echo     3. 重启 PostgreSQL 服务
    echo     4. 再次运行此脚本
)

echo [步骤 2/4] 创建 market 用户和数据库...
echo   执行 SQL: CREATE USER market WITH PASSWORD 'market';
echo   执行 SQL: CREATE DATABASE market OWNER market;
echo.

"%PSQL%" -U postgres -d postgres -f "%~dp0..\..\script\build\init-postgresql.sql" 2>&1

echo [步骤 3/4] 验证 market 用户连接...
"%PSQL%" -U market -d market -c "SELECT '✓ 数据库连接成功' AS result, current_user, version();" 2>&1
if %errorLevel% equ 0 (
    echo   ✓ 验证通过！
) else (
    echo   ✗ 验证失败，请检查:
    echo     - pg_hba.conf 是否允许 md5 认证
    echo     - PostgreSQL 是否监听 localhost:5432
)

echo [步骤 4/4] 检查后端配置...
type "%~dp0..\..\backend\src\main\resources\application-dev.properties" | find "spring.datasource.username=market" >nul
if %errorLevel% equ 0 (
    echo   ✓ 后端配置正确: market/market
) else (
    echo   ✗ 后端配置不匹配，请检查 application-dev.properties
)

echo.
echo ========================================
echo 初始化完成！
echo.
echo 启动后端:
echo   cd backend
echo   mvn spring-boot:run
echo ========================================
pause
