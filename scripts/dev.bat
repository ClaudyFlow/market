@echo off
chcp 65001 >nul
echo ========================================
echo     市场平台 - 前端开发模式
echo     Market Platform - Frontend Dev
echo ========================================
echo.

cd /d "%~dp0..\frontend"

echo [检查] Node.js...
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [错误] 未找到 Node.js
    pause
    exit /b 1
)

echo [提示] 启动 Vite 开发服务器...
echo.
echo 访问地址：
echo   用户端：http://localhost:5173/
echo   商家端：http://localhost:5173/merchant.html
echo   管理端：http://localhost:5173/admin.html
echo.
echo 按 Ctrl+C 停止服务
echo.

call npm run dev
