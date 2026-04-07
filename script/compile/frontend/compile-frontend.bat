@echo off
chcp 65001 >nul
echo ========================================
echo     Frontend Compile Script
echo ========================================
echo.

set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%..\..\.."

echo [1/3] Checking Node.js...
winget list OpenJS.NodeJS.LTS >nul 2>nul
if %errorlevel% neq 0 (
    echo [Info] Node.js not found, installing...
    winget install -e --id OpenJS.NodeJS.LTS --accept-package-agreements --accept-source-agreements
    if %errorlevel% neq 0 (
        echo [Error] Node.js installation failed, please install manually: https://nodejs.org/
        exit /b 1
    )
    echo [Success] Node.js installed
) else (
    echo [Success] Node.js is installed
)
echo.

echo [2/3] Compiling frontend...
cd /d "%PROJECT_ROOT%\frontend"
if not exist "node_modules" (
    echo [Install] Installing npm dependencies...
    call npm install
    if %errorlevel% neq 0 (
        echo [Error] npm install failed
        exit /b 1
    )
)
echo [Build] Building with Vite...
call npm run build >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] Frontend compilation failed
    exit /b 1
)
echo [Success] Frontend compilation completed
echo.

echo [3/3] Starting development server...
echo [Info] Dev server running on port 5173
cd /d "%PROJECT_ROOT%\frontend"
start /b npm run dev >nul 2>&1
echo [Success] Development server started
echo.

echo ========================================
echo     Frontend Compile Success!
echo ========================================
