@echo off
set "PROJECT_ROOT=D:\Code\Project\market"

echo [Build Frontend] Building frontend via Docker...
echo.

docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" build frontend-dev
docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d frontend-dev

echo.
echo [Success] Frontend built and started via Docker
pause