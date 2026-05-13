@echo off
set "PROJECT_ROOT=D:\Code\Project\market"

echo [Start Database] Starting database services via Docker...
echo.

echo [1/2] Starting PostgreSQL...
docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d postgresql

echo Checking PostgreSQL health...
for /f "tokens=*" %%i in ('docker inspect --format "{{.State.Health.Status}}" postgresql 2^>nul') do set PG_STATUS=%%i
if "%PG_STATUS%"=="healthy" (
    echo [OK] PostgreSQL is healthy
) else (
    echo [Warning] PostgreSQL status: %PG_STATUS%
)

echo.
echo [2/2] Starting Redis...
docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d redis

echo Checking Redis health...
for /f "tokens=*" %%i in ('docker inspect --format "{{.State.Health.Status}}" redis 2^>nul') do set REDIS_STATUS=%%i
if "%REDIS_STATUS%"=="healthy" (
    echo [OK] Redis is healthy
) else (
    echo [Warning] Redis status: %REDIS_STATUS%
)

echo.
echo [Success] Database services started via Docker
