@echo off
chcp 65001 >nul
REM ============================================================
REM Start Database Service - PostgreSQL
REM ============================================================

set "CALLER=%~1"
if "%CALLER%"=="" set "CALLER=%~dp0start-backend.bat"

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
    winget list PostgreSQL.PostgreSQL.18 >nul 2>nul
    if %errorlevel% neq 0 (
        echo [Error] PostgreSQL installation verification failed
        exit /b 1
    )
    echo [Success] PostgreSQL installation verified
    where psql >nul 2>nul
    if %errorlevel% neq 0 (
        cls
        start "" cmd /c "%CALLER%"
        exit /b 1
    )
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
echo.

:: Start Redis
echo [Start] Starting Redis...
winget list Redis.Redis >nul 2>nul
if %errorlevel% neq 0 (
    echo [Info] Redis not found, installing...
    winget install -e --id Redis.Redis --accept-package-agreements --accept-source-agreements
    if %errorlevel% neq 0 (
        echo [Error] Redis installation failed
        exit /b 1
    )
    echo [Success] Redis installed
    winget list Redis.Redis >nul 2>nul
    if %errorlevel% neq 0 (
        echo [Error] Redis installation verification failed
        exit /b 1
    )
    echo [Success] Redis installation verified
)
net start Redis >nul 2>&1
echo [Success] Redis started
echo.

echo [Success] Database service is ready
