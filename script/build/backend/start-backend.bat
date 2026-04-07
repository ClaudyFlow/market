@echo off
chcp 65001 >nul
REM ============================================================
REM Start Backend Service (Redis + PostgreSQL + Spring Boot)
REM ============================================================

echo [Start Backend] Starting backend services...
echo.

:: [1/3] Stop Redis if exists
echo [1/3] Stopping Redis...
sc query Redis >nul 2>&1
if %errorlevel% equ 0 (
    net stop Redis >nul 2>&1
    echo [Success] Redis stopped
) else (
    echo [Info] Redis not installed
)
echo.

:: [2/3] Start PostgreSQL
echo [2/3] Starting PostgreSQL...
call "%~dp0start-database.bat"
if %errorlevel% neq 0 (
    echo [Error] Database startup failed
    exit /b 1
)
echo.

:: Start Redis
echo [Start] Starting Redis...
winget list Redis.Redis >nul 2>nul
if %errorlevel% neq 0 (
    echo [Info] Redis not found, installing...
    winget install -e --id Redis.Redis --accept-package-agreements --accept-source-agreements
    if %errorlevel% neq 0 (
        echo [Error] Redis installation failed
        exit /b 1
    )
)
net start Redis >nul 2>&1
echo [Success] Redis started
echo.

:: [3/3] Compile and Start Spring Boot
echo [3/3] Compiling and starting Spring Boot...
call "%~dp0compile-backend.bat"
if %errorlevel% neq 0 (
    echo [Error] Compilation failed
    exit /b 1
)

cd /d "%~dp0..\..\..\backend"
if not exist "pom.xml" (
    echo [Error] pom.xml not found
    exit /b 1
)

echo [Start] Starting Spring Boot backend service...
start "Spring Boot Server" cmd /c "mvn spring-boot:run"
if %errorlevel% neq 0 (
    echo [Error] Backend service startup failed
    exit /b 1
)
echo [Success] Backend service started
