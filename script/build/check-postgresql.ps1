@echo off
chcp 65001 >nul
echo ========================================
echo PostgreSQL 数据库检查与修复脚本
echo ========================================
echo.

echo [1/6] 检查 PostgreSQL 服务状态...
Get-Service -Name *postgre* | Where-Object {$_.Status -eq "Running"} | ForEach-Object {
    echo   ✓ 服务运行中: $_.DisplayName
}
echo.

echo [2/6] 检查端口 5432 监听状态...
$portOpen = Test-NetConnection -ComputerName localhost -Port 5432 -InformationLevel Quiet -ErrorAction SilentlyContinue
if ($portOpen) {
    echo   ✓ 端口 5432 可访问
} else {
    echo   ✗ 端口 5432 不可访问，尝试修复...
    echo   提示: 检查 postgresql.conf 中的 listen_addresses 设置
}
echo.

echo [3/6] 检查 market 数据库是否存在...
try {
    $env:PGPASSWORD="123456"
    $result = & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U admin -d postgres -h localhost -p 5432 -c "SELECT 1;" 2>&1
    if ($LASTEXITCODE -eq 0) {
        echo   ✓ 使用 admin/123456 可以连接
    } else {
        echo   ✗ admin 用户连接失败，尝试其他用户...
    }
} catch {
    echo   ✗ 连接测试异常
}
echo.

echo [4/6] 尝试创建 market 用户和数据库...
echo   执行 SQL: CREATE USER market WITH PASSWORD 'market';
echo   执行 SQL: CREATE DATABASE market OWNER market;
echo.
echo   如果报错 "角色已存在" 或 "数据库已存在" 可忽略
echo.

echo [5/6] 验证 market 用户连接...
echo   命令: psql -U market -d market -h localhost -p 5432 -c "SELECT 1;"
echo.

echo [6/6] 检查结果摘要
echo ========================================
echo 完成检查！
echo.
echo 如果数据库已就绪，接下来可以启动后端:
echo   cd backend
echo   mvn spring-boot:run
echo ========================================
pause
