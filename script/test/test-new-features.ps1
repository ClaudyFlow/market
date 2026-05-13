$BASE_URL = "http://localhost:8080/api"

Write-Host "=== 测试新功能 ===" -ForegroundColor Cyan

Write-Host "`n[1] 分享订单..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/order/1/share" -Method POST -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "  状态码: $statusCode (需登录401正常)" -ForegroundColor $(if($statusCode -eq 401){'Green'}else{'Yellow'})
}

Write-Host "`n[2] 评价商家(店铺)..." -ForegroundColor Yellow
try {
    $body = @{
        orderId = 1
        rating = 5
        descriptionScore = 5.0
        serviceScore = 5.0
        logisticsScore = 5.0
        content = "商家服务很好"
    } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/shop/1/review" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "  状态码: $statusCode (需登录401正常)" -ForegroundColor $(if($statusCode -eq 401){'Green'}else{'Yellow'})
}

Write-Host "`n[3] 获取店铺评价列表..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/shop/1/reviews" -Method GET -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode)" -ForegroundColor $(if($response.StatusCode -eq 200){'Green'}else{'Yellow'})
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "  状态码: $statusCode" -ForegroundColor Yellow
}

Write-Host "`n[4] 商家回复评价..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/merchant/review/1/reply?content=谢谢好评" -Method POST -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需商家登录403正常)" -ForegroundColor $(if($response.StatusCode -eq 403 -or $response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "  状态码: $statusCode (需商家登录403正常)" -ForegroundColor $(if($statusCode -eq 403 -or $statusCode -eq 401){'Green'}else{'Yellow'})
}

Write-Host "`n[5] 获取商家评价统计..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/merchant/review/stats" -Method GET -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需商家登录403正常)" -ForegroundColor $(if($response.StatusCode -eq 403 -or $response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "  状态码: $statusCode (需商家登录403正常)" -ForegroundColor $(if($statusCode -eq 403 -or $statusCode -eq 401){'Green'}else{'Yellow'})
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Cyan