@echo off
chcp 65001 >nul
echo ========================================
echo   市场平台 - 后端服务启动脚本
echo ========================================
echo.

REM 检查 Java 是否安装
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Java，请先安装 Java 21+
    echo.
    echo 下载地址：https://adoptium.net/
    pause
    exit /b 1
)

echo [信息] 正在检查 Maven...

REM 检查是否有 Maven wrapper
if exist ".mvn\wrapper\maven-wrapper.jar" (
    echo [信息] 使用 Maven Wrapper 启动...
    mvnw spring-boot:run -pl backend
    goto :end
)

REM 检查系统 Maven
where mvn >nul 2>&1
if %errorlevel% equ 0 (
    echo [信息] 使用系统 Maven 启动...
    cd /d %~dp0
    mvn spring-boot:run -pl backend
    goto :end
)

REM 都没有 Maven，尝试直接编译运行
echo [警告] 未找到 Maven，尝试直接编译运行...
echo.

if not exist "backend\target\classes" (
    echo [错误] 未找到编译后的类文件
    echo.
    echo 请先使用以下方式之一编译项目:
    echo   1. 在 IDE 中构建项目 (推荐)
    echo   2. 安装 Maven 后运行：mvn compile -pl backend
    echo.
    pause
    exit /b 1
)

echo [信息] 使用已编译的类文件启动...
cd backend
java -cp target/classes;target/dependency/* com.market.MarketApplication

:end
echo.
echo ========================================
echo   服务已停止
echo ========================================
pause
