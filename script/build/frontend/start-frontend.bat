@echo off
chcp 65001 >nul

echo ========================================
echo     Start Frontend Service
echo ========================================
echo.

set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%..\..\.."
set "COLOR=%SCRIPT_DIR%..\color.bat"

:: [1/2] 检查环境
echo [1/2] Check environment...
if exist "%PROJECT_ROOT%\depend\nginx\nginx.exe" (
    call "%COLOR%" Green "Nginx found"
) else (
    call "%COLOR%" Red "Nginx not found"
    exit /b 1
)
call "%COLOR%" Green "Environment check passed"
echo.

:: [2/2] 停止旧服务
echo [2/2] Stop services...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173" ^| findstr "LISTENING"') do (
    taskkill /PID %%a /F >nul 2>nul
)
cd /d "%PROJECT_ROOT%\depend\nginx"
nginx.exe -s stop >nul 2>nul
timeout /t 2 /nobreak >nul
taskkill /IM nginx.exe /F >nul 2>nul
call "%COLOR%" Green "Services stopped"
echo.

:: [3/2] 构建并启动
echo [3/2] Build and start frontend...
call "%SCRIPT_DIR%build-frontend.bat"
if %errorlevel% neq 0 (
    call "%COLOR%" Red "Build failed"
    exit /b 1
)
cd /d "%PROJECT_ROOT%\depend\nginx"
start /b nginx.exe
timeout /t 2 /nobreak >nul
call "%COLOR%" Green "Nginx started"
echo.

echo ========================================
call "%COLOR%" Green "Frontend Service Started!"
echo ========================================
echo.
echo Access URLs:
echo   - Production: http://localhost/
echo   - Dev Server: http://localhost:5173
echo   - Merchant: http://localhost/merchant.html
echo   - Admin: http://localhost/admin.html
