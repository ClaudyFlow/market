# 数据库测试运行脚本
param(
    [string]$DbUrl = "jdbc:h2:mem:testdb",
    [string]$DbUser = "sa",
    [string]$DbPassword = "",
    [string]$SqlFile = "database-test.sql"
)

Write-Host "=== 数据库功能测试 ===" -ForegroundColor Cyan
Write-Host "数据库URL: $DbUrl"
Write-Host "SQL文件: $SqlFile"
Write-Host ""

if (-Not (Test-Path $SqlFile)) {
    Write-Host "错误: SQL 文件 '$SqlFile' 不存在!" -ForegroundColor Red
    exit 1
}

# 检查是否有H2 JAR
$h2Jar = Get-ChildItem -Path .\ -Filter h2*.jar -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
if (-Not $h2Jar) {
    $h2Jar = Get-ChildItem -Path ..\backend\target\dependency\ -Filter h2*.jar -ErrorAction SilentlyContinue | Select-Object -First 1
}
if (-Not $h2Jar) {
    $h2Jar = Get-ChildItem -Path ../../backend/target/dependency/ -Filter h2*.jar -ErrorAction SilentlyContinue | Select-Object -First 1
}

if ($h2Jar) {
    Write-Host "找到 H2 JAR: $($h2Jar.FullName)" -ForegroundColor Green
    & java -cp "`$($h2Jar.FullName)`" org.h2.tools.Shell -url $DbUrl -user $DbUser -password $DbPassword -sql $SqlFile
    exit $LASTEXITCODE
} else {
    Write-Host "警告: 未找到 H2 JAR 文件。" -ForegroundColor Yellow
    Write-Host "请手动执行以下命令之一：" -ForegroundColor Yellow
    Write-Host "1. 如果您有 H2 数据库命令行工具:" -ForegroundColor Yellow
    Write-Host "   java -cp h2.jar org.h2.tools.Shell -url $DbUrl -user $DbUser -password $DbPassword -sql $SqlFile" -ForegroundColor Yellow
    Write-Host "2. 或者使用数据库客户端直接连接到 $DbUrl 执行 $SqlFile 中的 SQL" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "注意: 此测试脚本使用了内存模式 H2 数据库 (jdbc:h2:mem:testdb)，每次运行都是全新的数据库。" -ForegroundColor Yellow
    exit 0
}