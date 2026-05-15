# 集成测试脚本 - 端到端业务流程测试
# 用法：Powershell -ExecutionPolicy Bypass -File .\integration-test.ps1

$baseUrl = "http://localhost:8080/api"

function Write-Step {
    param([string]$msg)
    Write-Host "`n>>> $msg" -ForegroundColor Cyan
}

function Assert-Success {
    param($response, $stepName)
    if ($response.success -eq $false -or $response.code -ne 200) {
        Write-Host "❌ [$stepName] 失败: $($response.message)" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ [$stepName] 成功" -ForegroundColor Green
    return $true
}

Write-Host "===== 集成测试：完整电商流程 =====" -ForegroundColor Yellow

# 步骤1: 用户注册
Write-Step "1. 用户注册"
$reg = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body (@{
    name = "integration_user_$(Get-Date -Format 'HHmmss')"
    email = "integration@test.com"
    password = "Test123456"
    confirmPassword = "Test123456"
} | ConvertTo-Json) -ContentType "application/json"
Assert-Success $reg "注册"
$token = $reg.token
$headers = @{ Authorization = "Bearer $token" }

# 步骤2: 用户登录
Write-Step "2. 用户登录"
$login = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body (@{
    name = $reg.data.name
    password = "Test123456"
} | ConvertTo-Json) -ContentType "application/json"
Assert-Success $login "登录"

# 步骤3: 获取用户信息
Write-Step "3. 获取用户信息"
$userInfo = Invoke-RestMethod -Uri "$baseUrl/user/info" -Method Get -Headers $headers
Assert-Success $userInfo "获取用户信息"
Write-Host "   用户名: $($userInfo.data.name), VIP等级: $($userInfo.data.vipLevel)"

# 步骤4: 浏览商品列表
Write-Step "4. 浏览商品列表"
$productList = Invoke-RestMethod -Uri "$baseUrl/product?page=1&size=10" -Method Get -Headers $headers
Assert-Success $productList "获取商品列表"
Write-Host "   共找到 $($productList.data.totalElements) 个商品"
$product = $productList.data.content[0]
Write-Host "   选择商品: $($product.name) (ID: $($product.id)) - 价格: $($product.price)"

# 步骤5: 搜索商品
Write-Step "5. 搜索商品"
$search = Invoke-RestMethod -Uri "$baseUrl/product/search?keyword=$($product.name.Substring(0,[Math]::Min(2,$product.name.Length)))" -Method Get -Headers $headers
Assert-Success $search "商品搜索"
Write-Host "   搜索结果: $($search.data.content.Count) 个"

# 步骤6: 查看商品详情
Write-Step "6. 查看商品详情"
$productDetail = Invoke-RestMethod -Uri "$baseUrl/product/$($product.id)" -Method Get -Headers $headers
Assert-Success $productDetail "商品详情"
Write-Host "   详情: $($productDetail.data.description.Substring(0,[Math]::Min(50,$productDetail.data.description.Length)))..."

# 步骤7: 加入购物车
Write-Step "7. 加入购物车"
$cartAdd = Invoke-RestMethod -Uri "$baseUrl/cart/add?productId=$($product.id)&quantity=1" -Method Post -Headers $headers -ContentType "application/x-www-form-urlencoded"
Assert-Success $cartAdd "加入购物车"
Write-Host "   已添加: $($cartAdd.data.product.name) x $($cartAdd.data.quantity)"

# 步骤8: 查看购物车
Write-Step "8. 查看购物车"
$cart = Invoke-RestMethod -Uri "$baseUrl/cart" -Method Get -Headers $headers
Assert-Success $cart "查看购物车"
Write-Host "   购物车商品数: $($cart.data.items.Count)"
$cartItem = $cart.data.items[0]

# 步骤9: 选中购物车商品
Write-Step "9. 选中购物车商品"
$select = Invoke-RestMethod -Uri "$baseUrl/cart/select/$($cartItem.id)?selected=true" -Method Put -Headers $headers
Assert-Success $select "选中商品"

# 步骤10: 更新购物车数量
Write-Step "10. 更新购物车数量"
$updateQty = Invoke-RestMethod -Uri "$baseUrl/cart/update/$($cartItem.id)?quantity=2" -Method Put -Headers $headers
Assert-Success $updateQty "更新数量"
Write-Host "   新数量: $($updateQty.data.quantity)"

# 步骤11: 获取选中商品列表
Write-Step "11. 获取选中商品列表"
$selected = Invoke-RestMethod -Uri "$baseUrl/cart/selected" -Method Get -Headers $headers
Assert-Success $selected "获取选中商品"
Write-Host "   选中商品数: $($selected.data.Count)"

# 步骤12: 检查购物车库存
Write-Step "12. 检查购物车库存"
$checkStock = Invoke-RestMethod -Uri "$baseUrl/cart/check-stock" -Method Get -Headers $headers
Assert-Success $checkStock "检查库存"
Write-Host "   全部有效: $($checkStock.data.allValid)"

# 步骤13: 获取VIP等级列表
Write-Step "13. 获取VIP等级列表"
$vipLevels = Invoke-RestMethod -Uri "$baseUrl/user/vip/levels" -Method Get -Headers $headers
Assert-Success $vipLevels "获取VIP等级"
Write-Host "   VIP等级数: $($vipLevels.data.Count)"

# 步骤14: 创建地址
Write-Step "14. 创建地址"
$address = @{
    name = "收货人"
    phone = "13800138000"
    address = "测试地址"
    isDefault = $true
}

# 步骤15: 创建订单
Write-Step "15. 创建订单"
$orderItems = $selected.data | ForEach-Object {
    @{
        id = $_.id
        quantity = $_.quantity
    }
}
$orderCreateBody = @{
    address = $address
    items = $orderItems
    couponId = $null
} | ConvertTo-Json
$order = Invoke-RestMethod -Uri "$baseUrl/order/create" -Method Post -Headers $headers -Body $orderCreateBody -ContentType "application/json"
Assert-Success $order "创建订单"
Write-Host "   订单号: $($order.data.orderSn), 金额: $($order.data.totalAmount)"

# 步骤16: 获取订单列表
Write-Step "16. 获取订单列表"
$orderList = Invoke-RestMethod -Uri "$baseUrl/order?page=1&size=10" -Method Get -Headers $headers
Assert-Success $orderList "获取订单列表"
Write-Host "   订单数: $($orderList.data.totalElements)"

# 步骤17: 获取奖品列表
Write-Step "17. 获取奖品列表"
$prizes = Invoke-RestMethod -Uri "$baseUrl/lottery/prizes" -Method Get -Headers $headers
Assert-Success $prizes "获取奖品列表"
Write-Host "   奖品数: $($prizes.data.Count)"

# 步骤18: 清空购物车
Write-Step "18. 清空购物车"
$clear = Invoke-RestMethod -Uri "$baseUrl/cart/clear" -Method Delete -Headers $headers
Assert-Success $clear "清空购物车"

# 总结
Write-Host "`n===== 集成测试全部通过! =====" -ForegroundColor Green
Write-Host "所有步骤成功完成，电商核心流程正常。"