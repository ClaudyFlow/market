@echo off
chcp 65001 >nul
REM ============================================================
REM Start Database Service - PostgreSQL
REM ============================================================

echo [Start Database] Checking PostgreSQL...

REM Check if PostgreSQL is installed
winget list PostgreSQL.PostgreSQL.18 >nul 2>nul
if %errorlevel% neq 0 (
    echo [Info] PostgreSQL not found, installing...
    winget install -e --id PostgreSQL.PostgreSQL.18 --accept-package-agreements --accept-source-agreements
    if %errorlevel% neq 0 (
        echo [Error] PostgreSQL installation failed
        exit /b 1
    )
    echo [Success] PostgreSQL installed
    echo [Info] Please restart the script to apply environment changes
    exit /b 1
)
echo [Success] PostgreSQL is installed
echo.

REM Stop PostgreSQL service if running
sc query postgresql-x64-18 >nul 2>&1
if %errorlevel% equ 0 (
    net stop postgresql-x64-18 >nul 2>&1
    echo [Success] PostgreSQL stopped
)
echo.

REM Start PostgreSQL service
echo [Start] Starting PostgreSQL...
net start postgresql-x64-18 >nul 2>&1
timeout /t 3 /nobreak >nul

sc query postgresql-x64-18 | findstr "RUNNING" >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] PostgreSQL startup failed
    exit /b 1
)
echo [Success] PostgreSQL started
echo.

REM Verify database connection
set PGPASSWORD=market
psql -U market -d market -c "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] Cannot connect to market database
    exit /b 1
)

echo [Success] Database service is ready
