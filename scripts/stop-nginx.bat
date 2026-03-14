@echo off
chcp 65001 >nul
echo ========================================
echo     市场平台 - 停止 Nginx
echo     Market Platform - Stop Nginx
echo ========================================
echo.

cd /d "%~dp0..\frontend\nginx"

echo [停止] Nginx 服务...
nginx.exe -s stop
if %errorlevel% neq 0 (
    echo [提示] Nginx 可能未运行或已停止
) else (
    echo [成功] Nginx 已停止
)
echo.
pause
