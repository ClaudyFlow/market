@echo off
chcp 65001 >nul

echo ========================================
echo     Start Frontend Service
echo ========================================
echo.

set "PROJECT_ROOT=D:\Code\Project\market"

echo [1/5] Checking Node.js...
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [Warning] Node.js not found, installing...
    scoop install -q nodejs
    if %errorlevel% neq 0 (
        echo [Error] Node.js install failed
        pause
        exit /b 1
    )
    echo [Success] Node.js installed
) else (
    echo [Info] Node.js found
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
echo Starting Vite dev server on port 5173...
cd /d "%PROJECT_ROOT%\frontend"
start npm run dev
timeout /t 3 /nobreak >nul
echo [Success] Vite dev server started
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
