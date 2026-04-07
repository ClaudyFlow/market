@echo off
REM ========================================
REM 前后端联调测试自动化脚本 (Windows)
REM 功能: 启动后端服务 -> 运行测试 -> 生成报告
REM ========================================

echo.
echo ========================================
echo   前后端联调测试自动化脚本
echo ========================================
echo.

REM 设置变量
set BACKEND_PORT=8080
set TEST_PROFILE=integration
set WAIT_TIME=30

echo [1/5] 检查环境...
echo.

REM 检查 Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到 Java
    echo 请先安装 JDK 21 或更高版本
    pause
    exit /b 1
)

REM 检查 Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到 Maven
    echo 请先安装 Maven
    pause
    exit /b 1
)

REM 检查 Node.js
node --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未找到 Node.js
    echo 请先安装 Node.js
    pause
    exit /b 1
)

echo ✅ 环境检查通过
echo.

echo [2/5] 清理之前的测试数据...
echo.

REM 清理之前的构建
cd backend
call mvn clean -q
cd ..

echo ✅ 清理完成
echo.

echo [3/5] 启动后端服务 (测试模式)...
echo.

REM 启动后端（后台运行）
cd backend
start "Market Backend (Integration Test)" cmd /k "mvn spring-boot:run -Dspring-boot.run.profiles=%TEST_PROFILE% -Dserver.port=%BACKEND_PORT%"
cd ..

echo ⏳ 等待后端服务启动 (%WAIT_TIME% 秒)...
timeout /t %WAIT_TIME% /nobreak >nul

echo.
echo ✅ 后端服务已启动
echo.

echo [4/5] 执行联调测试...
echo.

REM 运行前端联调测试脚本
node integration-test.js

REM 保存测试结果
set TEST_EXIT_CODE=%errorlevel%

echo.
echo [5/5] 测试完成，正在清理...
echo.

REM 询问是否关闭后端服务
echo.
choice /M "是否关闭后端服务"
if errorlevel 2 (
    echo ⚠️  后端服务保持运行
    echo 提示: 可以手动关闭后端窗口
) else (
    echo ⚠️  请手动关闭后端服务窗口
)

echo.
echo ========================================
if %TEST_EXIT_CODE% equ 0 (
    echo   ✅ 联调测试全部通过
) else (
    echo   ❌ 联调测试存在失败
)
echo ========================================
echo.
echo 📄 测试报告: integration-test-report.html
echo.

pause
