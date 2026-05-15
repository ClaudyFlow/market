<#
.SYNOPSIS
    电子商务平台 API 完整功能测试脚本 (改进版)
.DESCRIPTION
    测试主要 API 端点，包含完整流程：
    - 注册新用户（带时间戳确保唯一）
    - 使用 token 访问受保护资源
    - 验证数据完整性
.NOTES
    后端地址: http://localhost:8080
#>

$BaseUrl = "http://localhost:8080"
$AuthToken = $null
$UserId = $null
$TestProductId = $null

# ==================== 辅助函数 ====================
function Invoke-ApiRequest {
    param(
        [string]$Method,
        [string]$Endpoint,
        [object]$Body = $null,
        [string]$Token = $null
    )
    
    $uri = "$BaseUrl$Endpoint"
    $headers = @{ "Content-Type" = "application/json" }
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }
    
    try {
        if ($Body) {
            $jsonBody = $Body | ConvertTo-Json -Depth 10
            $response = Invoke-RestMethod -Uri $uri -Method $Method -Headers $headers -Body $jsonBody -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Uri $uri -Method $Method -Headers $headers -ErrorAction Stop
        }
        return @{ Success = $true; Data = $response }
    } catch {
        $errorResponse = $_.Exception.Response
        if ($errorResponse) {
            $stream = $errorResponse.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream)
            $reader.BaseStream.Position = 0
            $reader.DiscardBufferedData()
            $responseBody = $reader.ReadToEnd()
            return @{ Success = $false; Error = $responseBody }
        } else {
            return @{ Success = $false; Error = $_.Exception.Message }
        }
    }
}

function Write-TestResult {
    param(
        [string]$TestName,
        [bool]$Passed,
        [string]$Details = ""
    )
    $status = if ($Passed) { "✅" } else { "❌" }
    $color = if ($Passed) { 'Green' } else { 'Red' }
    Write-Host "$status $TestName" -ForegroundColor $color -NoNewline
    if ($Details) {
        Write-Host " - $Details" -ForegroundColor Gray
    } else {
        Write-Host ""
    }
}

# ==================== 开始测试 ====================
Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "    API 自动化测试脚本" -ForegroundColor Cyan
Write-Host "    后端地址: $BaseUrl" -ForegroundColor Cyan
Write-Host "    测试时间: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Cyan
Write-Host "============================================`n" -ForegroundColor Cyan

# ==================== 1. 用户注册 ====================
Write-Host "【步骤 1/11】注册测试用户" -ForegroundColor Cyan

$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$testUserName = "testuser_$timestamp"
$testEmail = "test${timestamp}@example.com"
$testPassword = "Test123456"

$registerBody = @{
    name = $testUserName
    email = $testEmail
    password = $testPassword
    confirmPassword = $testPassword
}

$result = Invoke-ApiRequest -Method POST -Endpoint "/api/auth/register" -Body $registerBody

if ($result.Success -and $result.Data.success) {
    $AuthToken = $result.Data.token
    $UserId = $result.Data.data.id
    Write-TestResult "✅ 用户注册成功" $true "用户: $testUserName, ID: $UserId, Token长度: $($AuthToken.Length)"
} else {
    Write-TestResult "❌ 用户注册失败" $false $result.Error
    exit 1
}

# ==================== 2. 用户登录 ====================
Write-Host "`n【步骤 2/11】用户登录" -ForegroundColor Cyan
Start-Sleep -Milliseconds 500

$loginBody = @{ name = $testUserName; password = $testPassword }
$result = Invoke-ApiRequest -Method POST -Endpoint "/api/auth/login" -Body $loginBody

if ($result.Success -and $result.Data.success) {
    $AuthToken = $result.Data.token  # 更新 token（可能不同）
    Write-TestResult "✅ 登录成功" $true "Token长度: $($AuthToken.Length)"
} else {
    Write-TestResult "❌ 登录失败" $false $result.Error
    exit 1
}

# ==================== 3. 获取用户信息 ====================
Write-Host "`n【步骤 3/11】获取当前用户信息" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/user/profile" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $userData = $result.Data.data
    Write-TestResult "✅ 获取用户信息" $true "用户: $($userData.name), 邮箱: $($userData.email), ID: $($userData.id)"
} else {
    Write-TestResult "❌ 获取用户信息失败" $false $result.Error
}

# ==================== 4. 商品列表 ====================
Write-Host "`n【步骤 4/11】获取商品列表" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/product/list" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $products = $result.Data.data.list
    $total = $result.Data.data.total
    Write-TestResult "✅ 获取商品列表" $true "共 $total 个商品"
    foreach ($p in $products) {
        Write-Host "   ├─ [$($p.id)] $($p.name) - ￥$($p.price) (库存: $($p.stock))"
    }
    if ($products.Count -gt 0) {
        $TestProductId = $products[0].id
    }
} else {
    Write-TestResult "❌ 获取商品列表失败" $false $result.Error
}

# ==================== 5. 商品详情 ====================
if ($TestProductId) {
    Write-Host "`n【步骤 5/11】获取商品详情 (ID: $TestProductId)" -ForegroundColor Cyan
    $result = Invoke-ApiRequest -Method GET -Endpoint "/api/product/detail/$TestProductId" -Token $AuthToken
    
    if ($result.Success -and $result.Data.success) {
        $p = $result.Data.data
        Write-TestResult "✅ 获取商品详情" $true "$($p.name) - $($p.description.Substring(0, [Math]::Min(30, $p.description.Length)))..."
    } else {
        Write-TestResult "❌ 获取商品详情失败" $false $result.Error
    }
}

# ==================== 6. 商品搜索 ====================
Write-Host "`n【步骤 6/11】商品搜索" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/product/search?keyword=蓝牙" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $count = $result.Data.data.total
    Write-TestResult "✅ 商品搜索" $true "关键字 '蓝牙' 找到 $count 个结果"
} else {
    Write-TestResult "❌ 商品搜索失败" $false $result.Error
}

# ==================== 7. 获取购物车 ====================
Write-Host "`n【步骤 7/11】获取购物车" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/cart" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $cart = $result.Data.data
    Write-TestResult "✅ 获取购物车" $true "商品总数: $($cart.totalCount), 件数: $($cart.totalQuantity)"
} else {
    Write-TestResult "❌ 获取购物车失败" $false $result.Error
}

# ==================== 8. 添加商品到购物车 ====================
if ($TestProductId) {
    Write-Host "`n【步骤 8/11】添加商品到购物车" -ForegroundColor Cyan
    $addCartBody = @{ productId = $TestProductId; quantity = 2 }
    $result = Invoke-ApiRequest -Method POST -Endpoint "/api/cart/add" -Body $addCartBody -Token $AuthToken
    
    if ($result.Success -and $result.Data.success) {
        Write-TestResult "✅ 添加购物车成功" $true "商品ID: $TestProductId, 数量: 2"
    } else {
        Write-TestResult "❌ 添加购物车失败" $false $result.Error
    }
}

# ==================== 9. VIP 等级列表 ====================
Write-Host "`n【步骤 9/11】VIP 等级列表" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/vip/levels" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $vipLevels = $result.Data.data
    Write-TestResult "✅ 获取 VIP 等级" $true "共 $($vipLevels.Count) 个等级"
    foreach ($vip in $vipLevels) {
        Write-Host "   ├─ 等级$($vip.level): $($vip.name) - 折扣: $($vip.discount) - 积分系数: $($vip.creditMultiplier)"
    }
} else {
    Write-TestResult "❌ 获取 VIP 等级失败" $false $result.Error
}

# ==================== 10. 抽奖奖品列表 ====================
Write-Host "`n【步骤 10/11】抽奖奖品列表" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/lottery/prizes" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $prizes = $result.Data.data
    Write-TestResult "✅ 获取抽奖奖品" $true "共 $($prizes.Count) 个奖品"
    foreach ($prize in $prizes | Select-Object -First 3) {
        Write-Host "   ├─ $($prize.name) - 概率: $($prize.probability) - 库存: $($prize.stock)/$($prize.totalStock)"
    }
    if ($prizes.Count -gt 3) {
        Write-Host "   └─ ... 等 $($prizes.Count - 3) 个更多"
    }
} else {
    Write-TestResult "❌ 获取抽奖奖品失败" $false $result.Error
}

# ==================== 11. 公告列表 ====================
Write-Host "`n【步骤 11/11】公告列表" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/announcement/list" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $announcements = $result.Data.data
    Write-TestResult "✅ 获取公告列表" $true "共 $($announcements.Count) 条公告"
    foreach ($ann in $announcements | Select-Object -First 3) {
        Write-Host "   ├─ [$($ann.type)] $($ann.title) ($($ann.createdAt.Substring(0,10)))"
    }
    if ($announcements.Count -gt 3) {
        Write-Host "   └─ ... 等 $($announcements.Count - 3) 个更多"
    }
} else {
    Write-TestResult "❌ 获取公告列表失败" $false $result.Error
}

# ==================== 12. 创建订单 ====================
if ($TestProductId) {
    Write-Host "`n【步骤 12/12】创建订单" -ForegroundColor Cyan
    $orderBody = @{
        items = @(
            @{ productId = $TestProductId; quantity = 1 }
        )
        shippingAddress = "测试地址 - 北京市海淀区"
        remark = "API自动化测试订单"
    }
    $result = Invoke-ApiRequest -Method POST -Endpoint "/api/order/create" -Body $orderBody -Token $AuthToken
    
    if ($result.Success -and $result.Data.success) {
        $order = $result.Data.data
        Write-TestResult "✅ 订单创建成功" $true "订单号: $($order.orderNo), 金额: ￥$($order.totalAmount)"
    } else {
        Write-TestResult "❌ 订单创建失败" $false $result.Error
    }
}

# ==================== 结束 ====================
Write-Host "`n============================================" -ForegroundColor Green
Write-Host "    测试完成!" -ForegroundColor Green
Write-Host "============================================`n" -ForegroundColor Green

Write-Host "测试摘要:" -ForegroundColor White
Write-Host "  使用账户: $testUserName"
Write-Host "  User ID  : $UserId"
Write-Host "  测试商品: $TestProductId"
Write-Host "  Token    : $($AuthToken.Substring(0,20))... (长度 $($AuthToken.Length))"
Write-Host ""
