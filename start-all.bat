@echo off
REM ============================================================
REM Market System - Complete Startup Script
REM ============================================================

set "PROJECT_ROOT=D:\market"
set "REDIS_HOME=C:\Redis"

echo.
echo ===========================================================
echo          Market System - Starting All Services
echo ===========================================================
echo.

echo [Step 1/4] Checking Redis...
tasklist /FI "IMAGENAME eq redis-server.exe" 2>nul | find /I "redis-server.exe" >nul
if %errorlevel% equ 0 (
    echo   [OK] Redis is already running
) else (
    echo   [Info] Starting Redis...
    start "Redis" /MIN "C:\Redis\redis-server.exe" --port 6379
    timeout /t 3 >nul
    echo   [OK] Redis started
)

echo.
echo [Step 2/4] Checking PostgreSQL...
sc query postgresql-x64-18 >nul 2>&1
if %errorlevel% equ 0 (
    sc query postgresql-x64-18 | find "RUNNING" >nul
    if %errorlevel% equ 0 (
        echo   [OK] PostgreSQL is running
    ) else (
        echo   [Info] Starting PostgreSQL...
        net start postgresql-x64-18 >nul 2>&1
        echo   [OK] PostgreSQL started
    )
) else (
    echo   [Warning] PostgreSQL service not found
)

echo.
echo [Step 3/4] Starting Backend (Spring Boot)...
if exist "%PROJECT_ROOT%\backend\pom.xml" (
    start "Spring Boot Backend" cmd /c "cd /d %PROJECT_ROOT%\backend && mvn spring-boot:run"
    echo   [OK] Backend starting in new window...
) else (
    echo   [Error] Backend not found at %PROJECT_ROOT%\backend
)

echo.
echo [Step 4/4] Starting Frontend (Vite Dev Server)...
if exist "%PROJECT_ROOT%\frontend\package.json" (
    start "Frontend Dev Server" cmd /c "cd /d %PROJECT_ROOT%\frontend && npm run dev"
    echo   [OK] Frontend starting in new window...
) else (
    echo   [Warning] Frontend not found at %PROJECT_ROOT%\frontend
)

echo.
echo ===========================================================
echo                    Startup Complete!
echo ===========================================================
echo.
echo  Services:
echo    - Redis:      localhost:6379
echo    - PostgreSQL: localhost:5432
echo    - Backend:    http://localhost:8080
echo    - Frontend:   http://localhost:5173
echo.
echo  Press any key to exit this window (services will continue)...
echo ===========================================================
pause >nul