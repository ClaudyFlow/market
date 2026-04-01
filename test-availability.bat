@echo off
chcp 65001 >nul
echo ========================================
echo   API 可用性检测 - 快速测试脚本
echo ========================================
echo.

set BASE_URL=http://localhost:8080

echo 正在检查服务是否运行...
curl -s %BASE_URL%/api/test/availability/basic >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo [错误] 服务未运行！请先启动后端服务
    echo.
    echo 启动方法:
    echo   1. mvn spring-boot:run -pl backend
    echo   2. 或在 IDE 中运行 MarketApplication.java
    echo.
    pause
    exit /b 1
)

echo 服务运行正常，开始测试...
echo.

echo [测试 1/10] 基础功能测试
echo ----------------------------------------
curl -s %BASE_URL%/api/test/availability/basic
echo.
echo.

echo [测试 2/10] 数据库依赖测试
echo ----------------------------------------
curl -s %BASE_URL%/api/test/availability/database
echo.
echo.

echo [测试 3/10] Redis 依赖测试
echo ----------------------------------------
curl -s %BASE_URL%/api/test/availability/redis
echo.
echo.

echo [测试 4/10] 多依赖测试
echo ----------------------------------------
curl -s %BASE_URL%/api/test/availability/multi-dependencies
echo.
echo.

echo [测试 5/10] 失败时抛出异常测试
echo ----------------------------------------
curl -s %BASE_URL%/api/test/availability/throw-exception
echo.
echo.

echo [测试 6/10] 失败时继续执行测试
echo ----------------------------------------
curl -s %BASE_URL%/api/test/availability/continue-on-failure
echo.
echo.

echo [测试 7/10] 超时测试 (延迟 2 秒，超时 1 秒)
echo ----------------------------------------
echo 预期：1 秒后返回超时错误
curl -s -m 5 %BASE_URL%/api/test/availability/timeout-test?delayMs=2000
echo.
echo.

echo [测试 8/10] 重试机制测试
echo ----------------------------------------
curl -s %BASE_URL%/api/test/availability/retry-test
echo.
echo.

echo [测试 9/10] 健康检查测试
echo ----------------------------------------
curl -s %BASE_URL%/api/test/availability/health-check
echo.
echo.

echo [测试 10/10] 禁用检测测试
echo ----------------------------------------
curl -s %BASE_URL%/api/test/availability/disabled
echo.
echo.

echo ========================================
echo   测试完成!
echo ========================================
echo.
echo 查看详细测试指南：
echo   backend/src/test/java/com/market/aspect/TEST_GUIDE.md
echo.
pause
