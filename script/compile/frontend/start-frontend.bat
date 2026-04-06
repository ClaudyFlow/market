@echo off
chcp 65001 >nul

echo ========================================
echo     Start Frontend Service
echo ========================================
echo.

set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%..\..\.."

:: [1/4] Check environment
echo [1/4] Check environment...
if exist "%PROJECT_ROOT%\frontend\nginx\nginx.exe" (
    echo [Success] Nginx found
) else (
    echo [Error] Nginx not found
    exit /b 1
)
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [Error] Node.js not found
    exit /b 1
)
echo [Success] Environment check passed
echo.

:: [2/4] Stop services
echo [2/4] Stop services...
:: Only kill node processes running the frontend dev server
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173" ^| findstr "LISTENING"') do (
    taskkill /PID %%a /F >nul 2>nul
)
cd /d "%PROJECT_ROOT%\frontend\nginx"
nginx.exe -s stop >nul 2>nul
timeout /t 2 /nobreak >nul
taskkill /IM nginx.exe /F >nul 2>nul
echo [Success] Services stopped
echo.

:: [3/4] Compile frontend
echo [3/4] Compile frontend...
call "%SCRIPT_DIR%compile-frontend.bat"
if %errorlevel% neq 0 (
    echo [Error] Compile failed
    exit /b 1
)
echo [Success] Compile completed
echo.

:: [4/4] Start Nginx
echo [4/4] Start Nginx...
cd /d "%PROJECT_ROOT%\frontend\nginx"
start /b nginx.exe
timeout /t 2 /nobreak >nul
echo [Success] Nginx started
echo.

echo ========================================
echo     Frontend Service Started!
echo ========================================
echo.
echo Access URLs:
echo   - Production: http://localhost/
echo   - Dev Server: http://localhost:5173
echo   - Merchant: http://localhost/merchant.html
echo   - Admin: http://localhost/admin.html
