@echo off
REM ============================================================
REM Start Frontend Service
REM ============================================================

set "PROJECT_ROOT=D:\market"

echo [Start Frontend] Starting frontend service...
echo.

echo [1/2] Checking Node.js...
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] Node.js not found. Please install Node.js.
    pause
    exit /b 1
)
echo [OK] Node.js found

echo.
echo [2/2] Starting frontend development server...
cd /d "%PROJECT_ROOT%\frontend"
start "Frontend Dev Server" cmd /c "npm run dev"
echo.
echo ========================================
echo [Success] Frontend Service Starting!
echo ========================================
echo NOTE: Frontend dev server is starting in a separate window
echo Visit: http://localhost:5173
pause