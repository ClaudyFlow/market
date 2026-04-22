$BASE_URL = "http://localhost:8080/api"

Write-Host "=== 测试购物车功能 ===" -ForegroundColor Cyan

Write-Host "`n[1] 获取商品列表..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/product" -Method GET -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode)" -ForegroundColor $(if($response.StatusCode -eq 200){'Green'}else{'Red'})

    if ($response.StatusCode -eq 200) {
        $products = $response.Content | ConvertFrom-Json
        if ($products.data -and $products.data.Count -gt 0) {
            $firstProductId = $products.data[0].id
            Write-Host "  商品ID: $firstProductId" -ForegroundColor Green
        } elseif ($products -is [array] -and $products.Count -gt 0) {
            $firstProductId = $products[0].id
            Write-Host "  商品ID: $firstProductId" -ForegroundColor Green
        } else {
            $firstProductId = 1
            Write-Host "  使用默认商品ID: $firstProductId" -ForegroundColor Yellow
        }
    } else {
        $firstProductId = 1
        Write-Host "  使用默认商品ID: $firstProductId" -ForegroundColor Yellow
    }
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
    $firstProductId = 1
}

Write-Host "`n[2] 获取购物车..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/cart" -Method GET -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需要登录，401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[3] 添加到购物车..." -ForegroundColor Yellow
try {
    $body = @{
        productId = $firstProductId
        quantity = 1
    } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/cart/add" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需要登录，401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[4] 获取购物车总数..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/cart/total" -Method GET -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需要登录，401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[5] 更新购物车数量..." -ForegroundColor Yellow
try {
    $body = @{ quantity = 2 } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/cart/update/$firstProductId" -Method PUT -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需要登录，401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[6] 选中购物车商品..." -ForegroundColor Yellow
try {
    $body = @{ selected = $true } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/cart/select/$firstProductId" -Method PUT -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需要登录，401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[7] 全选购物车商品..." -ForegroundColor Yellow
try {
    $body = @{ selected = $true } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/cart/select-all" -Method PUT -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需要登录，401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[8] 获取选中商品..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/cart/selected" -Method GET -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需要登录，401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[9] 检查库存..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/cart/check-stock" -Method GET -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需要登录，401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[10] 创建订单(结算)..." -ForegroundColor Yellow
try {
    $body = @{
        addressId = 1
        items = @(
            @{ productId = $firstProductId; quantity = 1 }
        )
    } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/order" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需要登录，401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[11] 删除购物车商品..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/cart/remove/$firstProductId" -Method DELETE -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需要登录，401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[12] 清空购物车..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/cart/clear" -Method DELETE -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需要登录，401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Cyan
