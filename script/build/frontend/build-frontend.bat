@echo off
chcp 65001 >nul

echo ========================================
echo     Frontend Build Script
echo ========================================
echo.

set "PROJECT_ROOT=D:\Code\Project\market"

echo [1/2] Checking Node.js...
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

echo [2/2] Building frontend...
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

echo [Info] Starting development server on http://localhost:5173
start "Vite Dev Server" cmd /c "cd /d "%PROJECT_ROOT%\frontend" && npm run dev"
timeout /t 2 /nobreak >nul
echo [Success] Development server started
echo.

echo ========================================
echo [Success] Frontend Build Success!
echo ========================================
pause
