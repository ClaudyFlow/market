<#
.SYNOPSIS
    电子商务平台 API 完整功能测试脚本
.DESCRIPTION
    测试主要 API 端点，包括：
    - 用户认证（注册/登录）
    - 商品浏览
    - 购物车操作
    - VIP等级
    - 抽奖
    - 订单创建
    - 公告和敏感词
.NOTES
    后端地址: http://localhost:8080
    如果 admin 用户登录失败，将自动注册一个测试用户
#>

$BaseUrl = "http://localhost:8080"
$AuthToken = $null
$UserId = $null

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
    Write-Host "$status $TestName" -ForegroundColor $(if ($Passed) { 'Green' } else { 'Red' })
    if ($Details) {
        Write-Host "   $Details"
    }
}

# ==================== 1. 用户认证 ====================
Write-Host "`n========== 1. 管理员登录尝试 ==========" -ForegroundColor Cyan

# 尝试 admin 登录
$loginBody = @{ name = "admin"; password = "admin123" }
$result = Invoke-ApiRequest -Method POST -Endpoint "/api/auth/login" -Body $loginBody

if ($result.Success -and $result.Data.success) {
    $AuthToken = $result.Data.token
    $UserId = $result.Data.data.id
    Write-TestResult "管理员登录" $true "Token: $($AuthToken.Substring(0,20))... UserId: $UserId"
} else {
    Write-TestResult "管理员登录" $false "密码错误，将使用测试用户"
    
    # 注册测试用户
    Write-Host "`n========== 1.b 注册测试用户 ==========" -ForegroundColor Cyan
    $registerBody = @{
        name = "testuser"
        email = "testuser@example.com"
        password = "Test123456"
        confirmPassword = "Test123456"
    }
    $result = Invoke-ApiRequest -Method POST -Endpoint "/api/auth/register" -Body $registerBody
    
    if ($result.Success -and $result.Data.success) {
        $AuthToken = $result.Data.token
        $UserId = $result.Data.data.id
        Write-TestResult "用户注册" $true "User: $($result.Data.data.name) Id: $UserId"
    } else {
        Write-TestResult "用户注册" $false $result.Error
        exit 1
    }
    
    # 登录测试用户
    Write-Host "`n========== 1.c 测试用户登录 ==========" -ForegroundColor Cyan
    $loginBody = @{ name = "testuser"; password = "Test123456" }
    $result = Invoke-ApiRequest -Method POST -Endpoint "/api/auth/login" -Body $loginBody
    if ($result.Success -and $result.Data.success) {
        $AuthToken = $result.Data.token
        Write-TestResult "用户登录" $true
    } else {
        Write-TestResult "用户登录" $false $result.Error
        exit 1
    }
}

# ==================== 2. 获取用户信息 ====================
Write-Host "`n========== 2. 获取当前用户信息 ==========" -ForegroundColor Cyan
Write-Host "DEBUG: Token used = '$($AuthToken.Substring(0, [Math]::Min(30, $AuthToken.Length)))...'"
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/user/profile" -Token $AuthToken
if ($result.Success -and $result.Data.success) {
    Write-TestResult "获取用户信息" $true "用户: $($result.Data.data.name), 邮箱: $($result.Data.data.email)"
} else {
    Write-TestResult "获取用户信息" $false $result.Error
}

# ==================== 3. 商品相关 ====================
Write-Host "`n========== 3. 商品列表 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/product/list" -Token $AuthToken
if ($result.Success -and $result.Data.success) {
    $count = $result.Data.data.total
    Write-TestResult "获取商品列表" $true "共 $count 个商品"
    foreach ($product in $result.Data.data.list) {
        Write-Host "   - $($product.name) ￥$($product.price) 库存: $($product.stock)"
    }
} else {
    Write-TestResult "获取商品列表" $false $result.Error
}

Write-Host "`n========== 3.b 商品详情 ==========" -ForegroundColor Cyan
# 获取第一个商品ID
$firstProductId = if ($result.Success -and $result.Data.data.list.Count -gt 0) { $result.Data.data.list[0].id } else { 1000 }
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/product/detail/$firstProductId" -Token $AuthToken
if ($result.Success -and $result.Data.success) {
    Write-TestResult "获取商品详情" $true "商品: $($result.Data.data.name)"
} else {
    Write-TestResult "获取商品详情" $false $result.Error
}

# ==================== 4. 购物车 ====================
Write-Host "`n========== 4. 获取购物车 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/cart" -Token $AuthToken
if ($result.Success -and $result.Data.success) {
    $cartCount = $result.Data.data.totalCount
    Write-TestResult "获取购物车" $true "商品种类数: $cartCount"
} else {
    Write-TestResult "获取购物车" $false $result.Error
}

Write-Host "`n========== 4.b 添加商品到购物车 ==========" -ForegroundColor Cyan
if ($firstProductId) {
    $addCartBody = @{ productId = $firstProductId; quantity = 2 }
    $result = Invoke-ApiRequest -Method POST -Endpoint "/api/cart/add" -Body $addCartBody -Token $AuthToken
    if ($result.Success -and $result.Data.success) {
        Write-TestResult "添加购物车" $true "数量: 2"
    } else {
        Write-TestResult "添加购物车" $false $result.Error
    }
}

# ==================== 5. VIP 等级 ====================
Write-Host "`n========== 5. VIP 等级列表 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/vip/levels" -Token $AuthToken
if ($result.Success -and $result.Data.success) {
    $count = $result.Data.data.Count
    Write-TestResult "获取 VIP 等级" $true "共 $count 个等级"
    foreach ($vip in $result.Data.data) {
        Write-Host "   - 等级$($vip.level): $($vip.name) 折扣: $($vip.discount)"
    }
} else {
    Write-TestResult "获取 VIP 等级" $false $result.Error
}

# ==================== 6. 抽奖奖品 ====================
Write-Host "`n========== 6. 抽奖奖品列表 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/lottery/prizes" -Token $AuthToken
if ($result.Success -and $result.Data.success) {
    $prizes = $result.Data.data
    Write-TestResult "获取抽奖奖品" $true "共 $($prizes.Count) 个奖品"
    foreach ($prize in $prizes) {
        Write-Host "   - $($prize.name) 概率: $($prize.probability)"
    }
} else {
    Write-TestResult "获取抽奖奖品" $false $result.Error
}

# ==================== 7. 创建订单 ====================
Write-Host "`n========== 7. 创建订单 ==========" -ForegroundColor Cyan
if ($firstProductId) {
    $orderBody = @{
        items = @(
            @{ productId = $firstProductId; quantity = 1 }
        )
        shippingAddress = "测试地址"
        remark = "API测试订单"
    }
    $result = Invoke-ApiRequest -Method POST -Endpoint "/api/order/create" -Body $orderBody -Token $AuthToken
    if ($result.Success -and $result.Data.success) {
        $orderId = $result.Data.data.id
        Write-TestResult "创建订单" $true "订单号: $orderId 金额: ￥$($result.Data.data.totalAmount)"
    } else {
        Write-TestResult "创建订单" $false $result.Error
    }
}

# ==================== 8. 订单列表 ====================
Write-Host "`n========== 8. 订单列表 ==========" -ForegroundColor Cyan
Start-Sleep -Seconds 2  # 等待订单处理
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/order/list" -Token $AuthToken
if ($result.Success -and $result.Data.success) {
    $orderCount = $result.Data.data.totalElements
    Write-TestResult "获取订单列表" $true "共 $orderCount 个订单"
} else {
    Write-TestResult "获取订单列表" $false $result.Error
}

# ==================== 9. 敏感词过滤 ====================
Write-Host "`n========== 9. 敏感词过滤测试 ==========" -ForegroundColor Cyan
$filterBody = @{ content = "这是一条包含测试内容的消息" }
$result = Invoke-ApiRequest -Method POST -Endpoint "/api/sensitive/filter" -Body $filterBody -Token $AuthToken
if ($result.Success -and $result.Data.success) {
    Write-TestResult "敏感词过滤" $true "结果: $($result.Data.data.isValid)"
} else {
    Write-TestResult "敏感词过滤" $false $result.Error
}

# ==================== 10. 公告列表 ====================
Write-Host "`n========== 10. 公告列表 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/announcement/list" -Token $AuthToken
if ($result.Success -and $result.Data.success) {
    $count = $result.Data.data.Count
    Write-TestResult "获取公告列表" $true "共 $count 条公告"
    foreach ($announcement in $result.Data.data) {
        Write-Host "   - [$($announcement.type)] $($announcement.title)"
    }
} else {
    Write-TestResult "获取公告列表" $false $result.Error
}

# ==================== 11. 测试商品搜索 ====================
Write-Host "`n========== 11. 商品搜索 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/product/search?keyword=蓝牙" -Token $AuthToken
if ($result.Success -and $result.Data.success) {
    $count = $result.Data.data.total
    Write-TestResult "商品搜索" $true "搜索 '蓝牙' 找到 $count 个结果"
} else {
    Write-TestResult "商品搜索" $false $result.Error
}

Write-Host "`n========== 测试完成 ==========`n" -ForegroundColor Green
