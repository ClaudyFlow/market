@echo off
set "PROJECT_ROOT=D:\Code\Project\market"

echo [Build Backend] Building backend via Docker...
echo.

echo [1/4] Checking JDK and Maven images...
for /f "delims=" %%i in ('docker images eclipse-temurin:21-jdk --format "{{.Repository}}" 2^>nul') do set "JDK_FOUND=1"
for /f "delims=" %%i in ('docker images maven:3.9-eclipse-temurin-21 --format "{{.Repository}}" 2^>nul') do set "MAVEN_FOUND=1"
if not defined JDK_FOUND echo [Warning] JDK 21 not found locally
if not defined MAVEN_FOUND echo [Warning] Maven not found locally
echo [OK] Image check complete

echo.
echo [2/4] Compiling with Maven...
docker run --rm -v "%USERPROFILE%\.m2:/root/.m2" -v "%PROJECT_ROOT%\backend:/app" -w /app maven:3.9-eclipse-temurin-21 mvn -B package -DskipTests || (
    echo [Error] Backend compilation failed
    pause
    exit /b 1
)
echo [OK] Backend compiled

echo.
echo [3/4] Starting RabbitMQ...
docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" up -d rabbitmq
if %errorlevel% neq 0 (
    echo [Error] RabbitMQ startup failed
    pause
    exit /b 1
)
echo [OK] RabbitMQ started

echo.
echo [4/4] Building Spring Boot Docker image...
docker-compose -f "%PROJECT_ROOT%\depend\docker-compose.yml" build springboot
if %errorlevel% neq 0 (
    echo [Error] Docker image build failed
    pause
    exit /b 1
)
echo [OK] Spring Boot image built

echo.
echo [Success] Backend built via Docker
