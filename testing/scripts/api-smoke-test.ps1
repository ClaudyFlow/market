# 电商平台 API 冒烟测试脚本
# 用法：Powershell -ExecutionPolicy Bypass -File .\api-smoke-test.ps1

$baseUrl = "http://localhost:8080/api"
$testResults = @()
$total = 0
$passed = 0

function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers = @{
            "Authorization" = "Bearer $token"
        },
        [object]$Body = $null,
        [string]$ContentType = "application/json"
    )
    $script:total++
    Write-Host "[$script:total] $Method $Name" -ForegroundColor Cyan
    try {
        $params = @{ Uri = $Url; Method = $Method; Headers = $Headers }
        if ($Body) { $params["Body"] = $Body }
        if ($ContentType) { $params["ContentType"] = $ContentType }
        $resp = Invoke-RestMethod @params -ErrorAction Stop
        $script:passed++
        Write-Host "✅ 通过" -ForegroundColor Green
        return @{ success = $true; data = $resp }
    } catch {
        Write-Host "❌ 失败: $($_.Exception.Message)" -ForegroundColor Red
        return @{ success = $false; error = $_.Exception.Message }
    }
}

# 1. 用户注册
Write-Host "`n===== 用户注册 =====" -ForegroundColor Yellow
$regResp = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body (
    @{ name="smoke_test_$(Get-Date -Format 'HHmmss')"; email="smoke@test.com"; password="Test123456"; confirmPassword="Test123456" } | ConvertTo-Json
) -ContentType "application/json"
if ($regResp.success) {
    $token = $regResp.token
    $passed++; $total++
    Write-Host "✅ 注册成功" -ForegroundColor Green
} else {
    Write-Host "❌ 注册失败: $($regResp.message)" -ForegroundColor Red
    $total++
    # 尝试用已有用户登录
    $loginResp = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body (
        @{ name="smoke_test_temp"; password="Test123456" } | ConvertTo-Json
    ) -ContentType "application/json" -ErrorAction SilentlyContinue
    if ($loginResp -and $loginResp.token) {
        $token = $loginResp.token
        Write-Host "⚠️  使用备用用户登录成功" -ForegroundColor Yellow
    } else {
        Write-Host "❌ 无法获取token，测试终止" -ForegroundColor Red
        exit 1
    }
}

$headers = @{ "Authorization" = "Bearer $token" }

# 2. 用户信息
Test-Endpoint -Name "用户信息" -Method GET -Url "$baseUrl/user/info" -Headers $headers

# 3. 商品列表
$result = Test-Endpoint -Name "商品列表" -Method GET -Url "$baseUrl/product?page=1&size=5" -Headers $headers
$productId = $null
if ($result.success -and $result.data.content.Count -gt 0) {
    $productId = $result.data.content[0].id
    Write-Host "   第一个商品ID: $productId" -ForegroundColor Gray
}

# 4. 商品详情 (如果获取到商品ID)
if ($productId) {
    Test-Endpoint -Name "商品详情" -Method GET -Url "$baseUrl/product/$productId" -Headers $headers
} else {
    $total++; Write-Host "[$total] ⚠️ 商品详情 (跳过，无商品ID)" -ForegroundColor Yellow
}

# 5. 搜索商品
Test-Endpoint -Name "商品搜索" -Method GET -Url "$baseUrl/product/search?keyword=蓝牙" -Headers $headers

# 6. 热销商品
Test-Endpoint -Name "热销商品" -Method GET -Url "$baseUrl/product/hot" -Headers $headers

# 7. 购物车列表
Test-Endpoint -Name "购物车列表" -Method GET -Url "$baseUrl/cart" -Headers $headers

# 8. 添加购物车 (使用查询参数)
if ($productId) {
    $cartAddUrl = "$baseUrl/cart/add?productId=$productId&quantity=1"
    $resp = Invoke-RestMethod -Uri $cartAddUrl -Method Post -Headers $headers -ContentType "application/x-www-form-urlencoded" -ErrorAction SilentlyContinue
    $total++
    if ($resp -and $resp.success) {
        $passed++
        Write-Host "✅ 添加购物车" -ForegroundColor Green
    } else {
        Write-Host "❌ 添加购物车: $($resp.message)" -ForegroundColor Red
    }
    # 获取购物车商品ID用于后续操作
    $cartItems = (Invoke-RestMethod -Uri "$baseUrl/cart" -Method GET -Headers $headers -ErrorAction SilentlyContinue).data.items
    $cartItemId = if ($cartItems.Count -gt 0) { $cartItems[0].id } else { $null }
} else {
    $total++; Write-Host "[$total] ⚠️ 添加购物车 (跳过，无商品ID)" -ForegroundColor Yellow
    $cartItemId = $null
}

# 9. 更新购物车数量 (如果有购物车项)
if ($cartItemId) {
    Test-Endpoint -Name "更新购物车数量" -Method PUT -Url "$baseUrl/cart/update/$cartItemId?quantity=2" -Headers $headers
} else {
    $total++; Write-Host "[$total] ⚠️ 更新购物车数量 (跳过，无购物车项)" -ForegroundColor Yellow
}

# 10. VIP等级列表
Test-Endpoint -Name "VIP等级列表" -Method GET -Url "$baseUrl/user/vip/levels" -Headers $headers

# 11. 抽奖奖品列表
Test-Endpoint -Name "抽奖奖品列表" -Method GET -Url "$baseUrl/lottery/prizes" -Headers $headers

# 12. 消息列表
Test-Endpoint -Name "消息列表" -Method GET -Url "$baseUrl/message/list" -Headers $headers

# 13. 公告列表 (可能不存在，跳过但计入总数)
$total++; Write-Host "[$total] ⚠️ 公告列表 (端点不存在，跳过)" -ForegroundColor Yellow

# 14. 敏感词检测 (如果没有端点，跳过)
$total++; Write-Host "[$total] ⚠️ 敏感词检测 (端点不存在，跳过)" -ForegroundColor Yellow

# 15. 运营报表 (需要ADMIN权限，跳过)
$total++; Write-Host "[$total] ⚠️ 运营报表 (需要ADMIN权限，跳过)" -ForegroundColor Yellow

# 16. 创建订单 (需要购物车有商品，简化)
if ($cartItemId -and $productId) {
    # 先选中
    Invoke-RestMethod -Uri "$baseUrl/cart/select/$cartItemId?selected=true" -Method PUT -Headers $headers -ErrorAction SilentlyContinue
    # 获取选中商品
    $selected = Invoke-RestMethod -Uri "$baseUrl/cart/selected" -Method GET -Headers $headers -ErrorAction SilentlyContinue
    if ($selected -and $selected.data.Count -gt 0) {
        $orderCreateUrl = "$baseUrl/order/create"
        $address = @{ id=1; name="test"; phone="13800138000"; address="test" }
        $item = @{ id=$cartItemId; quantity=1 }
        $body = @{ address=$address; items=@($item); couponId=$null } | ConvertTo-Json
        try {
            $orderResp = Invoke-RestMethod -Uri $orderCreateUrl -Method Post -Headers $headers -Body $body -ContentType "application/json" -ErrorAction Stop
            $total++; $passed++
            Write-Host "✅ 创建订单" -ForegroundColor Green
        } catch {
            $total++
            Write-Host "❌ 创建订单: $($_.Exception.Message)" -ForegroundColor Red
        }
    } else {
        $total++; Write-Host "[$total] ⚠️ 创建订单 (跳过，无选中商品)" -ForegroundColor Yellow
    }
} else {
    $total++; Write-Host "[$total] ⚠️ 创建订单 (跳过，条件不足)" -ForegroundColor Yellow
}

# 总结
Write-Host "`n===== 测试总结 =====" -ForegroundColor Yellow
Write-Host "总测试数: $total"
Write-Host "通过数: $passed"
Write-Host "失败数: $($total - $passed)"
Write-Host "通过率: $([math]::Round($passed/$total*100, 1))%"

# 保存报告
$report = @{
    Total = $total
    Passed = $passed
    Failed = $total - $passed
    Rate = [math]::Round($passed/$total*100, 1)
    Timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
}
$report | ConvertTo-Json | Out-File "D:/market/testing/api-smoke-test-report.json" -Encoding utf8
Write-Host "报告已保存到 D:/market/testing/api-smoke-test-report.json" -ForegroundColor Green