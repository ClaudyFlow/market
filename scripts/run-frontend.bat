@echo off
echo ========================================
echo     Market Platform - Frontend
echo ========================================
echo.

REM Check Node.js
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [Error] Node.js not found, please install Node.js
    echo Download: https://nodejs.org/
    pause
    exit /b 1
)

echo [Check] Node.js installed
node --version
echo.

REM Go to frontend directory
cd /d "%~dp0..\frontend"

REM Check node_modules
if not exist "node_modules" (
    echo [Info] First run, installing dependencies...
    call npm install
    if %errorlevel% neq 0 (
        echo [Error] npm install failed
        pause
        exit /b 1
    )
    echo [Done] Dependencies installed
    echo.
)

REM Build production version
echo [Build] Building production version...
echo.
call npm run build
if %errorlevel% neq 0 (
    echo [Error] Build failed
    pause
    exit /b 1
)
echo.
echo [Done] Build successful! Output: nginx/html/
echo.

REM Start Nginx
echo [Start] Nginx...
echo.
cd /d "%~dp0..\frontend\nginx"
start "" nginx.exe
if %errorlevel% neq 0 (
    echo [Error] Nginx start failed
    pause
    exit /b 1
)
echo [Done] Nginx started
echo.

REM Start dev server
echo [Start] Frontend development server...
echo.
cd /d "%~dp0..\frontend"
call npm run dev
