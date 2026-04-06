@echo off
chcp 65001 >nul
REM ============================================================
REM Start Backend Service (Database + Spring Boot)
REM ============================================================

echo [Start Backend] Starting backend services...

REM Start database first
call "%~dp0start-database.bat"
if %errorlevel% neq 0 (
    echo [Error] Database startup failed, aborting
    exit /b 1
)

REM Check if mvnd is available
where mvnd >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] mvnd not found, please install mvnd
    exit /b 1
)

REM Start Spring Boot
cd /d "%~dp0..\..\.."
if not exist "pom.xml" (
    echo [Error] pom.xml not found, current directory: %CD%
    exit /b 1
)

echo [Start] Starting Spring Boot backend service...
start /b mvnd spring-boot:run >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] Backend service startup failed
    exit /b 1
)
echo [Success] Backend service started
