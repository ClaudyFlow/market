@echo off
set "SCRIPT_DIR=%~dp0"
call "%SCRIPT_DIR%docker\start-docker.bat"
start "Backend" cmd /c "%SCRIPT_DIR%backend\start-backend.bat"
start "Frontend" cmd /c "%SCRIPT_DIR%frontend\start-frontend.bat"
