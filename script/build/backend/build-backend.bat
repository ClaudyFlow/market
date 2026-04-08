@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

echo ========================================
echo     Backend Build Script
echo ========================================
echo.

set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%..\..\.."
set "COLOR=%SCRIPT_DIR%..\color.bat"

:: 检查 Java
echo [1/2] Checking Java...
where java >nul 2>nul
if !errorlevel! neq 0 (
    winget list Oracle.JDK.21 >nul 2>nul
    if !errorlevel! neq 0 (
        echo [Info] Java not found, installing...
        winget install -e --id Oracle.JDK.21 --accept-package-agreements --accept-source-agreements
        if !errorlevel! neq 0 (
            call "%COLOR%" Red "Java installation failed"
            exit /b 1
        )
        call "%COLOR%" Green "Java 21 installed"
    )
    winget list Oracle.JDK.21 >nul 2>nul
    if !errorlevel! neq 0 (
        call "%COLOR%" Red "Java installation verification failed"
        exit /b 1
    )
    call "%COLOR%" Green "Java 21 installation verified"
    where java >nul 2>nul
    if !errorlevel! neq 0 (
        cls
        start "" cmd /c "%SCRIPT_DIR%start-backend.bat"
        exit /b 1
    )
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

:: 启动 Spring Boot
echo [3/3] Starting Spring Boot service...
if not exist "pom.xml" (
    call "%COLOR%" Red "pom.xml not found"
    exit /b 1
)

echo [Start] Starting Spring Boot backend service...
start "Spring Boot Server" cmd /c "mvn spring-boot:run"
if !errorlevel! neq 0 (
    call "%COLOR%" Red "Backend service startup failed"
    exit /b 1
)
call "%COLOR%" Green "Backend service started"
echo.

echo ========================================
call "%COLOR%" Green "Backend Build & Start Success!"
echo ========================================
