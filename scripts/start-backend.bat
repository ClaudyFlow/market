@echo off
chcp 65001 >nul
echo ========================================
echo     市场平台 - 启动后端服务
echo     Market Platform - Start Backend
echo ========================================
echo.

cd /d "%~dp0.."

echo [检查] Java 环境...
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [错误] 未找到 Java，请先安装 JDK 21+
    pause
    exit /b 1
)
echo [成功] Java 已安装
java --version
echo.

echo [启动] Spring Boot 后端服务...
cd /d "%~dp0.."
if exist "target\market-platform-1.0.0.jar" (
    echo [运行] 使用已打包的 JAR 文件...
    start "" java -jar target\market-platform-1.0.0.jar
) else if exist "pom.xml" (
    echo [运行] 使用 Maven 启动...
    start "" mvn spring-boot:run
) else (
    echo [错误] 未找到后端项目
    pause
    exit /b 1
)

echo [成功] 后端服务启动中...
echo.
echo 后端 API 地址：http://localhost:8080/api/
echo.
timeout /t 3 /nobreak >nul
pause
