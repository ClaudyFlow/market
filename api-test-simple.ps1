# 简单 API 测试脚本 - 直接执行每个步骤，打印详细信息

$BaseUrl = "http://localhost:8080"

function Call-API {
    param($Method, $Endpoint, $Token=$null, $Body=$null, $Query=$null)
    $uri = "$BaseUrl$Endpoint"
    if ($Query) {
        $q = ($Query.GetEnumerator() | ForEach-Object { "$($_.Key)=$($_.Value)" }) -join '&'
        $uri += "?" + $q
    }
    $h = @{ "Content-Type" = "application/json" }
    if ($Token) { $h["Authorization"] = "Bearer $Token" }
    try {
        if ($Body) { $bodyJson = $Body | ConvertTo-Json; $r = Invoke-RestMethod $uri -Method $Method -Headers $h -Body $bodyJson -ErrorAction Stop }
        else { $r = Invoke-RestMethod $uri -Method $Method -Headers $h -ErrorAction Stop }
        return @{ Ok=$true; Response=$r }
    } catch {
        $errMsg = $_.Exception.Message
        if ($_.Exception.Response) {
            $s = $_.Exception.Response.GetResponseStream()
            $rd = New-Object System.IO.StreamReader($s)
            $rd.BaseStream.Position = 0; $rd.DiscardBufferedData()
            $errMsg = $rd.ReadToEnd()
        }
        return @{ Ok=$false; Error=$errMsg }
    }
Write-Host "`n=== 注册新用户 ==="
$ts = Get-Date -Format "yyyyMMddHHmmss"
$reg = Call-API -Method POST -Endpoint "/api/auth/register" -Body @{
    name="t_$ts"; email="t$ts@test.com"; password="Test123456"; confirmPassword="Test123456"
}
if ($reg.Ok -and $reg.Response.success) {
    $token = $reg.Response.token
    Write-Host "✅ 注册成功. Token长度: $($token.Length)"
} else { Write-Host "❌ 注册失败: $($reg.Error)"; exit }

Write-Host "`n=== 登录 ==="
$login = Call-API -Method POST -Endpoint "/api/auth/login" -Body @{ name="t_$ts"; password="Test123456" }
if ($login.Ok -and $login.Response.success) {
    $token = $login.Response.token
    Write-Host "✅ 登录成功"
} else { Write-Host "❌ 登录失败: $($login.Error)"; exit }

Write-Host "`n=== 用户信息 GET /api/user/info ==="
$ui = Call-API -Method GET -Endpoint "/api/user/info" -Token $token
if ($ui.Ok -and $ui.Response.success) { Write-Host "✅ 用户: $($ui.Response.data.name)" } else { Write-Host "❌ $($ui.Error)" }

Write-Host "`n=== 商品列表 GET /api/product ==="
$pl = Call-API -Method GET -Endpoint "/api/product" -Token $token
if ($pl.Ok -and $pl.Response.success) { Write-Host "✅ 商品总数: $($pl.Response.data.totalElements)" } else { Write-Host "❌ $($pl.Error)" }

Write-Host "`n=== 商品搜索 GET /api/product/search?keyword=蓝牙 ==="
$ps = Call-API -Method GET -Endpoint "/api/product/search" -Token $token -Query @{ keyword="蓝牙" }
if ($ps.Ok -and $ps.Response.success) { Write-Host "✅ 搜索结果: $($ps.Response.data.totalElements) 个" } else { Write-Host "❌ $($ps.Error)" }

Write-Host "`n=== 获取购物车 GET /api/cart ==="
$cg = Call-API -Method GET -Endpoint "/api/cart" -Token $token
if ($cg.Ok -and $cg.Response.success) { Write-Host "✅ 购物车项目数: $($cg.Response.data.Count)" } else { Write-Host "❌ $($cg.Error)" }

Write-Host "`n=== VIP 等级 GET /api/user/vip/levels ==="
$vl = Call-API -Method GET -Endpoint "/api/user/vip/levels" -Token $token
if ($vl.Ok -and $vl.Response.success) { Write-Host "✅ VIP 等级数: $($vl.Response.data.Count)" } else { Write-Host "❌ $($vl.Error)" }

Write-Host "`n=== 抽奖奖品 GET /api/lottery/prizes ==="
$lp = Call-API -Method GET -Endpoint "/api/lottery/prizes" -Token $token
if ($lp.Ok -and $lp.Response.success) { Write-Host "✅ 奖品数: $($lp.Response.data.Count)" } else { Write-Host "❌ $($lp.Error)" }

Write-Host "`n=== 消息列表 GET /api/message/list ==="
$ml = Call-API -Method GET -Endpoint "/api/message/list" -Token $token
if ($ml.Ok -and $ml.Response.success) { Write-Host "✅ 消息数: $($ml.Response.data.total)" } else { Write-Host "❌ $($ml.Error)" }

Write-Host "`n=== 创建订单 POST /api/order ==="
$firstProductId = if ($pl.Ok -and $pl.Response.data.totalElements -gt 0) { $pl.Response.data.content[0].id } else { 1 }
$order = Call-API -Method POST -Endpoint "/api/order" -Token $token -Body @{
    items = @(@{ productId=$firstProductId; quantity=1 })
    shippingAddress = "测试地址"
    remark = "API测试"
}
if ($order.Ok -and $order.Response.success) { Write-Host "✅ 订单创建成功: $($order.Response.data.orderNo)" } else { Write-Host "❌ $($order.Error)" }

Write-Host "`n=== 订单列表 GET /api/order ==="
$ol = Call-API -Method GET -Endpoint "/api/order" -Token $token
if ($ol.Ok -and $ol.Response.success) { Write-Host "✅ 订单总数: $($ol.Response.data.total)" } else { Write-Host "❌ $($ol.Error)" }

Write-Host "`n测试完成.`n"
