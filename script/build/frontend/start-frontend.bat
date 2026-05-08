@echo off
REM ============================================================
REM Start Frontend Service via Docker
REM ============================================================

set "PROJECT_ROOT=D:\Code\Project\market"

echo [Start Frontend] Starting frontend via Docker...
echo.

echo [1/2] Starting Nginx...
docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d nginx
echo.

echo [2/2] Starting Node.js dev server...
docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d frontend-dev
echo.

echo ========================================
echo [Success] Frontend Service Started!
echo ========================================
echo.
echo Access URLs:
echo   - Production: http://localhost/
echo   - Dev Server: http://localhost:5173
echo   - Merchant: http://localhost/merchant.html
echo   - Admin: http://localhost/admin.html
echo.
pause