@echo off
:: 颜色输出辅助脚本
:: 用法: call color.bat Green "[Success] Build completed"
::       call color.bat Red "[Error] Build failed"

if /i "%1"=="Green" (
    powershell -command "Write-Host '[Success]' -ForegroundColor Green -NoNewline; Write-Host ' %~2'"
) else if /i "%1"=="Red" (
    powershell -command "Write-Host '[Error]' -ForegroundColor Red -NoNewline; Write-Host ' %~2'"
) else if /i "%1"=="Yellow" (
    powershell -command "Write-Host '[Warn]' -ForegroundColor Yellow -NoNewline; Write-Host ' %~2'"
) else if /i "%1"=="Cyan" (
    powershell -command "Write-Host '[Info]' -ForegroundColor Cyan -NoNewline; Write-Host ' %~2'"
) else (
    echo %~2
)
