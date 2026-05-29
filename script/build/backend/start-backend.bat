@echo off
REM ============================================================
REM Start Backend Service
REM ============================================================

set "PROJECT_ROOT=D:\market"

echo [Start Backend] Starting backend service...
echo.

echo [1/2] Checking database services...
sc query postgresql-x64-18 >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] PostgreSQL service exists
) else (
    echo [Warning] PostgreSQL service not found
)

echo.
echo [2/2] Starting Spring Boot...
cd /d "%PROJECT_ROOT%\backend"
start "Spring Boot" cmd /c "mvn spring-boot:run"
echo.
echo ========================================
echo [Success] Backend Service Starting!
echo ========================================
echo NOTE: Spring Boot is starting in a separate window
echo Visit: http://localhost:8080
pause