@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

echo ========================================
echo     Backend Build Script (Fixed PATH)
echo ========================================
echo.

:: Maven 短路径（8.3 格式，兼容空格和 CMD）
set "MAVEN_CMD=C:\PROGRA~1\APACHE~1\bin\mvn.cmd"
if not exist "%MAVEN_CMD%" (
    echo [Error] Maven not found at: %MAVEN_CMD%
    echo [Hint] Please verify Maven is installed in "C:\Program Files\apache-maven-3.9.11"
    echo [Hint] Or update this path in the script.
    exit /b 1
)

:: 检查 Java（复用原逻辑）
echo [1/2] Checking Java...
where java >nul 2>nul
if !errorlevel! neq 0 (
    winget list Oracle.JDK.21 >nul 2>nul
    if !errorlevel! neq 0 (
        echo [Info] Java not found, installing...
        winget install -e --id Oracle.JDK.21 --accept-package-agreements --accept-source-agreements
        if !errorlevel! neq 0 (
            echo [Error] Java installation failed
            exit /b 1
        )
        echo [Success] Java 21 installed
    )
    winget list Oracle.JDK.21 >nul 2>nul
    if !errorlevel! neq 0 (
        echo [Error] Java installation verification failed
        exit /b 1
    )
    where java >nul 2>nul
    if !errorlevel! neq 0 (
        cls
        start "" cmd /c "script\build\backend\start-backend.bat"
        exit /b 1
    )
) else (
    echo [Success] Java 21 is installed
)
echo.

:: 编译后端
echo [2/2] Compiling backend...
cd /d "D:\market\backend"
if exist "target" (
    echo [Clean] Cleaning old build...
    rmdir /s /q target >nul 2>&1
)

echo [Build] Compiling with Maven (fixed path)...
call "%MAVEN_CMD%" clean compile -DskipTests
if !errorlevel! neq 0 (
    echo [Error] Backend compilation failed
    exit /b 1
)
echo [Success] Backend compilation completed
echo.

:: 启动 Spring Boot
echo [3/3] Starting Spring Boot service...
if not exist "pom.xml" (
    echo [Error] pom.xml not found in D:\market\backend
    exit /b 1
)

echo [Start] Starting Spring Boot backend service...
start "Spring Boot Server" cmd /c ""%MAVEN_CMD%" spring-boot:run"
if !errorlevel! neq 0 (
    echo [Error] Backend service startup failed
    exit /b 1
)
echo [Success] Backend service started
echo.

echo ========================================
echo [Success] Backend Build & Start Complete!
echo ========================================
pause
