@echo off
chcp 65001 >nul
REM ============================================================
REM Start Database Service - PostgreSQL
REM ============================================================

echo [Start Database] Checking PostgreSQL...

set PG_BIN=C:\Program Files\PostgreSQL\18\bin
set PG_DATA=C:\Program Files\PostgreSQL\18\data

REM Check if PostgreSQL is installed
"%PG_BIN%\pg_isready.exe" >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] PostgreSQL not installed, installing via winget...
    winget install -e --id PostgreSQL.PostgreSQL.18 --accept-package-agreements --accept-source-agreements
    if %errorlevel% neq 0 (
        echo [Error] PostgreSQL installation failed
        exit /b 1
    )
    echo [Success] PostgreSQL installed
)

REM Check if PostgreSQL is running
"%PG_BIN%\pg_isready.exe" >nul 2>&1
if %errorlevel% neq 0 (
    echo [Start] PostgreSQL not running, starting...
    "%PG_BIN%\pg_ctl.exe" start -D "%PG_DATA%" -l "%PG_DATA%\log\postgresql.log" >nul 2>&1
    timeout /t 3 /nobreak >nul

    "%PG_BIN%\pg_isready.exe" >nul 2>&1
    if %errorlevel% neq 0 (
        echo [Error] PostgreSQL startup failed
        exit /b 1
    )
    echo [Success] PostgreSQL started
)

REM Verify database connection
set PGPASSWORD=market
"%PG_BIN%\psql.exe" -U market -d market -c "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] Cannot connect to market database
    exit /b 1
)

echo [Success] Database service is ready
