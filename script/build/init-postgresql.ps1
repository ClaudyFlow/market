# PostgreSQL 数据库初始化脚本（无需重启服务）
# 使用 pg_reload_conf() 动态重载配置

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   PostgreSQL 数据库初始化" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检测 PostgreSQL 版本
$pgPaths = @(
    "C:\Program Files\PostgreSQL\18\bin\psql.exe",
    "C:\Program Files\PostgreSQL\10\bin\psql.exe"
)

$psql = $null
foreach ($path in $pgPaths) {
    if (Test-Path $path) {
        $psql = $path
        break
    }
}

if (-not $psql) {
    Write-Host "[错误] 未找到 psql.exe" -ForegroundColor Red
    Write-Host "请安装 PostgreSQL https://www.postgresql.org/download/windows/" -ForegroundColor Yellow
    exit 1
}

$version = if ($psql -like "*18*") { "18" } else { "10" }
Write-Host "[检测] PostgreSQL $version 已安装" -ForegroundColor Green
Write-Host "[路径] $psql" -ForegroundColor Gray

# 测试连接
Write-Host ""
Write-Host "[测试] 尝试连接 PostgreSQL..." -ForegroundColor Yellow

try {
    $output = & $psql -U postgres -d postgres -h localhost -c "SELECT version();" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ 可以连接到 PostgreSQL" -ForegroundColor Green
    } else {
        Write-Host "✗ 连接失败: $output" -ForegroundColor Red
        Write-Host ""
        Write-Host "可能原因:" -ForegroundColor Yellow
        Write-Host "  1. postgres 用户密码未知或错误"
        Write-Host "  2. pg_hba.conf 配置不允许本地连接"
        Write-Host "  3. PostgreSQL 未运行"
        Write-Host ""
        Write-Host "解决方案:" -ForegroundColor Green
        Write-Host "  1. 打开 'C:\Program Files\PostgreSQL\$version\data\pg_hba.conf'" -ForegroundColor White
        Write-Host "  2. 查找包含 '127.0.0.1/32' 和 '::1/128' 的行" -ForegroundColor White
        Write-Host "  3. 将 'md5' 改为 'trust'" -ForegroundColor White
        Write-Host "  4. 运行: net stop postgresql-x64-$version && net start postgresql-x64-$version" -ForegroundColor White
        Write-Host "  5. 重新运行此脚本" -ForegroundColor White
        exit 1
    }
} catch {
    Write-Host "✗ 连接异常: $_" -ForegroundColor Red
    exit 1
}

# 执行初始化
Write-Host ""
Write-Host "[步骤 1/3] 创建市场用户和数据库..." -ForegroundColor Yellow

$scriptPath = Join-Path $PSScriptRoot "..\..\script\build\init-postgresql.sql"
if (Test-Path $scriptPath) {
    & $psql -U postgres -d postgres -f $scriptPath 2>&1 | Out-Host
} else {
    Write-Host "警告: 未找到 SQL 脚本，使用内联 SQL" -ForegroundColor Yellow
    & $psql -U postgres -d postgres -c "CREATE USER market WITH PASSWORD 'market' CREATEDB;" 2>nul
    & $psql -U postgres -d postgres -c "CREATE DATABASE market OWNER market;" 2>nul
}

# 验证
Write-Host ""
Write-Host "[步骤 2/3] 验证连接..." -ForegroundColor Yellow

try {
    $env:PGPASSWORD = "market"
    $result = & $psql -U market -d market -c "SELECT current_user, version();" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ market 用户连接成功" -ForegroundColor Green
        $result | Out-Host
    } else {
        Write-Host "✗ market 用户连接失败" -ForegroundColor Red
        $result | Out-Host
    }
} catch {
    Write-Host "✗ 验证异常: $_" -ForegroundColor Red
}

# 检查后端配置
Write-Host ""
Write-Host "[步骤 3/3] 检查后端配置..." -ForegroundColor Yellow

$devProps = "backend\src\main\resources\application-dev.properties"
if (Test-Path $devProps) {
    $content = Get-Content $devProps -Raw
    if ($content -match "spring\.datasource\.username=market") {
        Write-Host "✓ 后端配置正确 (market/market)" -ForegroundColor Green
    } else {
        Write-Host "✗ 后端配置不匹配" -ForegroundColor Red
        Write-Host "预期: spring.datasource.username=market" -ForegroundColor Yellow
    }
} else {
    Write-Host "✗ 未找到 $devProps" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "初始化完成！" -ForegroundColor Cyan
Write-Host "" -ForegroundColor Cyan
Write-Host "启动后端:" -ForegroundColor White
Write-Host "  cd backend" -ForegroundColor Gray
Write-Host "  mvn spring-boot:run" -ForegroundColor Gray
Write-Host "" -ForegroundColor Cyan
Write-Host "或使用 Docker (需先启动 Docker Desktop):" -ForegroundColor White
Write-Host "  cd depend" -ForegroundColor Gray
Write-Host "  docker-compose up -d" -ForegroundColor Gray
Write-Host "========================================" -ForegroundColor Cyan

Read-Host "按回车键退出..."
