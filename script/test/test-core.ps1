$BASE_URL = "http://localhost:8080/api"

Write-Host "=== 测试核心功能 ===" -ForegroundColor Cyan

Write-Host "`n[1] 用户登录..." -ForegroundColor Yellow
try {
    $body = @{
        name = "testuser"
        password = "testpass"
    } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/auth/login" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode)" -ForegroundColor $(if($response.StatusCode -eq 200){'Green'}else{'Yellow'})
    if ($response.StatusCode -eq 200) {
        $content = $response.Content | ConvertFrom-Json
        if ($content.token) {
            Write-Host "  Token获取成功" -ForegroundColor Green
            $script:TOKEN = $content.token
        } else {
            Write-Host "  响应: $($response.Content.Substring(0, [Math]::Min(100, $response.Content.Length)))" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[2] 用户注册..." -ForegroundColor Yellow
try {
    $timestamp = Get-Date -Format "yyyyMMddHHmmss"
    $body = @{
        name = "test$timestamp"
        password = "Test123456"
        email = "test$timestamp@example.com"
    } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/auth/register" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode)" -ForegroundColor $(if($response.StatusCode -eq 200 -or $response.StatusCode -eq 201){'Green'}else{'Yellow'})
    if ($response.StatusCode -eq 200 -or $response.StatusCode -eq 201) {
        Write-Host "  注册成功" -ForegroundColor Green
    }
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[3] 获取订单列表..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/order" -Method GET -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[4] 创建订单..." -ForegroundColor Yellow
try {
    $body = @{
        addressId = 1
        items = @(
            @{ productId = 1; quantity = 1 }
        )
    } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/order" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[5] 获取订单统计..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/order/stats" -Method GET -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[6] 获取订单详情..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/order/1" -Method GET -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[7] 取消订单..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/order/1/cancel" -Method PUT -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[8] 确认收货..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/order/1/confirm" -Method PUT -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[9] 订单支付..." -ForegroundColor Yellow
try {
    $body = @{ paymentMethod = "alipay" } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/order/1/pay" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[10] 订单退款..." -ForegroundColor Yellow
try {
    $body = @{ reason = "不想要了" } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/order/1/refund" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[11] 订单评价..." -ForegroundColor Yellow
try {
    $body = @{
        content = "好评"
        rating = 5
    } | ConvertTo-Json
    $response = Invoke-WebRequest -Uri "$BASE_URL/order/1/review" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[12] 获取订单物流..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/order/1/logistics" -Method GET -TimeoutSec 10
    Write-Host "  状态码: $($response.StatusCode) (需登录401正常)" -ForegroundColor $(if($response.StatusCode -eq 401){'Green'}else{'Yellow'})
} catch {
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Cyan