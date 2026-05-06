# PostgreSQL 10 临时信任配置脚本
# 用于解决本地开发认证问题

$ErrorActionPreference = "Stop"
$PgHba = "C:\Program Files\PostgreSQL\10\data\pg_hba.conf"
$PgConf = "C:\Program Files\PostgreSQL\10\data\postgresql.conf"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  PostgreSQL 10 配置临时信任模式" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor White

# 检查管理员权限
$currentPrincipal = New-Object Security.Principal.WindowsPrincipal([Security.Principal.WindowsIdentity]::GetCurrent())
if (-not $currentPrincipal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Write-Host "错误: 需要管理员权限！" -ForegroundColor Red
    Write-Host "请以管理员身份运行 PowerShell" -ForegroundColor Yellow
    Read-Host "按回车退出"
    exit 1
}

Write-Host "[1/5] 备份配置文件..."
Copy-Item $PgHba "$PgHba.bak" -Force
Write-Host "  已备份 pg_hba.conf"

# 检查是否需要添加 local trust 规则
$content = Get-Content $PgHba -Raw
if ($content -notmatch "local\s+all\s+all\s+trust") {
    Write-Host "[2/5] 添加 local trust 规则..."
    $localRule = "`n# Added by setup script - local trust for development`nlocal   all             all                                     trust`n"
    Add-Content $PgHba $localRule
    Write-Host "  已添加 local trust"
} else {
    Write-Host "[2/5] local trust 规则已存在，跳过"
}

# 确保 IPv4 和 IPv6 也是 trust（用于 127.0.0.1 和 ::1）
Write-Host "[3/5] 设置 IPv4/IPv6 为 trust..."
(Get-Content $PgHba) | ForEach-Object {
    $_ -replace 'host\s+all\s+all\s+127\.0\.0\.1/32\s+md5', 'host    all             all             127.0.0.1/32            trust'
} | Set-Content $PgHba

(Get-Content $PgHba) | ForEach-Object {
    $_ -replace 'host\s+all\s+all\s+::1/128\s+md5', 'host    all             all             ::1/128                 trust'
} | Set-Content $PgHba

Write-Host "  已将所有 localhost 的 md5 改为 trust"

Write-Host "[4/5] 重启 PostgreSQL 10 服务..."
Stop-Service postgresql-x64-10 -Force
Start-Sleep -Seconds 2
Start-Service postgresql-x64-10
Write-Host "  服务已重启"

Write-Host "[5/5] 验证连接..."
Start-Sleep -Seconds 3
try {
    $result = & "C:\Program Files\PostgreSQL\10\bin\psql.exe" -U postgres -d postgres -c "SELECT current_user;" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ 无密码连接成功！" -ForegroundColor Green
        $result | Out-Host
    } else {
        Write-Host "连接失败: $result" -ForegroundColor Red
    }
} catch {
    Write-Host "异常: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "配置完成！" -ForegroundColor Cyan
Write-Host ""
Write-Host "现在可以执行数据库初始化:" -ForegroundColor White
Write-Host "  & `"C:\Program Files\PostgreSQL\10\bin\psql.exe`" -U postgres -d market -c `"DROP SCHEMA public CASCADE; CREATE SCHEMA public;`"" -ForegroundColor Gray
Write-Host ""
Write-Host "或直接启动后端（会自动初始化）:" -ForegroundColor White
Write-Host "  cd D:\market\backend" -ForegroundColor Gray
Write-Host "  mvn spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "注意: 完成后请将 pg_hba.conf 恢复为 md5 以保证安全！" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

Read-Host "按回车退出"
