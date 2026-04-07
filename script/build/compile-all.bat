@echo off
chcp 65001 >nul
echo ========================================
echo     Market Platform - Full Build Script
echo ========================================
echo.

cd /d "%~dp0../../.."

:: ========================================
:: [1/8] Check Java
:: ========================================
echo [1/8] Checking Java...
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [Error] Java not found
    echo [Action] Please visit: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
echo [Success] Java is installed
java --version | findstr "version"
echo.

:: ========================================
:: [2/8] Check mvnd
:: ========================================
echo [2/8] Checking Maven Daemon (mvnd)...
where mvnd >nul 2>nul
if %errorlevel% neq 0 (
    echo [Error] mvnd not found
    echo [Action] Please visit: https://github.com/mvndaemon/mvnd
    pause
    exit /b 1
)
echo [Success] mvnd is installed
echo.

:: ========================================
:: [3/8] Check Node.js
:: ========================================
echo [3/8] Checking Node.js...
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [Warning] Node.js not found
    echo [Action] Installing Node.js via winget...
    winget install OpenJS.NodeJS
    if %errorlevel% neq 0 (
        echo [Error] Node.js installation failed
        echo [Action] Please visit: https://nodejs.org/
        pause
        exit /b 1
    )
    echo [Success] Node.js installed
) else (
    echo [Success] Node.js is installed
    node --version
    npm --version
)
echo.

:: ========================================
:: [4/8] Check Nginx
:: ========================================
echo [4/8] Checking Nginx...
if exist "..\..\..\depend\nginx\nginx.exe" (
    echo [Success] Nginx found in depend\nginx\
    ..\..\..\depend\nginx\nginx.exe -v 2>&1
    echo [Info] Nginx website: https://nginx.org/
) else (
    echo [Error] depend\nginx\nginx.exe not found
    echo [Info] Nginx is pre-packaged in the repository, please ensure files are complete
    echo [Action] Please visit: https://nginx.org/
    pause
    exit /b 1
)
echo.

:: ========================================
:: [5/8] Check PostgreSQL
:: ========================================
echo [5/8] Checking PostgreSQL...
sc query PostgreSQL >nul 2>nul
if %errorlevel% equ 0 (
    echo [Success] PostgreSQL service is installed
    net start PostgreSQL >nul 2>nul
    echo [Success] PostgreSQL started
) else (
    echo [Warning] PostgreSQL service not detected
    echo [Info] Please visit: https://www.postgresql.org/
)
echo.

:: ========================================
:: [6/8] Check Redis
:: ========================================
echo [6/8] Checking Redis...
sc query Redis >nul 2>nul
if %errorlevel% equ 0 (
    echo [Success] Redis service is installed
    net start Redis >nul 2>nul
    echo [Success] Redis started
) else (
    echo [Warning] Redis service not detected
    echo [Info] Please visit: https://redis.io/
)
echo.

:: ========================================
:: [7/8] Build Frontend
:: ========================================
echo ========================================
echo [7/8] Building Frontend
echo ========================================
echo.

cd /d "%~dp0..\..\frontend"

if not exist "node_modules" (
    echo [Install] Installing frontend dependencies...
    call npm install
    if %errorlevel% neq 0 (
        echo [Error] Dependency installation failed
        pause
        exit /b 1
    )
)

echo [Build] Building frontend...
call npm run build
if %errorlevel% neq 0 (
    echo [Error] Frontend build failed
    pause
    exit /b 1
)
echo [Success] Frontend build completed
echo.

:: ========================================
:: [8/8] Build Backend
:: ========================================
echo ========================================
echo [8/8] Building Backend
echo ========================================
echo.

cd /d "%~dp0.."

if exist "target" (
    echo [Clean] Cleaning old build...
    rmdir /s /q target
)

echo [Build] Building backend...
call mvnd clean compile -DskipTests
if %errorlevel% neq 0 (
    echo [Error] Backend build failed
    pause
    exit /b 1
)
echo [Success] Backend build completed
echo.

:: ========================================
:: Restart Nginx
:: ========================================
echo ========================================
echo Restarting Nginx
echo ========================================
echo.

cd /d "%~dp0../../../depend/nginx"
if exist "nginx.exe" (
    echo [Restart] Restarting Nginx...
    nginx.exe -s stop >nul 2>nul
    timeout /t 2 /nobreak >nul
    start "" nginx.exe
    echo [Success] Nginx restarted
)
echo.

echo ========================================
echo     Full Build Success!
echo ========================================
echo.
echo Build Information:
echo   - Frontend: Vite + Vue 3
echo   - Backend: Spring Boot + mvnd
echo   - Server: Nginx (pre-packaged)
echo   - Database: PostgreSQL
echo   - Cache: Redis
echo.
echo Access URLs:
echo   - Frontend: http://localhost:80
echo   - Backend API: http://localhost:8080/api/
echo   - Dev Server: http://localhost:5173
echo.
pause
