@echo off
chcp 65001 >nul

set "SCRIPT_DIR=%~dp0"

:: 启动后端
start "Backend" cmd /c "%SCRIPT_DIR%backend\start-backend.bat"

:: 启动前端
start "Frontend" cmd /c "%SCRIPT_DIR%frontend\start-frontend.bat"
