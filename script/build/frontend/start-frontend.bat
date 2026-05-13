@echo off
REM ============================================================
REM Start Frontend Service via Docker
REM ============================================================

set "PROJECT_ROOT=D:\Code\Project\market"

echo [Start Frontend] Starting frontend via Docker...
echo.

echo [1/2] Building frontend...
call "%~dp0build-frontend.bat"
if %errorlevel% neq 0 (
    echo [Error] Frontend build failed
    pause
    exit /b 1
)
echo.

echo [2/2] Starting nginx (port 80)...
docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d nginx
if %errorlevel% neq 0 (
    echo [Error] Failed to start nginx
    pause
    exit /b 1
)

echo.
echo [Success] Frontend started
pause
