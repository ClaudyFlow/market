@echo off
REM ============================================================
REM Start Backend Service via Docker
REM ============================================================

set "PROJECT_ROOT=D:\Code\Project\market"

echo [Start Backend] Starting backend via Docker...
echo.

echo [1/3] Starting PostgreSQL...
docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d postgres
echo.

echo [2/3] Starting Redis...
docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d redis
echo.

echo [3/3] Starting Spring Boot...
docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d app
echo.

echo ========================================
echo [Success] Backend Service Started!
echo ========================================
pause