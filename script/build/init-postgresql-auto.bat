@echo off
chcp 65001 >nul
title 市场平台数据库初始化（本地 PostgreSQL）

:: 需要管理员权限
:: 此脚本会:
::   1. 停止 PostgreSQL 服务
::   2. 临时将 pg_hba.conf 改为 trust 认证
::   3. 重启 PostgreSQL
::   4. 创建 market 用户和数据库
::   5. 恢复 pg_hba.conf 为 md5 认证

echo ========================================
echo   自动初始化 PostgreSQL 数据库
echo ========================================
echo.

:: 检查管理员权限
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo [错误] 需要管理员权限，请右键"以管理员身份运行"
    pause
    exit /b 1
)

:: 确定 PostgreSQL 版本和路径
set "PG_VERSION=18"
set "PG_DATA=C:\Program Files\PostgreSQL\%PG_VERSION%\data"
set "PG_CTL=C:\Program Files\PostgreSQL\%PG_VERSION%\bin\pg_ctl.exe"
set "PSQL=C:\Program Files\PostgreSQL\%PG_VERSION%\bin\psql.exe"

if not exist "%PG_DATA%" (
    set "PG_VERSION=10"
    set "PG_DATA=C:\Program Files\PostgreSQL\%PG_VERSION%\data"
    set "PG_CTL=C:\Program Files\PostgreSQL\%PG_VERSION%\bin\pg_ctl.exe"
    set "PSQL=C:\Program Files\PostgreSQL\%PG_VERSION%\bin\psql.exe"
)

echo [使用 PostgreSQL %PG_VERSION%]
echo   数据目录: %PG_DATA%
echo.

:: 备份 pg_hba.conf
echo [步骤 1/7] 备份配置文件...
if exist "%PG_DATA%\pg_hba.conf" (
    copy "%PG_DATA%\pg_hba.conf" "%PG_DATA%\pg_hba.conf.bak" >nul
    echo   ✓ 备份 pg_hba.conf
) else (
    echo   ✗ 找不到 pg_hba.conf
    pause
    exit /b 1
)

:: 修改 pg_hba.conf 为 trust
echo [步骤 2/7] 临时启用 trust 认证...
powershell -Command "(Get-Content '%PG_DATA%\pg_hba.conf') -replace 'md5', 'trust' | Set-Content '%PG_DATA%\pg_hba.conf'"
echo   ✓ 已将所有 md5 认证改为 trust

:: 重启 PostgreSQL 服务
echo [步骤 3/7] 重启 PostgreSQL 服务...
net stop postgresql-x64-%PG_VERSION% >nul 2>&1
timeout /t 2 /nobreak >nul
net start postgresql-x64-%PG_VERSION% >nul 2>&1
if %errorLevel% equ 0 (
    echo   ✓ 服务已重启
) else (
    echo   ! 服务重启失败，尝试手动重启...
)

timeout /t 5 /nobreak >nul

:: 执行初始化 SQL
echo [步骤 4/7] 创建 market 用户和数据库...
echo   执行: CREATE USER market WITH PASSWORD 'market';
echo   执行: CREATE DATABASE market OWNER market;
echo.

cd /d "%~dp0..\.."
"%PSQL%" -U postgres -d postgres -f "script\build\init-postgresql.sql" 2>&1

if %errorLevel% neq 0 (
    echo   ✗ SQL 执行失败，尝试简化版本...
    "%PSQL%" -U postgres -d postgres -c "CREATE USER market WITH PASSWORD 'market' CREATEDB;" 2>nul
    "%PSQL%" -U postgres -d postgres -c "CREATE DATABASE market OWNER market;" 2>nul
)

:: 恢复 pg_hba.conf
echo [步骤 5/7] 恢复 md5 认证...
powershell -Command "(Get-Content '%PG_DATA%\pg_hba.conf') -replace 'trust', 'md5' | Set-Content '%PG_DATA%\pg_hba.conf'"
echo   ✓ 已恢复 md5 认证

:: 再次重启
echo [步骤 6/7] 再次重启 PostgreSQL 服务...
net stop postgresql-x64-%PG_VERSION% >nul 2>&1
timeout /t 2 /nobreak >nul
net start postgresql-x64-%PG_VERSION% >nul 2>&1
if %errorLevel% equ 0 (
    echo   ✓ 服务已重启
) else (
    echo   ! 警告: 服务重启失败
)

timeout /t 3 /nobreak >nul

:: 验证连接
echo [步骤 7/7] 验证连接...
set PGPASSWORD=market
"%PSQL%" -U market -d market -c "SELECT '✓ 数据库初始化完成！' AS status, current_user, version();" 2>&1

echo.
echo ========================================
echo 初始化完成！
echo.
echo 接下来可以启动后端:
echo   cd backend
echo   mvn spring-boot:run
echo.
echo 遇到问题?
echo   查看日志: dockerDesktop 或 eventvwr.msc 中的 PostgreSQL 日志
echo ========================================
pause
