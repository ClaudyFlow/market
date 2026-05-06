# 压力测试脚本 - 模拟多用户并发操作
# 用法：Powershell -ExecutionPolicy Bypass -File .\load-test.ps1 -Users 10 -Requests 50

param(
    [int]$Users = 10,
    [int]$RequestsPerUser = 20
)

$baseUrl = "http://localhost:8080/api"
$results = @()
$lock = [System.Object]::new()

function Invoke-UserScenario {
    param(
        [int]$UserId
    )
    $userName = "load_test_user_$UserId"
    $email = "$userId@test.com"
    $password = "LoadTest123456"
    
    # 1. 注册
    try {
        $reg = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body (
            @{ name=$userName; email=$email; password=$password; confirmPassword=$password } | ConvertTo-Json
        ) -ContentType "application/json" -ErrorAction Stop
        $token = $reg.token
    } catch {
        # 可能已存在，尝试登录
        $login = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body (
            @{ name=$userName; password=$password } | ConvertTo-Json
        ) -ContentType "application/json" -ErrorAction SilentlyContinue
        $token = $login.token
    }
    
    if (-not $token) {
        Write-Warning "用户 $userName 无法获取token"
        return
    }
    
    $headers = @{ Authorization = "Bearer $token" }
    $localResults = @()
    
    for ($i = 1; $i -le $RequestsPerUser; $i++) {
        $start = Get-Date
        $success = $false
        $errorMsg = $null
        $endpoint = ""
        
        try {
            # 随机选择一个操作
            $op = Get-Random -Minimum 1 -Maximum 8
            switch ($op) {
                1 { # 获取商品列表
                    $resp = Invoke-RestMethod -Uri "$baseUrl/product?page=1&size=10" -Method Get -Headers $headers -ErrorAction Stop
                    $success = $true
                    $endpoint = "GET /product"
                }
                2 { # 获取用户信息
                    $resp = Invoke-RestMethod -Uri "$baseUrl/user/info" -Method Get -Headers $headers -ErrorAction Stop
                    $success = $true
                    $endpoint = "GET /user/info"
                }
                3 { # 获取购物车
                    $resp = Invoke-RestMethod -Uri "$baseUrl/cart" -Method Get -Headers $headers -ErrorAction Stop
                    $success = $true
                    $endpoint = "GET /cart"
                }
                4 { # 获取VIP等级
                    $resp = Invoke-RestMethod -Uri "$baseUrl/user/vip/levels" -Method Get -Headers $headers -ErrorAction Stop
                    $success = $true
                    $endpoint = "GET /vip/levels"
                }
                5 { # 获取奖品列表
                    $resp = Invoke-RestMethod -Uri "$baseUrl/lottery/prizes" -Method Get -Headers $headers -ErrorAction Stop
                    $success = $true
                    $endpoint = "GET /lottery/prizes"
                }
                6 { # 获取消息列表
                    $resp = Invoke-RestMethod -Uri "$baseUrl/message/list" -Method Get -Headers $headers -ErrorAction Stop
                    $success = $true
                    $endpoint = "GET /message/list"
                }
                7 { # 获取公告列表（可能失败）
                    $resp = Invoke-RestMethod -Uri "$baseUrl/announcement/list" -Method Get -Headers $headers -ErrorAction Stop
                    $success = $true
                    $endpoint = "GET /announcement/list"
                }
            }
        } catch {
            $errorMsg = $_.Exception.Message
            $endpoint = if ($endpoint) { $endpoint } else { "未知" }
        }
        
        $duration = (Get-Date) - $start
        $result = [PSCustomObject]@{
            UserId = $UserId
            RequestIndex = $i
            Endpoint = $endpoint
            Success = $success
            DurationMs = [math]::Round($duration.TotalMilliseconds, 2)
            Error = $errorMsg
            Timestamp = Get-Date -Format "HH:mm:ss.fff"
        }
        $localResults += $result
        
        # 小延迟避免过快
        Start-Sleep -Milliseconds (Get-Random -Minimum 10 -Maximum 100)
    }
    
    # 将结果添加到全局集合（线程安全）
    [System.Threading.Monitor]::Enter($lock)
    try {
        $results += $localResults
    } finally {
        [System.Threading.Monitor]::Exit($lock)
    }
    
    Write-Host "用户 $userName 完成 $RequestsPerUser 次请求" -ForegroundColor Cyan
}

# 主程序
Write-Host "===== 压力测试开始 =====" -ForegroundColor Yellow
Write-Host "并发用户数: $Users"
Write-Host "每个用户请求数: $RequestsPerUser"
Write-Host "总请求数: $($Users * $RequestsPerUser)"
Write-Host "开始时间: $(Get-Date -Format 'HH:mm:ss')" -ForegroundColor Yellow

$startTime = Get-Date

# 使用 runspace 并发执行
$runspacePool = [runspacefactory]::CreateRunspacePool(1, $Users)
$runspacePool.Open()

$jobs = @()
for ($i = 1; $i -le $Users; $i++) {
    $powershell = [powershell]::Create()
    $powershell.RunspacePool = $runspacePool
    [void]$powershell.AddScript({
        param($baseUrl, $userIndex, $reqPerUser)
        # 注册/登录
        $userName = "load_test_user_$userIndex"
        $email = "$userIndex@test.com"
        $password = "LoadTest123456"
        try {
            $reg = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body (@{name=$userName;email=$email;password=$password;confirmPassword=$password} | ConvertTo-Json) -ContentType "application/json" -ErrorAction Stop
            $token = $reg.token
        } catch {
            try {
                $login = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body (@{name=$userName;password=$password} | ConvertTo-Json) -ContentType "application/json" -ErrorAction SilentlyContinue
                $token = $login.token
            } catch {
                $token = $null
            }
        }
        if (-not $token) { return @() }
        $headers = @{ Authorization = "Bearer $token" }
        $localResults = @()
        $endpoints = @(
            "$baseUrl/product?page=1&size=5",
            "$baseUrl/user/info",
            "$baseUrl/cart",
            "$baseUrl/user/vip/levels",
            "$baseUrl/lottery/prizes",
            "$baseUrl/message/list"
        )
        for ($j = 1; $j -le $reqPerUser; $j++) {
            $ep = $endpoints | Get-Random
            $sw = [System.Diagnostics.Stopwatch]::StartNew()
            try {
                $resp = Invoke-RestMethod -Uri $ep -Method Get -Headers $headers -ErrorAction Stop
                $success = $true
                $err = $null
            } catch {
                $success = $false
                $err = $_.Exception.Message
            }
            $sw.Stop()
            $localResults += [PSCustomObject]@{
                UserId = $userIndex
                Request = $j
                Endpoint = $ep
                Success = $success
                DurationMs = $sw.ElapsedMilliseconds
                Error = $err
                Timestamp = (Get-Date).ToString("HH:mm:ss.fff")
            }
            Start-Sleep -Milliseconds (Get-Random -Minimum 5 -Maximum 50)
        }
        return $localResults
    }).AddArgument($baseUrl).AddArgument($i).AddArgument($RequestsPerUser)
    $powershell.BeginInvoke()
    $jobs += @{
        PowerShell = $powershell
        AsyncResult = $powershell.BeginInvoke()
    }
}

# 等待所有任务完成
$allResults = @()
foreach ($job in $jobs) {
    $job.PowerShell.EndInvoke($job.AsyncResult) | ForEach-Object { $allResults += $_ }
    $job.PowerShell.Dispose()
}
$runspacePool.Close()
$runspacePool.Dispose()

$endTime = Get-Date
$duration = $endTime - $startTime

# 汇总统计
$totalRequests = $allResults.Count
$successfulRequests = ($allResults | Where-Object { $_.Success }).Count
$failedRequests = $totalRequests - $successfulRequests
$avgDuration = ($allResults | Measure-Object -Property DurationMs -Average).Average
$minDuration = ($allResults | Measure-Object -Property DurationMs -Minimum).Minimum
$maxDuration = ($allResults | Measure-Object -Property DurationMs -Maximum).Maximum

Write-Host "`n===== 压力测试完成 =====" -ForegroundColor Yellow
Write-Host "总耗时: $($duration.ToString('mm\:ss\.fff'))"
Write-Host "总请求数: $totalRequests"
Write-Host "成功数: $successfulRequests"
Write-Host "失败数: $failedRequests"
Write-Host "成功率: $([math]::Round($successfulRequests/$totalRequests*100, 2))%"
Write-Host "平均响应时间: $([math]::Round($avgDuration, 2)) ms"
Write-Host "最小响应时间: $minDuration ms"
Write-Host "最大响应时间: $maxDuration ms"

# 按端点统计
Write-Host "`n===== 各端点统计 =====" -ForegroundColor Yellow
$allResults | Group-Object Endpoint | ForEach-Object {
    $ep = $_.Name
    $count = $_.Count
    $succ = ($_.Group | Where-Object Success).Count
    $fail = $count - $succ
    $avg = ($_.Group | Measure-Object -Property DurationMs -Average).Average
    Write-Host "$ep : 总 $count, 成功 $succ, 失败 $fail, 平均 ${avg}ms"
}

# 保存详细结果
$report = [PSCustomObject]@{
    TotalTime = $duration.ToString()
    TotalRequests = $totalRequests
    Successful = $successfulRequests
    Failed = $failedRequests
    SuccessRate = [math]::Round($successfulRequests/$totalRequests*100, 2)
    AvgResponseMs = [math]::Round($avgDuration, 2)
    MinResponseMs = $minDuration
    MaxResponseMs = $maxDuration
    Details = $allResults
}
$report | ConvertTo-Json -Depth 5 | Out-File "D:/market/testing/load-test-report.json" -Encoding utf8
Write-Host "`n详细报告已保存到 D:/market/testing/load-test-report.json" -ForegroundColor Green