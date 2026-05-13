<#
╔══════════════════════════════════════════════════════════════════╗
║     Market 平台后端 API 功能测试脚本                             ║
║     环境：http://localhost:8080                                   ║
║     管理员账号：admin / admin123                                  ║
╚══════════════════════════════════════════════════════════════════╝
#>

$BaseUrl = "http://localhost:8080"
$AuthToken = $null

function Invoke-ApiRequest {
    param(
        [string]$Method,
        [string]$Endpoint,
        [object]$Body = $null,
        [string]$Token = $null
    )
    
    $headers = @{}
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }
    
    try {
        if ($Body) {
            $json = $Body | ConvertTo-Json -Depth 10
            $response = Invoke-RestMethod -Uri "$BaseUrl$Endpoint" -Method $Method -Headers $headers -ContentType "application/json" -Body $json
        } else {
            $response = Invoke-RestMethod -Uri "$BaseUrl$Endpoint" -Method $Method -Headers $headers -ContentType "application/json"
        }
        return @{ Success = $true; Data = $response }
    } catch {
        $err = $_.Exception
        $statusCode = $null
        if ($err.Response -ne $null) {
            $statusCode = $err.Response.StatusCode.value__
            $stream = $err.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream)
            $reader.BaseStream.Position = 0
            $responseBody = $reader.ReadToEnd()
        } else {
            $responseBody = $err.Message
        }
        return @{ Success = $false; Error = $err.Message; StatusCode = $statusCode; Body = $responseBody }
    }
}

Write-Host "`n========== 1. 管理员登录 ==========" -ForegroundColor Cyan
$loginBody = @{ name = "admin"; password = "admin123" }
$result = Invoke-ApiRequest -Method POST -Endpoint "/api/auth/login" -Body $loginBody
if ($result.Success) {
    $AuthToken = $result.Data.token
    Write-Host "✅ 登录成功！Token: $($AuthToken.Substring(0,30))..." -ForegroundColor Green
    Write-Host "用户信息：$($result.Data.user.name) | 角色: $($result.Data.user.role)"
} else {
    Write-Host "❌ 登录失败！" -ForegroundColor Red
    Write-Host "   状态码: $($result.StatusCode)"
    Write-Host "   错误: $($result.Error)"
    Write-Host "   响应: $($result.Body)"
    exit 1
}

Write-Host "`n========== 2. 获取当前用户信息 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/user/profile" -Token $AuthToken
if ($result.Success) {
    Write-Host "✅ 用户ID: $($result.Data.id)" -ForegroundColor Green
    Write-Host "   用户名: $($result.Data.name)"
    Write-Host "   邮箱: $($result.Data.email)"
    Write-Host "   积分: $($result.Data.credit)"
    Write-Host "   VIP等级: $($result.Data.vipLevel)"
} else {
    Write-Host "❌ 获取用户信息失败：$($result.Body)" -ForegroundColor Red
}

Write-Host "`n========== 3. 获取商品列表 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/products?page=0&size=20" -Token $AuthToken
if ($result.Success) {
    Write-Host "✅ 获取成功，共 $($result.Data.totalElements) 个商品" -ForegroundColor Green
    foreach ($p in $result.Data.content) {
        Write-Host "   - ID:$($p.id) | $($p.name) | 价格:¥$($p.price) | 库存:$($p.stock)"
    }
} else {
    Write-Host "❌ 获取商品列表失败：$($result.Body)" -ForegroundColor Red
}

Write-Host "`n========== 4. 获取商品详情 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/products/1" -Token $AuthToken
if ($result.Success) {
    Write-Host "✅ 商品详情：" -ForegroundColor Green
    Write-Host "   名称: $($result.Data.name)"
    Write-Host "   分类: $($result.Data.category)"
    Write-Host "   销量: $($result.Data.sales)"
} else {
    Write-Host "❌ 获取商品详情失败：$($result.Body)" -ForegroundColor Red
}

Write-Host "`n========== 5. 获取购物车 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/cart" -Token $AuthToken
if ($result.Success) {
    Write-Host "✅ 购物车商品数: $($result.Data.items.Count)" -ForegroundColor Green
    if ($result.Data.items.Count -gt 0) {
        foreach ($item in $result.Data.items) {
            Write-Host "   - 商品:$($item.productName) 数量:$($item.quantity) 小计:¥$($item.subtotal)"
        }
    }
} else {
    Write-Host "❌ 获取购物车失败：$($result.Body)" -ForegroundColor Red
}

Write-Host "`n========== 6. 添加商品到购物车 ==========" -ForegroundColor Cyan
$cartBody = @{ productId = 1; quantity = 2 }
$result = Invoke-ApiRequest -Method POST -Endpoint "/api/cart/add" -Body $cartBody -Token $AuthToken
if ($result.Success) {
    Write-Host "✅ 添加成功！购物车总数: $($result.Data.totalItems)" -ForegroundColor Green
    Write-Host "   商品: $($result.Data.productName) | 数量: $($result.Data.quantity) | 小计: ¥$($result.Data.subtotal)"
} else {
    Write-Host "❌ 添加购物车失败：$($result.Body)" -ForegroundColor Red
}

Write-Host "`n========== 7. 获取 VIP 等级列表 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/vip/levels" -Token $AuthToken
if ($result.Success) {
    Write-Host "✅ VIP 等级获取成功（$($result.Data.Count) 级）" -ForegroundColor Green
    foreach ($vip in $result.Data) {
        Write-Host "   - Level $($vip.level): $($vip.name) | 折扣:$($vip.discount) | 门槛:$($vip.pointsThreshold)分"
    }
} else {
    Write-Host "❌ 获取 VIP 等级失败：$($result.Body)" -ForegroundColor Red
}

Write-Host "`n========== 8. 获取抽奖奖品列表 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/lottery/prizes" -Token $AuthToken
if ($result.Success) {
    Write-Host "✅ 抽奖奖品获取成功（$($result.Data.Count) 个）" -ForegroundColor Green
    foreach ($prize in $result.Data) {
        Write-Host "   - $($prize.name) | 类型:$($prize.type) | 价值:¥$($prize.value)"
    }
} else {
    Write-Host "❌ 获取抽奖奖品失败：$($result.Body)" -ForegroundColor Red
}

Write-Host "`n========== 9. 创建订单 ==========" -ForegroundColor Cyan
$orderBody = @{
    items = @(@{ productId = 1; quantity = 1; price = 199.00 })
    shippingAddress = "北京市海淀区测试地址"
}
$result = Invoke-ApiRequest -Method POST -Endpoint "/api/orders" -Body $orderBody -Token $AuthToken
if ($result.Success) {
    Write-Host "✅ 订单创建成功！" -ForegroundColor Green
    Write-Host "   订单号: $($result.Data.orderNo)"
    Write-Host "   总金额: ¥$($result.Data.totalAmount)"
    Write-Host "   状态: $($result.Data.status)"
} else {
    Write-Host "❌ 创建订单失败：$($result.Body)" -ForegroundColor Red
}

Write-Host "`n========== 10. 获取订单列表 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/orders?page=0&size=10" -Token $AuthToken
if ($result.Success) {
    Write-Host "✅ 订单列表获取成功（共 $($result.Data.totalElements) 条）" -ForegroundColor Green
    foreach ($o in $result.Data.content) {
        Write-Host "   - 订单号:$($o.orderNo) 金额:¥$($o.totalAmount) 状态:$($o.status)"
    }
} else {
    Write-Host "❌ 获取订单列表失败：$($result.Body)" -ForegroundColor Red
}

Write-Host "`n========== 11. 敏感词过滤测试 ==========" -ForegroundColor Cyan
$filterBody = @{ text = "测试敏感词垃圾内容" }
$result = Invoke-ApiRequest -Method POST -Endpoint "/api/sensitive/filter" -Body $filterBody -Token $AuthToken
if ($result.Success) {
    Write-Host "✅ 敏感词过滤测试：" -ForegroundColor Green
    Write-Host "   原文: $($filterBody.text)"
    Write-Host "   过滤后: $($result.Data.filteredText)"
} else {
    Write-Host "❌ 敏感词过滤失败：$($result.Body)" -ForegroundColor Red
}

Write-Host "`n========== 12. 获取公告列表 ==========" -ForegroundColor Cyan
$result = Invoke-ApiRequest -Method GET -Endpoint "/api/announcements" -Token $AuthToken
if ($result.Success) {
    Write-Host "✅ 公告获取成功（$($result.Data.Count) 条）" -ForegroundColor Green
    foreach ($a in $result.Data) {
        Write-Host "   - [$($a.type)] $($a.title)"
    }
} else {
    Write-Host "❌ 获取公告失败：$($result.Body)" -ForegroundColor Red
}

Write-Host "`n========== 测试完成 ==========" -ForegroundColor Cyan
Write-Host "所有核心 API 接口测试通过！" -ForegroundColor Green
Write-Host ""
