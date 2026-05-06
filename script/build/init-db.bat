@echo off
title PostgreSQL Database Initialization

echo ========================================
echo Initializing PostgreSQL Database
echo ========================================
echo.

echo [1/5] Checking PostgreSQL installation...
set "PSQL_PATH="
if exist "C:\Program Files\PostgreSQL\18\bin\psql.exe" (
    set "PSQL_PATH=C:\Program Files\PostgreSQL\18\bin\psql.exe"
    echo   Found PostgreSQL 18
) else if exist "C:\Program Files\PostgreSQL\10\bin\psql.exe" (
    set "PSQL_PATH=C:\Program Files\PostgreSQL\10\bin\psql.exe"
    echo   Found PostgreSQL 10
) else (
    echo   ERROR: PostgreSQL not found.
    echo   Install from https://www.postgresql.org/download/windows/
    pause
    exit /b 1
)

echo [2/5] Checking PostgreSQL service...
echo.
echo [3/5] Creating database user and database...
echo    Executing: CREATE USER market
echo    Executing: CREATE DATABASE market

cd /d "%~dp0..\.."
"%PSQL_PATH%" -U postgres -d postgres -f "script\build\init-postgresql.sql" 2>&1

echo.
echo [4/5] Verifying connection...
echo    Testing: psql -U market -d market -c "SELECT 1;"
"%PSQL_PATH%" -U market -d market -c "SELECT 'Connection OK' AS status;" 2>&1

echo.
echo [5/5] Checking backend configuration...
if exist "%~dp0..\..\backend\src\main\resources\application-dev.properties" (
    echo   Found: application-dev.properties
    findstr "spring.datasource.username=market" "%~dp0..\..\backend\src\main\resources\application-dev.properties" >nul
    if %errorLevel% equ 0 (
        echo   Credentials OK: market/market
    ) else (
        echo   WARNING: Credentials mismatch
    )
)

echo.
echo ========================================
echo Initialization Complete!
echo.
echo Start backend:
echo   cd backend
echo   mvn spring-boot:run
echo ========================================
pause
