@echo off
set "PROJECT_ROOT=D:\market"

echo [Build Frontend] Building frontend via npm...
echo.

echo [1/4] Checking Node.js and npm...
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] Node.js not found. Please install Node.js.
    pause
    exit /b 1
)
where npm >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] npm not found. Please install npm.
    pause
    exit /b 1
)
echo [OK] Node.js and npm found

echo.
echo [2/4] Installing npm dependencies...
cd /d "%PROJECT_ROOT%\frontend"
call npm install
if %errorlevel% neq 0 (
    echo [Error] npm install failed
    pause
    exit /b 1
)
echo [OK] Dependencies installed

echo.
echo [3/4] Starting npm dev in new window (port 5173)...
start "Frontend Dev Server" cmd /c "npm run dev"
echo [OK] Frontend dev server starting in new window

echo.
echo [4/4] Running npm build...
call npm run build
if %errorlevel% neq 0 (
    echo [Error] npm build failed
    pause
    exit /b 1
)
echo [OK] Frontend built

echo.
echo [Success] Frontend built successfully
echo Dev server: http://localhost:5173
echo Build output: %PROJECT_ROOT%\frontend\dist