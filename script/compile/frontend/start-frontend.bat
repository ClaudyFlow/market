@echo off
chcp 65001 >nul

echo ========================================
echo     Start Frontend Service
echo ========================================
echo.

set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%..\..\.."

:: [1/3] Check environment
echo [1/3] Check environment...
if exist "%PROJECT_ROOT%\frontend\nginx\nginx.exe" (
    echo [Success] Nginx found
) else (
    echo [Error] Nginx not found
    exit /b 1
)
winget list OpenJS.NodeJS.LTS >nul 2>nul
if %errorlevel% neq 0 (
    echo [Info] Node.js not found, installing...
    winget install -e --id OpenJS.NodeJS.LTS --accept-package-agreements --accept-source-agreements
    if %errorlevel% neq 0 (
        echo [Error] Node.js installation failed
        exit /b 1
    )
    echo [Success] Node.js installed
) else (
    echo [Success] Node.js is installed
)
echo [Success] Environment check passed
echo.

:: [2/3] Stop services
echo [2/3] Stop services...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173" ^| findstr "LISTENING"') do (
    taskkill /PID %%a /F >nul 2>nul
)
cd /d "%PROJECT_ROOT%\frontend\nginx"
nginx.exe -s stop >nul 2>nul
timeout /t 2 /nobreak >nul
taskkill /IM nginx.exe /F >nul 2>nul
echo [Success] Services stopped
echo.

:: [3/3] Compile and Start
echo [3/3] Compile and start frontend...
call "%SCRIPT_DIR%compile-frontend.bat"
if %errorlevel% neq 0 (
    echo [Error] Compile failed
    exit /b 1
)
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
