@echo off
set "PROJECT_ROOT=D:\Code\Project\market"

echo [Build Backend] Building backend via Docker...
echo.

docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" build app

echo.
echo [Success] Backend built via Docker
pause