@echo off
set "PROJECT_ROOT=D:\Code\Project\market"

echo [Start Database] Starting PostgreSQL via Docker...
echo.

docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d postgres

echo.
echo [Success] PostgreSQL started via Docker
pause