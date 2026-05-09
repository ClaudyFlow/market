@echo off
REM ============================================================
REM Start Backend Service via Docker
REM ============================================================

set "PROJECT_ROOT=D:\Code\Project\market"

echo [Start Backend] Starting backend via Docker...
echo.

echo [1/2] Starting database services...
call "%~dp0start-database.bat"
if %errorlevel% neq 0 (
    echo [Error] Database startup failed
    pause
    exit /b 1
)
echo.

echo [2/2] Building and starting Spring Boot...
call "%~dp0build-backend.bat"
if %errorlevel% neq 0 (
    echo [Error] Backend build failed
    pause
    exit /b 1
)
echo.

echo [3/3] Starting Spring Boot container in new window...
start "Spring Boot" cmd /c "docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d springboot & pause"
echo.
echo ========================================
echo [Success] Backend Service Started!
echo ========================================
echo NOTE: Spring Boot is starting in a separate window
pause
