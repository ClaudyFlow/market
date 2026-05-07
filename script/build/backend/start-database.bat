@echo off
chcp 65001 >nul
REM ============================================================
REM Start Database Service - PostgreSQL and Redis
REM ============================================================

echo [Start Database] Checking PostgreSQL...

where psql >nul 2>&1
if %errorlevel% equ 0 (
    echo [Info] PostgreSQL found
) else (
    echo [Info] PostgreSQL not found, installing...
    scoop install -q postgresql
    if %errorlevel% neq 0 (
        echo [Error] PostgreSQL install failed
        pause
        exit /b 1
    )
    echo [Success] PostgreSQL installed
)
echo.

REM Check if PostgreSQL is accessible
set PGPASSWORD=market
psql -U market -d market -c "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo [Info] PostgreSQL not running, starting...
    start "" cmd /c "pg_ctl -D %USERPROFILE%\scoop\persist\postgresql\data start"
    timeout /t 5 /nobreak >nul
    psql -U market -d market -c "SELECT 1;" >nul 2>&1
    if %errorlevel% neq 0 (
        echo [Error] Cannot start PostgreSQL
        pause
        exit /b 1
    )
)
echo [Success] PostgreSQL is ready
echo.

echo [Start Database] Checking Redis...

where redis-server >nul 2>&1
if %errorlevel% equ 0 (
    echo [Info] Redis found
) else (
    echo [Info] Redis not found, installing...
    scoop install -q redis
    if %errorlevel% neq 0 (
        echo [Error] Redis install failed
        pause
        exit /b 1
    )
    echo [Success] Redis installed
)
echo.

redis-cli ping >nul 2>&1
if %errorlevel% neq 0 (
    echo [Info] Redis not running, starting...
    start "" redis-server
    timeout /t 3 /nobreak >nul
    redis-cli ping >nul 2>&1
    if %errorlevel% neq 0 (
        echo [Error] Cannot start Redis
        pause
        exit /b 1
    )
)
echo [Success] Redis is ready
echo.

echo [Success] Database services are ready
pause
