@echo off
chcp 65001 >nul
REM ============================================================
REM Start Backend Service (PostgreSQL + Redis + Spring Boot)
REM ============================================================

echo [Start Backend] Starting backend services...
echo.

:: [1/2] Start PostgreSQL + Redis
echo [1/2] Starting PostgreSQL and Redis...
call "%~dp0start-database.bat" "%~f0"
if %errorlevel% neq 0 (
    echo [Error] Database startup failed
    exit /b 1
)
echo.

:: [2/2] Build and Start Spring Boot
echo [2/2] Building and starting Spring Boot...
call "%~dp0build-backend.bat"
if %errorlevel% neq 0 (
    echo [Error] Backend startup failed
    exit /b 1
)

echo.
echo ========================================
echo [Success] Backend Service Started!
echo ========================================
