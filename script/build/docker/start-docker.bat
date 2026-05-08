@echo off
echo Checking Docker...

:: Check if Docker is installed
docker --version >nul 2>&1
if %errorLevel% neq 0 (
    echo Docker not installed. Please install Docker Desktop.
    pause
    exit /b 1
)

:: Check if Docker is running
docker info >nul 2>&1
if %errorLevel% neq 0 (
    echo Docker not running. Starting Docker Desktop...
    start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    echo Waiting for Docker to start...
    :wait_loop
    timeout /t 3 /nobreak >nul
    docker info >nul 2>&1
    if %errorLevel% neq 0 (
        echo Still waiting...
        goto wait_loop
    )
    echo Docker started.
)

echo.
echo Cleaning up old containers...
cd /d "%~dp0..\..\depend"
docker-compose down

echo.
echo Starting all services...
docker-compose up -d

echo.
echo ========================================
echo Services Started!
echo.
echo Access URLs:
echo   API: http://localhost:8080
echo   Nginx: http://localhost
echo   RabbitMQ: http://localhost:15672
echo ========================================
pause
