@echo off
chcp 65001 >nul
title PostgreSQL 连接诊断工具

echo ========================================
echo   PostgreSQL 连接诊断
echo ========================================
echo.

echo 检测到以下 PostgreSQL 服务:
Get-Service -Name *postgre* | ForEach-Object {
    echo   [%_.Status%%] %_.DisplayName
}
echo.

echo 端口监听情况:
netstat -ano | findstr :5432 | findstr LISTENING
echo.

echo 尝试连接测试...
echo.

echo [测试 1] 尝试以 postgres 用户连接(无密码):
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d postgres -h localhost -c "SELECT current_user, version();" 2>&1 | Select-String -Pattern "current_user|ERROR|致命"
echo.

echo [测试 2] 尝试以 market 用户连接(密码: market):
set PGPASSWORD=market
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U market -d market -h localhost -c "SELECT current_user, version();" 2>&1 | Select-String -Pattern "current_user|ERROR|致命"
echo.

echo [测试 3] 查看现有数据库列表:
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d postgres -c "\l" 2>&1 | Select-String -Pattern "market|ERROR|致命" | Select-Object -First 10
echo.

echo ========================================
echo 诊断完成
echo.
echo 常见问题与解决:
echo   1. 密码错误: 修改 pg_hba.conf 为 trust，重启服务，重置密码
echo   2. 连接拒绝: 检查 postgresql.conf 的 listen_addresses
echo   3. 用户不存在: 以 postgres 用户登录后 CREATE USER
echo ========================================
pause
