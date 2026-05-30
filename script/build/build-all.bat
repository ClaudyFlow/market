@echo off
chcp 65001 >nul

set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%..\.."

start "Backend" cmd /c "cd /d "%PROJECT_ROOT%\backend" && "%SCRIPT_DIR%backend\start-backend.bat""
start "Frontend" cmd /c "cd /d "%PROJECT_ROOT%\frontend" && "%SCRIPT_DIR%frontend\start-frontend.bat""