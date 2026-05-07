@echo off
chcp 65001 >nul

echo ========================================
echo     Start Frontend Service
echo ========================================
echo.

set "PROJECT_ROOT=D:\Code\Project\market"

echo [1/5] Checking Node.js...
scoop list nodejs
if %errorlevel% equ 0 (
    echo [Info] Node.js found, updating...
    scoop update nodejs
    if %errorlevel% neq 0 (
        echo [Error] Node.js update failed
        pause
        exit /b 1
    )
    echo [Success] Node.js updated
) else (
    echo [Info] Node.js not found, installing...
    scoop install nodejs
    if %errorlevel% neq 0 (
        echo [Error] Node.js install failed
        pause
        exit /b 1
    )
    echo [Success] Node.js installed
)
echo.

echo [2/5] Building frontend...
cd /d "%PROJECT_ROOT%\frontend"
if not exist "node_modules" (
    echo [Install] Installing npm dependencies...
    call npm install
    if %errorlevel% neq 0 (
        echo [Error] npm install failed
        pause
        exit /b 1
    )
)
echo [Build] Building with Vite...
call npm run build
if %errorlevel% neq 0 (
    echo [Error] Frontend build failed
    pause
    exit /b 1
)
echo [Success] Frontend build completed
echo.

echo [3/5] Checking environment...
if exist "%PROJECT_ROOT%\depend\nginx\nginx.exe" (
    echo [Success] Nginx found
) else (
    echo [Error] Nginx not found
    pause
    exit /b 1
)
echo [Success] Environment check passed
echo.

echo [4/5] Stopping services...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173" ^| findstr "LISTENING"') do (
    taskkill /PID %%a /F >nul 2>nul
)
cd /d "%PROJECT_ROOT%\depend\nginx"
nginx.exe -s stop >nul 2>nul
timeout /t 2 /nobreak >nul
taskkill /IM nginx.exe /F >nul 2>nul
echo [Success] Services stopped
echo.

echo [5/5] Starting Nginx...
cd /d "%PROJECT_ROOT%\depend\nginx"
start /b nginx.exe
timeout /t 2 /nobreak >nul
echo [Success] Nginx started
echo.

echo ========================================
echo [Success] Frontend Service Started!
echo ========================================
echo.

echo Access URLs:
echo   - Production: http://localhost/
echo   - Dev Server: http://localhost:5173
echo   - Merchant: http://localhost/merchant.html
echo   - Admin: http://localhost/admin.html
pause
