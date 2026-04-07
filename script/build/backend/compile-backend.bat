@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

echo ========================================
echo     Backend Compile Script
echo ========================================
echo.

set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%..\..\.."
set "COLOR=%SCRIPT_DIR%..\color.bat"

:: 检查 Java
echo [1/2] Checking Java...
where java >nul 2>nul
if !errorlevel! neq 0 (
    echo [Info] Java not found, installing...
    winget install -e --id Oracle.JDK.21 --accept-package-agreements --accept-source-agreements
    if !errorlevel! neq 0 (
        call "%COLOR%" Red "Java installation failed"
        exit /b 1
    )
    call "%COLOR%" Green "Java 21 installed"
) else (
    call "%COLOR%" Green "Java 21 is installed"
)
echo.

:: 编译后端
echo [2/2] Compiling backend...
cd /d "%PROJECT_ROOT%\backend"
if exist "target" (
    echo [Clean] Cleaning old build...
    rmdir /s /q target >nul 2>&1
)

:: 优先使用 mvnd，没有则降级 mvn
where mvnd >nul 2>nul
if !errorlevel! equ 0 (
    echo [Build] Compiling with mvnd...
    call mvnd clean compile -DskipTests
) else (
    echo [Build] mvnd not found, using mvn...
    call mvn clean compile -DskipTests
)
if !errorlevel! neq 0 (
    call "%COLOR%" Red "Backend compilation failed"
    exit /b 1
)
call "%COLOR%" Green "Backend compilation completed"
echo.

echo ========================================
call "%COLOR%" Green "Backend Compile Success!"
echo ========================================
