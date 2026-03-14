@echo off
chcp 65001 >nul
echo ========================================
echo     市场平台 - 启动 Nginx
echo     Market Platform - Start Nginx
echo ========================================
echo.

cd /d "%~dp0..\frontend\nginx"

echo [检查] Nginx 是否已运行...
tasklist /FI "IMAGENAME eq nginx.exe" 2>nul | findstr /I nginx.exe >nul 2>nul
if %errorlevel% equ 0 (
    echo [提示] Nginx 已经在运行
    echo [操作] 先停止再重新启动...
    call nginx.exe -s stop
    timeout /t 2 /nobreak >nul
)

echo [启动] Nginx 服务...
start "" nginx.exe
if %errorlevel% neq 0 (
    echo [错误] Nginx 启动失败
    pause
    exit /b 1
)

timeout /t 2 /nobreak >nul
echo [成功] Nginx 已启动
echo.
echo 访问地址：
echo   用户端：http://localhost/
echo   商家端：http://localhost/merchant.html
echo   管理端：http://localhost/admin.html
echo   后端 API: http://localhost:8080/api/
echo.
pause
