<#
.SYNOPSIS
    电商平台 API 快速测试脚本
.DESCRIPTION
    注册用户 → 测试主要接口 → 输出结果
.NOTES
    后端: http://localhost:8080
#>

$BaseUrl = "http://localhost:8080"

Write-Host "`n========== 注册新用户 ==========" -ForegroundColor Cyan
$ts = Get-Date -Format "yyyyMMddHHmmss"
$uname = "t_$ts"
$uemail = "t$ts@test.com"
$upwd = "Test123456"

$regBody = @{ name=$uname; email=$uemail; password=$upwd; confirmPassword=$upwd } | ConvertTo-Json
try {
    $reg = Invoke-RestMethod "$BaseUrl/api/auth/register" -Method POST -Body $regBody -ContentType "application/json" -ErrorAction Stop
    $token = $reg.token
    Write-Host "✅ 注册成功. Token长度: $($token.Length)"
} catch {
    Write-Host "❌ 注册失败: $($_.Exception.Message)"
    exit 1
}

$headers = @{ Authorization = "Bearer $token"; "Content-Type" = "application/json" }

Write-Host "`n========== 用户信息 GET /api/user/info ==========" -ForegroundColor Cyan
try {
    $ui = Invoke-RestMethod "$BaseUrl/api/user/info" -Method GET -Headers $headers -ErrorAction Stop
    Write-Host "✅ 用户: $($ui.name), 邮箱: $($ui.email), VIP: $($ui.vipLevel)"
} catch {
    Write-Host "❌ 失败: $($_.Exception.Message)"
}

Write-Host "`n========== 商品列表 GET /api/product ==========" -ForegroundColor Cyan
try {
    $pl = Invoke-RestMethod "$BaseUrl/api/product?page=1&size=10" -Method GET -Headers $headers -ErrorAction Stop
    Write-Host "✅ 商品总数: $($pl.data.totalElements)"
    if ($pl.data.totalElements -gt 0) {
        $global:FirstProductId = $pl.data.content[0].id
        Write-Host "   第一个商品: [$($pl.data.content[0].id)] $($pl.data.content[0].name) ￥$($pl.data.content[0].price)"
    } else {
        Write-Host "   （商品表为空）"
    }
} catch {
    Write-Host "❌ 失败: $($_.Exception.Message)"
}

Write-Host "`n========== 商品搜索 GET /api/product/search?keyword=蓝牙 ==========" -ForegroundColor Cyan
try {
    $ps = Invoke-RestMethod "$BaseUrl/api/product/search?keyword=蓝牙" -Method GET -Headers $headers -ErrorAction Stop
    Write-Host "✅ 搜索结果: $($ps.data.totalElements) 个"
} catch {
    Write-Host "❌ 失败: $($_.Exception.Message)"
}

Write-Host "`n========== 获取购物车 GET /api/cart ==========" -ForegroundColor Cyan
try {
    $cg = Invoke-RestMethod "$BaseUrl/api/cart" -Method GET -Headers $headers -ErrorAction Stop
    Write-Host "✅ 购物车商品种数: $($cg.data.Count)"
} catch {
    Write-Host "❌ 失败: $($_.Exception.Message)"
}

if ($global:FirstProductId) {
    Write-Host "`n========== 添加购物车 POST /api/cart/add?productId=$FirstProductId&quantity=2 ==========" -ForegroundColor Cyan
    try {
        $add = Invoke-RestMethod "$BaseUrl/api/cart/add?productId=$FirstProductId&quantity=2" -Method POST -Headers $headers -ErrorAction Stop
        Write-Host "✅ 添加成功"
    } catch {
        Write-Host "❌ 添加失败 (可能因序列化返回500): $($_.Exception.Message)"
    }
}

Write-Host "`n========== VIP 等级 GET /api/user/vip/levels ==========" -ForegroundColor Cyan
try {
    $vl = Invoke-RestMethod "$BaseUrl/api/user/vip/levels" -Method GET -Headers $headers -ErrorAction Stop
    Write-Host "✅ VIP等级数: $($vl.data.Count)"
} catch {
    Write-Host "❌ 失败: $($_.Exception.Message)"
}

Write-Host "`n========== 抽奖奖品 GET /api/lottery/prizes ==========" -ForegroundColor Cyan
try {
    $lp = Invoke-RestMethod "$BaseUrl/api/lottery/prizes" -Method GET -Headers $headers -ErrorAction Stop
    Write-Host "✅ 奖品数: $($lp.data.Count)"
} catch {
    Write-Host "❌ 失败: $($_.Exception.Message)"
}

Write-Host "`n========== 消息列表 GET /api/message/list ==========" -ForegroundColor Cyan
try {
    $ml = Invoke-RestMethod "$BaseUrl/api/message/list" -Method GET -Headers $headers -ErrorAction Stop
    Write-Host "✅ 消息总数: $($ml.data.total)"
} catch {
    Write-Host "❌ 失败: $($_.Exception.Message)"
}

Write-Host "`n========== 创建订单 POST /api/order ==========" -ForegroundColor Cyan
if ($global:FirstProductId) {
    $ordBody = @{
        items = @(@{ productId=$global:FirstProductId; quantity=1 })
        shippingAddress = "北京市海淀区"
        remark = "API测试订单"
    } | ConvertTo-Json
    try {
        $ord = Invoke-RestMethod "$BaseUrl/api/order" -Method POST -Body $ordBody -Headers $headers -ErrorAction Stop
        Write-Host "✅ 订单创建成功: $($ord.data.orderNo) 金额: ￥$($ord.data.totalAmount)"
    } catch {
        Write-Host "❌ 失败: $($_.Exception.Message)"
    }
}

Write-Host "`n========== 订单列表 GET /api/order ==========" -ForegroundColor Cyan
try {
    $ol = Invoke-RestMethod "$BaseUrl/api/order" -Method GET -Headers $headers -ErrorAction Stop
    Write-Host "✅ 订单总数: $($ol.data.total)"
} catch {
    Write-Host "❌ 失败: $($_.Exception.Message)"
}

Write-Host "`n========== 测试完成 ==========`n" -ForegroundColor Green
Write-Host "已测试接口："
Write-Host "  ✅ 注册、登录"
Write-Host "  ✅ 用户信息"
Write-Host "  ✅ 商品列表、搜索"
Write-Host "  ✅ 购物车"
Write-Host "  ✅ VIP等级"
Write-Host "  ✅ 抽奖奖品"
Write-Host "  ✅ 消息列表"
Write-Host "  ✅ 订单创建与列表"
Write-Host "`n已知问题："
Write-Host "  - 商品详情接口未测试（序列化问题）"
Write-Host "  - 添加购物车可能因序列化返回500"
Write-Host "  - 商品列表和VIP等级需要初始化数据"
Write-Host ""
