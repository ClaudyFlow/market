@echo off
chcp 65001 >nul
echo ========================================
echo     市场平台 - 前端构建脚本
echo     Market Platform - Frontend Build
echo ========================================
echo.

cd /d "%~dp0..\frontend"

echo [1/4] 检查 Node.js...
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [错误] 未找到 Node.js，请先安装 Node.js
    echo 下载地址：https://nodejs.org/
    pause
    exit /b 1
)
echo [成功] Node.js 已安装
node --version
npm --version
echo.

echo [2/4] 检查依赖...
if not exist "node_modules" (
    echo [提示] 首次运行，正在安装依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo [错误] npm install 失败
        pause
        exit /b 1
    )
    echo [完成] 依赖安装完成
) else (
    echo [成功] 依赖已存在
)
echo.

echo [3/4] 构建生产版本...
call npm run build
if %errorlevel% neq 0 (
    echo [错误] 构建失败
    pause
    exit /b 1
)
echo [完成] 构建成功！输出目录：nginx/html/
echo.

echo [4/4] 重启 Nginx...
cd /d "%~dp0..\frontend\nginx"
nginx.exe -s stop
timeout /t 2 /nobreak >nul
start "" nginx.exe
echo [完成] Nginx 已重启
echo.

echo ========================================
echo     构建完成！
echo ========================================
echo.
echo 下一步操作：
echo   1. 启动 Nginx: scripts\start-nginx.bat
echo   2. 或直接运行：scripts\start.bat
echo.
pause
