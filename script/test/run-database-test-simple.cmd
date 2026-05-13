@echo off
REM Simple database test runner - CMD version
REM Provides instructions for running database-test.sql

echo ===== 数据库功能测试 =====
echo.
echo SQL测试脚本: database-test.sql
echo.

if not exist "database-test.sql" (
    echo 错误: 未找到 database-test.sql 文件!
    exit /b 1
)

echo 找到测试脚本: database-test.sql
echo.

echo 请根据您的数据库类型选择运行方式:
echo.
echo 1. 如果使用 H2 数据库:
echo    java -cp h2.jar org.h2.tools.Shell -url jdbc:h2:mem:testdb -user sa -password -sql database-test.sql
echo.
echo 2. 如果使用 PostgreSQL (项目默认):
echo    确保PostgreSQL服务运行中
echo    使用 psql 或其他客户端连接到数据库
echo    然后手动执行 database-test.sql 中的命令
echo.
echo 3. 查看详细说明:
echo    type DATABASE_TEST_GUIDE.md
echo.

echo 注意: 此脚本仅提供运行指南，实际执行需要根据您的环境配置。
echo.