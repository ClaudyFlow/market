@echo off
set "PROJECT_ROOT=D:\market"

echo [Build Backend] Building backend via Maven...
echo.

echo [1/4] Checking JDK and Maven...
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] Maven not found. Please install Maven.
    pause
    exit /b 1
)
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [Error] JDK not found. Please install JDK 21.
    pause
    exit /b 1
)
echo [OK] Environment check complete

echo.
echo [2/4] Compiling with Maven...
cd /d "%PROJECT_ROOT%\backend"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo [Error] Backend compilation failed
    pause
    exit /b 1
)
echo [OK] Backend compiled

echo.
echo [3/4] Checking PostgreSQL...
where psql >nul 2>&1
if %errorlevel% neq 0 (
    echo [Warning] psql not found. Ensure PostgreSQL is installed and running.
) else (
    echo [OK] PostgreSQL client found
)

echo.
echo [4/4] Checking Redis...
where redis-cli >nul 2>&1
if %errorlevel% neq 0 (
    echo [Warning] redis-cli not found. Ensure Redis is installed and running.
) else (
    echo [OK] Redis client found
)

echo.
echo [Success] Backend built successfully
echo JAR file: %PROJECT_ROOT%\backend\target\market-*.jar