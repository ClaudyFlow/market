@echo off
title Start Market Platform Backend (Local PostgreSQL)

echo ========================================
echo Starting Market Platform Backend
echo ========================================
echo.

:: 检查 PostgreSQL 是否运行
echo [1/4] Checking PostgreSQL service...
sc query postgresql-x64-18 | find "RUNNING" >nul
if %errorLevel% equ 0 (
    echo   Found PostgreSQL 18 - running
    set PG_VERSION=18
) else (
    sc query postgresql-x64-10 | find "RUNNING" >nul
    if %errorLevel% equ 0 (
        echo   Found PostgreSQL 10 - running
        set PG_VERSION=10
    ) else (
        echo   ERROR: PostgreSQL not running!
        echo   Start it first: net start postgresql-x64-18
        pause
        exit /b 1
    )
)

:: 检查数据库和用户是否存在
echo.
echo [2/4] Checking database user 'market'...
echo    If user does not exist, we will create it.
echo.

set PSQL="C:\Program Files\PostgreSQL\%PG_VERSION%\bin\psql.exe"

:: 尝试连接并创建用户/数据库（使用 postgres 用户，可能需要密码）
echo    Attempting to connect as postgres user...
echo    Note: You may need to enter postgres password if configured.
echo.

%PSQL% -U postgres -d postgres -c "CREATE USER market WITH PASSWORD 'market' CREATEDB;" 2>nul
%PSQL% -U postgres -d postgres -c "CREATE DATABASE market OWNER market;" 2>nul

echo [3/4] Verifying market user...
%PSQL% -U market -d market -c "SELECT 'OK' AS status;" 2>&1 | find "OK" >nul
if %errorLevel% equ 0 (
    echo   Database connection: OK
) else (
    echo   WARNING: Connection as market failed.
    echo   Trying with explicit password...
    set PGPASSWORD=market
    %PSQL% -U market -d market -c "SELECT 'OK' AS status;" 2>&1 | find "OK" >nul
    if %errorLevel% neq 0 (
        echo   ERROR: Cannot connect as user market.
        echo   Solutions:
        echo     1. Run script: script\build\init-postgresql-local.bat
        echo     2. Or modify pg_hba.conf to trust, restart, then create user
        pause
        exit /b 1
    )
)

:: 启动后端
echo.
echo [4/4] Starting backend application...
echo.
cd /d "%~dp0..\backend"

echo    Using Maven to run Spring Boot...
echo    (First run will download dependencies - may take 2-5 minutes)
echo.
echo    Press Ctrl+C to stop the server
echo ========================================
echo.

mvn spring-boot:run

pause
