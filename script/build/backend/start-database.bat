@echo off
set "PROJECT_ROOT=D:\market"

echo [Start Database] Starting database services...
echo.

echo [1/2] Checking PostgreSQL...
sc query postgresql-x64-18 >nul 2>&1
if %errorlevel% equ 0 (
    echo PostgreSQL service found. Use 'net start postgresql-x64-18' to start.
) else (
    echo [Warning] PostgreSQL service not found
)

echo.
echo [2/2] Checking Redis...
sc query redis >nul 2>&1
if %errorlevel% equ 0 (
    echo Redis service found. Use 'net start redis' to start.
) else (
    echo [Warning] Redis service not found
)

echo.
echo ========================================
echo Please ensure the following services are running:
echo - PostgreSQL (port 5432)
echo - Redis (port 6379)
echo ========================================
echo.
echo To start services manually:
echo   net start postgresql-x64-18
echo   net start redis
pause