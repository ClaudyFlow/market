@echo off
chcp 65001 >nul
echo ========================================
echo     市场平台 - 清理脚本
echo     Market Platform - Clean
echo ========================================
echo.

cd /d "%~dp0.."

echo [清理] 删除构建产物...

REM 清理前端
if exist "frontend\dist" (
    rmdir /s /q "frontend\dist"
    echo [完成] 已清理 frontend/dist
)

if exist "frontend\node_modules" (
    echo [提示] 是否删除 node_modules? (Y/N)
    set /p choice=
    if /i "%choice%"=="Y" (
        rmdir /s /q "frontend\node_modules"
        echo [完成] 已清理 frontend/node_modules
    )
)

REM 清理后端
if exist "target" (
    rmdir /s /q "target"
    echo [完成] 已清理 target
)

REM 清理 Nginx html
if exist "frontend\nginx\html" (
    rmdir /s /q "frontend\nginx\html"
    echo [完成] 已清理 frontend/nginx/html
)

REM 清理日志
if exist "logs" (
    del /q /s "logs\*.log"
    echo [完成] 已清理日志文件
)

echo.
echo [完成] 清理完成！
echo.
pause
