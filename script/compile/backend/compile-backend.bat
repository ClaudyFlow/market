@echo off
chcp 65001 >nul
echo ========================================
echo     Backend Compile Script
echo ========================================
echo.

set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%..\..\.."

echo [1/2] Checking Java...
winget list Oracle.JDK.21 >nul 2>nul
if %errorlevel% neq 0 (
    echo [Info] Java 21 not found, installing...
    winget install -e --id Oracle.JDK.21 --accept-package-agreements --accept-source-agreements
    if %errorlevel% neq 0 (
        echo [Error] Java installation failed
        exit /b 1
    )
    echo [Success] Java 21 installed
) else (
    echo [Success] Java 21 is installed
)
echo.

echo [2/2] Compiling backend...
winget list mvndaemon.mvnd >nul 2>nul
if %errorlevel% neq 0 (
    echo [Error] mvnd not found
    echo [Action] Please visit: https://github.com/mvndaemon/mvnd
    exit /b 1
)
cd /d "%PROJECT_ROOT%"
if exist "target" (
    echo [Clean] Cleaning old compilation...
    rmdir /s /q target >nul 2>&1
)
call mvnd clean compile -DskipTests >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] Backend compilation failed
    exit /b 1
)
echo [Success] Backend compilation completed
echo.

echo ========================================
echo     Backend Compile Success!
echo ========================================
