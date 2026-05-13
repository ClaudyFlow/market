<#
.SYNOPSIS
    电子商务平台 API 完整功能测试脚本 (已验证路径)
.DESCRIPTION
    基于实际后端接口路径进行测试，包含：
    - 注册新用户（带时间戳确保唯一）
    - 测试主要 API 端点
    - 验证数据和响应
.NOTES
    后端地址: http://localhost:8080
    请确保后端正在运行
#>

# ==================== 配置 ====================
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
        [string]$Token = $null,
        [hashtable]$QueryParams = $null
    )
    
    $uri = "$BaseUrl$Endpoint"
    if ($QueryParams -and $QueryParams.Count -gt 0) {
        $query = ($QueryParams.GetEnumerator() | ForEach-Object { "$($_.Key)=$($_.Value)" }) -join '&'
        $uri += "?" + $query
    }
    
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
Write-Host "    电子商务平台 API 测试脚本 (已验证)" -ForegroundColor Cyan
Write-Host "    后端地址: $BaseUrl" -ForegroundColor Cyan
Write-Host "    测试时间: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Cyan
Write-Host "============================================`n" -ForegroundColor Cyan

# ==================== 1. 用户注册 ====================
Write-Host "【1/12】用户注册" -ForegroundColor Cyan

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
    Write-TestResult "✅ 用户注册成功" $true "用户: $testUserName, ID: $UserId"
} else {
    Write-TestResult "❌ 用户注册失败" $false $result.Error
    exit 1
}

# ==================== 2. 用户登录 ====================
Write-Host "`n【2/12】用户登录" -ForegroundColor Cyan
Start-Sleep -Milliseconds 500

$loginBody = @{ name = $testUserName; password = $testPassword }
$result = Invoke-ApiRequest -Method POST -Endpoint "/api/auth/login" -Body $loginBody

if ($result.Success -and $result.Data.success) {
    $AuthToken = $result.Data.token
    Write-TestResult "✅ 登录成功" $true "Token长度: $($AuthToken.Length)"
} else {
    Write-TestResult "❌ 登录失败" $false $result.Error
    exit 1
}

# ==================== 3. 获取当前用户信息 ====================
Write-Host "`n【3/12】获取当前用户信息 (GET /api/user/info)" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/user/info" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $userData = $result.Data.data
    Write-TestResult "✅ 获取用户信息成功" $true "用户: $($userData.name), 邮箱: $($userData.email), VIP等级: $($userData.vipLevel)"
} else {
    Write-TestResult "❌ 获取用户信息失败" $false $result.Error
}

# ==================== 4. 商品列表 ====================
Write-Host "`n【4/12】获取商品列表 (GET /api/product)" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/product" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $products = $result.Data.data.content
    $total = $result.Data.data.totalElements
    Write-TestResult "✅ 获取商品列表成功" $true "共 $total 个商品"
    foreach ($p in $products) {
        Write-Host "   ├─ [$($p.id)] $($p.name) - ￥$($p.price)"
    }
    if ($products.Count -gt 0) {
        $TestProductId = $products[0].id
    } else {
        Write-Host "   （商品列表为空，请检查数据库初始化）"
    }
} else {
    Write-TestResult "❌ 获取商品列表失败" $false $result.Error
}

# ==================== 5. 商品详情 ====================
if ($TestProductId) {
    Write-Host "`n【5/12】商品详情 (GET /api/product/$TestProductId)" -ForegroundColor Cyan
    $result = Invoke-ApiRequest -Method GET -Endpoint "/api/product/$TestProductId" -Token $AuthToken
    
    if ($result.Success -and $result.Data.success) {
        $p = $result.Data.data
        Write-TestResult "✅ 获取商品详情成功" $true "$($p.name) - ￥$($p.price)"
    } else {
        Write-TestResult "❌ 获取商品详情失败" $false $result.Error
    }
}

# ==================== 6. 商品搜索 ====================
Write-Host "`n【6/12】商品搜索 (GET /api/product/search?keyword=蓝牙)" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/product/search" -Token $AuthToken -QueryParams @{ keyword = "蓝牙" }

if ($result.Success -and $result.Data.success) {
    $count = $result.Data.data.totalElements
    Write-TestResult "✅ 商品搜索成功" $true "找到 $count 个结果"
} else {
    Write-TestResult "❌ 商品搜索失败" $false $result.Error
}

# ==================== 7. 获取购物车 ====================
Write-Host "`n【7/12】获取购物车 (GET /api/cart)" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/cart" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $items = $result.Data.data
    $count = $items.Count
    Write-TestResult "✅ 获取购物车成功" $true "商品数: $count"
} else {
    Write-TestResult "❌ 获取购物车失败" $false $result.Error
}

# ==================== 8. 添加商品到购物车 ====================
if ($TestProductId) {
    Write-Host "`n【8/12】添加购物车 (POST /api/cart/add?productId=$TestProductId&quantity=2)" -ForegroundColor Cyan
    $result = Invoke-ApiRequest -Method POST -Endpoint "/api/cart/add" -Token $AuthToken -QueryParams @{
        productId = $TestProductId
        quantity = 2
    }
    
    if ($result.Success -and $result.Data.success) {
        Write-TestResult "✅ 添加购物车成功" $true "商品ID: $TestProductId"
    } else {
        Write-TestResult "❌ 添加购物车失败 (可能因序列化问题返回500)" $false $result.Error
    }
}

# ==================== 9. VIP 等级列表 ====================
Write-Host "`n【9/12】VIP 等级列表 (GET /api/user/vip/levels)" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/user/vip/levels" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $vipLevels = $result.Data.data
    Write-TestResult "✅ 获取 VIP 等级成功" $true "共 $($vipLevels.Count) 个等级"
    foreach ($vip in $vipLevels) {
        Write-Host "   ├─ 等级$($vip.level): $($vip.name) 折扣: $($vip.discount)"
    }
    if ($vipLevels.Count -eq 0) {
        Write-Host "   （VIP等级表未初始化数据）"
    }
} else {
    Write-TestResult "❌ 获取 VIP 等级失败" $false $result.Error
}

# ==================== 10. 抽奖奖品列表 ====================
Write-Host "`n【10/12】抽奖奖品 (GET /api/lottery/prizes)" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/lottery/prizes" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $prizes = $result.Data.data
    Write-TestResult "✅ 获取抽奖奖品成功" $true "共 $($prizes.Count) 个奖品"
    foreach ($prize in $prizes | Select-Object -First 3) {
        Write-Host "   ├─ $($prize.name) 类型: $($prize.type)"
    }
    if ($prizes.Count -gt 3) {
        Write-Host "   └─ ... 还有 $($prizes.Count - 3) 个"
    }
} else {
    Write-TestResult "❌ 获取抽奖奖品失败" $false $result.Error
}

# ==================== 11. 创建订单 ====================
if ($TestProductId) {
    Write-Host "`n【11/12】创建订单 (POST /api/order)" -ForegroundColor Cyan
    $orderBody = @{
        items = @(
            @{ productId = $TestProductId; quantity = 1 }
        )
        shippingAddress = "北京市海淀区测试地址"
        remark = "API自动化测试订单"
    }
    $result = Invoke-ApiRequest -Method POST -Endpoint "/api/order" -Body $orderBody -Token $AuthToken
    
    if ($result.Success -and $result.Data.success) {
        $order = $result.Data.data
        Write-TestResult "✅ 订单创建成功" $true "订单号: $($order.orderNo) 金额: ￥$($order.totalAmount)"
    } else {
        Write-TestResult "❌ 订单创建失败 (可能因序列化问题)" $false $result.Error
    }
}

# ==================== 12. 订单列表 ====================
Write-Host "`n【12/12】订单列表 (GET /api/order)" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/order" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    $orderList = $result.Data.data.list
    Write-TestResult "✅ 获取订单列表成功" $true "共 $($result.Data.data.total) 个订单"
} else {
    Write-TestResult "❌ 获取订单列表失败" $false $result.Error
}

# ==================== 13. 消息列表 ====================
Write-Host "`n【13/13】消息列表 (GET /api/message/list)" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/message/list" -Token $AuthToken

if ($result.Success -and $result.Data.success) {
    Write-TestResult "✅ 获取消息列表成功" $true "共 $($result.Data.data.total) 条消息"
} else {
    Write-TestResult "❌ 获取消息列表失败" $false $result.Error
}

# ==================== 结束 ====================
Write-Host "`n============================================" -ForegroundColor Green
Write-Host "    测试完成!" -ForegroundColor Green
Write-Host "============================================`n" -ForegroundColor Green

Write-Host "测试摘要:" -ForegroundColor White
Write-Host "  测试用户: $testUserName"
Write-Host "  User ID : $UserId"
Write-Host "  Token   : $($AuthToken.Substring(0,20))... (长度 $($AuthToken.Length))"
if ($TestProductId) {
    Write-Host "  测试商品: ID=$TestProductId"
}
Write-Host ""

Write-Host "注意事项:" -ForegroundColor Yellow
Write-Host "  - 商品列表为空：请检查 DatabaseInitConfig 中的产品插入语句（status 字段为 NULL）"
Write-Host "  - VIP 等级为空：请在数据库中初始化 vip_level 表数据"
Write-Host "  - 添加购物车/商品详情可能因序列化问题返回500，需要调整实体 JSON 注解"
Write-Host "  - 目前能正常工作的端点：登录、用户信息、商品搜索、抽奖奖品、消息列表"
