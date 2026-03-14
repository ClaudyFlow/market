@echo off
chcp 65001 >nul
echo ========================================
echo     市场平台 - 完整启动脚本
echo     Market Platform - Start All
echo ========================================
echo.

REM 启动后端
echo [1/3] 启动后端服务...
start "" cmd /c "%~dp0start-backend.bat"
timeout /t 2 /nobreak >nul
echo [成功] 后端服务启动命令已发送
echo.

REM 构建前端
echo [2/3] 构建前端...
cd /d "%~dp0..\frontend"
call npm run build >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 前端构建失败
    pause
    exit /b 1
)
echo [成功] 前端构建完成
echo.

REM 复制文件到 Nginx
echo [3/3] 部署到 Nginx...
cd /d "%~dp0..\frontend"
if exist "nginx\html" (
    rmdir /s /q "nginx\html"
)
mkdir "nginx\html"
xcopy /E /I /Y "dist\*" "nginx\html\" >nul
echo [成功] 文件已部署到 Nginx
echo.

REM 启动 Nginx
cd /d "%~dp0..\frontend\nginx"
tasklist /FI "IMAGENAME eq nginx.exe" 2>nul | findstr /I nginx.exe >nul 2>nul
if %errorlevel% equ 0 (
    nginx.exe -s stop >nul 2>&1
    timeout /t 2 /nobreak >nul
)
start "" nginx.exe
timeout /t 2 /nobreak >nul
echo [成功] Nginx 已启动
echo.

echo ========================================
echo     启动完成！
echo ========================================
echo.
echo 访问地址：
echo   用户端：http://localhost/
echo   商家端：http://localhost/merchant.html
echo   管理端：http://localhost/admin.html
echo   后端 API: http://localhost:8080/api/
echo.
echo 按任意键查看浏览器...
pause >nul
start http://localhost/
