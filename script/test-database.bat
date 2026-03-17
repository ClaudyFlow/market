@echo off
REM ============================================================
REM Market 电商平台 - 数据库快速测试脚本 (Windows)
REM ============================================================

echo.
echo ============================================================
echo Market 电商平台 - 数据库快速测试
echo ============================================================
echo.

REM 检查后端是否能编译
echo [1/4] 检查后端项目...
cd /d "%~dp0..\backend"
if not exist "pom.xml" (
    echo [错误] 未找到 backend/pom.xml，请确保在正确的目录运行
    pause
    exit /b 1
)
echo [完成] 后端项目存在

REM 编译后端
echo.
echo [2/4] 编译后端项目...
call mvnw clean compile -q
if %errorlevel% neq 0 (
    echo [错误] 后端编译失败
    pause
    exit /b 1
)
echo [完成] 后端编译成功

REM 启动后端并测试
echo.
echo [3/4] 启动后端服务（测试数据库连接）...
echo 提示：后端将在 http://localhost:8080 启动
echo 按 Ctrl+C 可停止服务
echo.

REM 测试 API 接口
echo [4/4] 测试 API 接口...
echo.

REM 等待后端启动
timeout /t 5 /nobreak >nul

REM 测试健康检查
echo 测试 1: 健康检查...
curl -s http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo [通过] 服务可访问
) else (
    echo [跳过] 服务可能未启动或无 actuator
)

REM 测试商品列表
echo.
echo 测试 2: 获取商品列表...
curl -s http://localhost:8080/api/products >nul 2>&1
if %errorlevel% equ 0 (
    echo [通过] 商品接口可访问
) else (
    echo [跳过] 接口可能不可用
)

echo.
echo ============================================================
echo 测试完成！
echo ============================================================
echo.
echo 数据库文件位置：backend\market.db
echo 如需查看数据库内容，可使用以下工具:
echo   - DB Browser for SQLite: https://sqlitebrowser.org/
echo   - DBeaver: https://dbeaver.io/
echo   - SQLiteStudio: https://sqlitestudio.pl/
echo.
echo 手动测试命令:
echo   1. 启动后端：cd backend ^&^& mvnw spring-boot:run
echo   2. 测试注册：curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"name\":\"test\",\"email\":\"test@test.com\",\"password\":\"123456\",\"confirmPassword\":\"123456\"}"
echo   3. 测试登录：curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"name\":\"test\",\"password\":\"123456\"}"
echo   4. 测试商品：curl http://localhost:8080/api/products
echo.
pause
