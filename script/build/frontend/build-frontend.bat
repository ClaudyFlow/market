@echo off
chcp 65001 >nul

echo ========================================
echo     Frontend Build Script
echo ========================================
echo.

set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%..\..\.."
set "COLOR=%SCRIPT_DIR%..\color.bat"

:: 检查 Node.js
echo [1/2] Checking Node.js...
winget list OpenJS.NodeJS.LTS >nul 2>nul
if %errorlevel% neq 0 (
    echo [Info] Node.js not found, installing...
    winget install -e --id OpenJS.NodeJS.LTS --accept-package-agreements --accept-source-agreements
    if %errorlevel% neq 0 (
        call "%COLOR%" Red "Node.js installation failed, please install manually: https://nodejs.org/"
        exit /b 1
    )
    call "%COLOR%" Green "Node.js installed"
) else (
    call "%COLOR%" Green "Node.js is installed"
)
echo.

:: 构建前端
echo [2/2] Building frontend ^(output to Nginx html^)...
cd /d "%PROJECT_ROOT%\frontend"
if not exist "node_modules" (
    echo [Install] Installing npm dependencies...
    call npm install
    if %errorlevel% neq 0 (
        call "%COLOR%" Red "npm install failed"
        exit /b 1
    )
)
echo [Build] Building with Vite...
call npm run build
if %errorlevel% neq 0 (
    call "%COLOR%" Red "Frontend build failed"
    exit /b 1
)
call "%COLOR%" Green "Frontend build completed"
echo.

:: 启动开发服务器
echo [Info] Starting development server on http://localhost:5173
cd /d "%PROJECT_ROOT%\frontend"
start "Vite Dev Server" cmd /c "npm run dev"
timeout /t 2 /nobreak >nul
call "%COLOR%" Green "Development server started"
echo.

echo ========================================
call "%COLOR%" Green "Frontend Build Success!"
echo ========================================
echo.
