@echo off
chcp 65001 >nul
REM ============================================================
REM Start Backend Service (PostgreSQL + Redis + Spring Boot)
REM ============================================================

set "PROJECT_ROOT=D:\Code\Project\market"

echo [Start Backend] Starting backend services...
echo.

echo [1/5] Checking PostgreSQL...
scoop list postgresql
if %errorlevel% equ 0 (
    echo [Info] PostgreSQL found, updating...
    scoop update postgresql
    if %errorlevel% neq 0 (
        echo [Error] PostgreSQL update failed
        pause
        exit /b 1
    )
    echo [Success] PostgreSQL updated
) else (
    echo [Info] PostgreSQL not found, installing...
    scoop install postgresql
    if %errorlevel% neq 0 (
        echo [Error] PostgreSQL install failed
        pause
        exit /b 1
    )
    echo [Success] PostgreSQL installed
)
echo.

echo [2/5] Checking Redis...
scoop list redis
if %errorlevel% equ 0 (
    echo [Info] Redis found, updating...
    scoop update redis
    if %errorlevel% neq 0 (
        echo [Error] Redis update failed
        pause
        exit /b 1
    )
    echo [Success] Redis updated
) else (
    echo [Info] Redis not found, installing...
    scoop install redis
    if %errorlevel% neq 0 (
        echo [Error] Redis install failed
        pause
        exit /b 1
    )
    echo [Success] Redis installed
)
echo.

echo [3/5] Checking Java...
winget list Oracle.JDK.21 >nul 2>nul
if %errorlevel% equ 0 (
    echo [Info] Java found, updating...
    winget install -e --id Oracle.JDK.21 --accept-package-agreements --accept-source-agreements >nul 2>&1
    if %errorlevel% neq 0 (
        echo [Error] Java update failed
        pause
        exit /b 1
    )
    echo [Success] Java updated
) else (
    echo [Info] Java not found, installing...
    winget install -e --id Oracle.JDK.21 --accept-package-agreements --accept-source-agreements >nul 2>&1
    if %errorlevel% neq 0 (
        echo [Error] Java install failed
        pause
        exit /b 1
    )
    echo [Success] Java installed
)
echo.

echo [4/5] Compiling backend...
cd /d "%PROJECT_ROOT%\backend"
if exist "target" (
    echo [Clean] Cleaning old build...
    rmdir /s /q target >nul 2>&1
)

where mvn >nul 2>nul
if %errorlevel% equ 0 (
    echo [Build] Compiling with mvn...
    call mvn clean compile -DskipTests
)
if %errorlevel% neq 0 (
    echo [Error] Backend compilation failed
    pause
    exit /b 1
)
echo [Success] Backend compilation completed
echo.

echo [5/5] Starting Spring Boot service...
if not exist "pom.xml" (
    echo [Error] pom.xml not found
    pause
    exit /b 1
)

echo [Start] Starting Spring Boot backend service...
start "Spring Boot Server" cmd /c "cd /d "%PROJECT_ROOT%\backend" && mvn spring-boot:run"
if %errorlevel% neq 0 (
    echo [Error] Backend service startup failed
    pause
    exit /b 1
)
echo [Success] Backend service started
echo.

echo ========================================
echo [Success] Backend Service Started!
echo ========================================
pause
