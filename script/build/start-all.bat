@echo off
chcp 936 >nul
title 市场平台 - 完整启动脚本

echo ========================================
echo 市场平台后端启动脚本
echo ========================================
echo.

echo [当前环境]
echo   操作系统: Windows
echo   Java: 检查中...
echo   Maven: 检查中...
echo.

:: 检查 Java
java -version 2>&1 | find "version" >nul
if %errorLevel% equ 0 (
    echo   ✓ Java 已安装
    java -version 2>&1 | findstr "version"
) else (
    echo   ✗ Java 未安装或未在 PATH 中
    echo     请安装 JDK 21 或更高版本
    pause
    exit /b 1
)

echo.

:: 检查 Maven
mvn -version 2>&1 | find "Apache Maven" >nul
if %errorLevel% equ 0 (
    echo   ✓ Maven 已安装
    mvn -version 2>&1 | findstr "Apache Maven"
) else (
    echo   ✗ Maven 未安装或未在 PATH 中
    echo     请安装 Maven 3.8+
    pause
    exit /b 1
)

echo.
echo [数据库状态检查]
echo   检查 PostgreSQL 10 服务...
sc query postgresql-x64-10 | find "RUNNING" >nul
if %errorLevel% equ 0 (
    echo   ✓ PostgreSQL 10 正在运行 (端口 5432)
) else (
    echo   ! PostgreSQL 10 未运行
    echo     尝试启动...
    net start postgresql-x64-10 2>nul
    timeout /t 3 /nobreak >nul
    sc query postgresql-x64-10 | find "RUNNING" >nul
    if %errorLevel% neq 0 (
        echo   ✗ 无法启动 PostgreSQL 10
        echo     请手动启动服务:
        echo       1. 打开 services.msc
        echo       2. 找到 "PostgreSQL 10"
        echo       3. 右键启动
        pause
        exit /b 1
    )
    echo   ✓ PostgreSQL 10 已启动
)

echo.
echo [数据库初始化检查]
echo   检查 market 用户和数据库是否存在...
echo.

set PGPASSWORD=market
"C:\Program Files\PostgreSQL\10\bin\psql.exe" -U market -d market -c "SELECT 1;" 2>nul
if %errorLevel% equ 0 (
    echo   ✓ market 数据库已就绪
    goto :skip_init
)

echo   ✗ market 数据库未找到或无法访问
echo.
echo   解决方案 A: 使用 pgAdmin 手动创建
echo      1. 打开 pgAdmin 4
echo      2. 连接到 PostgreSQL 10
echo      3. 执行 SQL:
echo         CREATE USER market WITH PASSWORD 'market' CREATEDB;
echo         CREATE DATABASE market OWNER market;
echo.
echo   解决方案 B: 修改 pg_hba.conf 允许本地连接
echo      1. 编辑: C:\Program Files\PostgreSQL\10\data\pg_hba.conf
echo      2. 将 local 和 host 行的 md5 改为 trust
echo      3. 重启 PostgreSQL 服务
echo      4. 再次运行本脚本
echo.
echo   解决方案 C: 以管理员身份运行 init 脚本
echo      cd D:\market\script\build
echo      init-postgresql-local.bat
echo.
pause
exit /b 1

:skip_init
echo.
echo [启动后端应用]
echo   使用 Maven 运行 Spring Boot...
echo   配置文件: application-dev.properties (dev profile)
echo   数据库: jdbc:postgresql://localhost:5432/market
echo   账号: market/market
echo.
echo   注意: 首次运行会下载所有依赖(约 3-5 分钟)
echo         日志将实时显示在控制台
echo.
echo   按 Ctrl+C 停止服务器
echo ========================================
echo.

cd backend
mvn spring-boot:run

if %errorLevel% neq 0 (
    echo.
    echo Maven 执行失败，错误码: %errorLevel%
    echo.
    echo 可能原因:
    echo   1. 端口 8080 被占用
    echo   2. 数据库连接失败
    echo   3. 依赖下载失败
    echo.
    echo 查看详细日志: D:\market\log\market.*.log
    pause
)

exit /b 0
