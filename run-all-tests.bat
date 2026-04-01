@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo   API 可用性检测注解 - 快速测试
echo ========================================
echo.

set TEST_DIR=%~dp0backend\src\test\java
set CLASS_DIR=%~dp0backend\src\main\java

echo [步骤 1/3] 运行注解集成测试...
echo ----------------------------------------
cd /d %TEST_DIR%
java com.market.aspect.ApiAvailabilityIntegrationTest
if %errorlevel% neq 0 (
    echo.
    echo [错误] 注解集成测试失败!
    echo.
    pause
    exit /b 1
)
echo.

echo [步骤 2/3] 运行状态码测试...
echo ----------------------------------------
cd /d %TEST_DIR%
java com.market.common.ApiStatusCodeTest
if %errorlevel% neq 0 (
    echo.
    echo [错误] 状态码测试失败!
    echo.
    pause
    exit /b 1
)
echo.

echo [步骤 3/3] 检查服务状态...
echo ----------------------------------------
echo 正在检查后端服务是否运行...
curl -s http://localhost:8080/api/health/check >nul 2>&1
if %errorlevel% equ 0 (
    echo.
    echo ✓ 后端服务运行正常
    echo.
    echo 是否运行集成测试？(Y/N)
    set /p RUN_INTEGRATION="输入选择："
    if /i "!RUN_INTEGRATION!"=="Y" (
        echo.
        echo 运行集成测试...
        call %~dp0test-availability.bat
    ) else (
        echo.
        echo 跳过集成测试
    )
) else (
    echo.
    echo ⚠ 后端服务未运行，跳过集成测试
    echo.
    echo 提示：启动服务后运行 test-availability.bat 进行集成测试
    echo   启动方法:
    echo   1. 在 IDE 中运行 MarketApplication.java
    echo   2. 或使用命令行：mvn spring-boot:run -pl backend
)

echo.
echo ========================================
echo   测试完成!
echo ========================================
echo.
echo 查看详细测试流程：
echo   backend\src\test\java\com\market\aspect\完整测试流程.md
echo.
pause
