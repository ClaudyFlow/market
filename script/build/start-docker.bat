@echo off
title Market Platform - Docker Start

echo ========================================
echo Market Platform Backend - Docker One-Click Start
echo ========================================
echo.

echo [1/3] Changing to depend directory...
cd /d "%~dp0..\depend"

echo [2/3] Stopping old containers (if any)...
docker-compose down 2>nul

echo [3/3] Starting all services...
echo Starting containers (first run may take 2-5 minutes to pull images)...
echo.

docker-compose up -d

echo.
echo ========================================
echo Services Started!
echo   Check status: docker-compose ps
echo   View logs:
echo     docker-compose logs -f app
echo     docker-compose logs -f postgres
echo     docker-compose logs -f redis
echo     docker-compose logs -f rabbitmq
echo.
echo Access URLs:
echo   API: http://localhost:8080
echo   RabbitMQ Admin: http://localhost:15672
echo.
echo Test Accounts:
echo   Admin: admin / 123456
echo   Merchant: merchant1 / 123456
echo   User: user1 / 123456
echo ========================================
pause
