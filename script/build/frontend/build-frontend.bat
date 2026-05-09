@echo off
set "PROJECT_ROOT=D:\Code\Project\market"

echo [Build Frontend] Building frontend via Docker...
echo.
echo [2/4] Installing npm dependencies...
docker run --rm -v "%PROJECT_ROOT%\frontend:/app" -w /app node:22-alpine npm install --registry https://registry.npmmirror.com
if %errorlevel% neq 0 (
    echo [Error] npm install failed
    pause
    exit /b 1
)
echo [OK] Dependencies installed

echo.
echo [3/4] Starting npm dev in new window (port 5173)...
start "Frontend Dev Server" cmd /c "docker run --rm -v "%PROJECT_ROOT%\frontend:/app" -w /app -p 5173:5173 node:22-alpine npm run dev"
echo [OK] Frontend-dev started in new window

echo.
echo [4/4] Running npm build (port 80)...
docker run --rm -v "%PROJECT_ROOT%\frontend:/app" -w /app node:22-alpine npm run build
if %errorlevel% neq 0 (
    echo [Error] npm build failed
    pause
    exit /b 1
)
echo [OK] Frontend built for nginx

echo.
echo [Success] Frontend built via Docker
