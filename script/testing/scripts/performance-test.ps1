# 性能基准测试脚本 - 测量API响应时间和吞吐量
# 用法：Powershell -ExecutionPolicy Bypass -File .\performance-test.ps1

$baseUrl = "http://localhost:8080/api"

# 准备：获取token
try {
    $login = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body (@{name="performance_test";password="Test123456"} | ConvertTo-Json) -ContentType "application/json" -ErrorAction SilentlyContinue
    if (-not $login.token) {
        $reg = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body (@{name="performance_test";email="perf@test.com";password="Test123456";confirmPassword="Test123456"} | ConvertTo-Json) -ContentType "application/json" -ErrorAction Stop
        $token = $reg.token
    } else {
        $token = $login.token
    }
} catch {
    Write-Host "无法获取token: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
$headers = @{ Authorization = "Bearer $token" }

$endpoints = @(
    @{ Name = "用户信息"; Url = "$baseUrl/user/info"; Method = "GET" },
    @{ Name = "商品列表"; Url = "$baseUrl/product?page=1&size=10"; Method = "GET" },
    @{ Name = "购物车"; Url = "$baseUrl/cart"; Method = "GET" },
    @{ Name = "VIP等级"; Url = "$baseUrl/user/vip/levels"; Method = "GET" },
    @{ Name = "奖品列表"; Url = "$baseUrl/lottery/prizes"; Method = "GET" },
    @{ Name = "消息列表"; Url = "$baseUrl/message/list"; Method = "GET" }
)

$iterations = 100
$results = @()

Write-Host "===== 性能基准测试 =====" -ForegroundColor Yellow
Write-Host "迭代次数: $iterations"
Write-Host "开始时间: $(Get-Date -Format 'HH:mm:ss')" -ForegroundColor Yellow

foreach ($ep in $endpoints) {
    Write-Host "`n测试: $($ep.Name)" -ForegroundColor Cyan
    $durations = @()
    $errors = 0
    for ($i = 1; $i -le $iterations; $i++) {
        $sw = [System.Diagnostics.Stopwatch]::StartNew()
        try {
            $resp = Invoke-RestMethod -Uri $ep.Url -Method $ep.Method -Headers $headers -ErrorAction Stop
            $sw.Stop()
            $durations += $sw.ElapsedMilliseconds
        } catch {
            $sw.Stop()
            $errors++
        }
    }
    if ($durations.Count -gt 0) {
        $avg = ($durations | Measure-Object -Average).Average
        $min = ($durations | Measure-Object -Minimum).Minimum
        $max = ($durations | Measure-Object -Maximum).Maximum
        $p95 = ($durations | Sort-Object)[[Math]::Floor($durations.Count * 0.95)]
        $result = [PSCustomObject]@{
            Endpoint = $ep.Name
            Iterations = $iterations
            Errors = $errors
            AvgMs = [math]::Round($avg, 2)
            MinMs = $min
            MaxMs = $max
            P95Ms = $p95
            Throughput = [math]::Round($iterations / ($durations | Measure-Object -Sum).Sum * 1000, 2)
        }
        $results += $result
        Write-Host "  平均: $($result.AvgMs) ms | 最小: $($result.MinMs) ms | 最大: $($result.MaxMs) ms | P95: $($result.P95Ms) ms" -ForegroundColor Green
        Write-Host "  吞吐量: $($result.Throughput) req/s | 错误: $($result.Errors)" -ForegroundColor Green
    } else {
        Write-Host "  所有请求失败" -ForegroundColor Red
    }
}

Write-Host "`n===== 基准测试完成 =====" -ForegroundColor Yellow

# 保存报告
$report = [PSCustomObject]@{
    Timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Iterations = $iterations
    Results = $results
}
$report | ConvertTo-Json -Depth 5 | Out-File "D:/market/testing/performance-report.json" -Encoding utf8
Write-Host "报告已保存到 D:/market/testing/performance-report.json" -ForegroundColor Green

# 生成摘要表格
Write-Host "`n===== 性能摘要 =====" -ForegroundColor Yellow
$results | Format-Table Endpoint, AvgMs, MinMs, MaxMs, P95Ms, Throughput, Errors -AutoSize