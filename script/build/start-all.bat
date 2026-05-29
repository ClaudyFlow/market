@echo off
set "SCRIPT_DIR=%~dp0"

echo [Start All] Starting all services...
echo.

echo Please start services individually:
echo   1. Ensure PostgreSQL and Redis are running
echo   2. Run backend\start-backend.bat
echo   3. Run frontend\start-frontend.bat
echo.
pause