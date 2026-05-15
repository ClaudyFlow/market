@echo off
:: 需要以管理员身份运行
:: 此脚本会修改 PostgreSQL 配置，请谨慎使用

echo 注意：此脚本将修改 PostgreSQL 配置以允许本地连接
echo 修改文件: C:\Program Files\PostgreSQL\10\data\pg_hba.conf
echo 修改内容: 将 md5 改为 trust
echo.
pause

:: 备份配置文件
copy "C:\Program Files\PostgreSQL\10\data\pg_hba.conf" "C:\Program Files\PostgreSQL\10\data\pg_hba.conf.bak" >nul
echo 已备份 pg_hba.conf 到 .bak

:: 将所有 md5 替换为 trust
powershell -Command "(Get-Content 'C:\Program Files\PostgreSQL\10\data\pg_hba.conf') -replace 'md5', 'trust' | Set-Content 'C:\Program Files\PostgreSQL\10\data\pg_hba.conf'"
echo 已将认证方式改为 trust

:: 重启 PostgreSQL 10
net stop postgresql-x64-10
timeout /t 2 /nobreak >nul
net start postgresql-x64-10
echo PostgreSQL 10 已重启

timeout /t 3 /nobreak >nul

:: 创建用户和数据库
echo 创建 market 用户和数据库...
"C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -d postgres -c "CREATE USER market WITH PASSWORD 'market' CREATEDB;" 2>nul
"C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -d postgres -c "CREATE DATABASE market OWNER market;" 2>nul

:: 验证
echo 验证连接...
set PGPASSWORD=market
"C:\Program Files\PostgreSQL\10\bin\psql.exe" -U market -d market -c "SELECT 'OK' AS status;"

:: 恢复配置（重要！）
echo.
echo 恢复安全配置...
powershell -Command "(Get-Content 'C:\Program Files\PostgreSQL\10\data\pg_hba.conf') -replace 'trust', 'md5' | Set-Content 'C:\Program Files\PostgreSQL\10\data\pg_hba.conf'"
net stop postgresql-x64-10
timeout /t 2 /nobreak >nul
net start postgresql-x64-10
echo 配置已恢复，PostgreSQL 已重启

echo.
echo 数据库初始化完成！
echo 用户: market
echo 密码: market
echo 数据库: market
echo.
echo 现在可以启动后端:
echo   cd backend
echo   mvn spring-boot:run
echo.
pause
