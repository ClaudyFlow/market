@echo off
chcp 65001 >nul
REM ============================================================
REM Start Database Service - PostgreSQL and Redis
REM ============================================================

echo [Start Database] Checking PostgreSQL...

scoop list postgresql
if %errorlevel% equ 0 (
    echo [Info] PostgreSQL found, updating...
    scoop update postgresql
    if %errorlevel% neq 0 (
        echo [Error] PostgreSQL update failed
        pause
        exit /b 1
    )
    echo [Success] PostgreSQL updated
) else (
    echo [Info] PostgreSQL not found, installing...
    scoop install postgresql
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

scoop list redis
if %errorlevel% equ 0 (
    echo [Info] Redis found, updating...
    scoop update redis
    if %errorlevel% neq 0 (
        echo [Error] Redis update failed
        pause
        exit /b 1
    )
    echo [Success] Redis updated
) else (
    echo [Info] Redis not found, installing...
    scoop install redis
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
