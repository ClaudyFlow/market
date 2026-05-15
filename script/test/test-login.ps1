$BASE_URL = "http://localhost:8080/api"

Write-Host "=== 测试登录注册 ===" -ForegroundColor Cyan

Write-Host "`n[1] 用户登录..." -ForegroundColor Yellow
try {
    $body = '{"name":"admin","password":"admin123"}'
    $response = Invoke-WebRequest -Uri "$BASE_URL/auth/login" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10 -ErrorAction Stop
    Write-Host "  状态码: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "  内容: $($response.Content.Substring(0, [Math]::Min(200, $response.Content.Length)))" -ForegroundColor White
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "  状态码: $statusCode" -ForegroundColor Yellow
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n[2] 用户注册..." -ForegroundColor Yellow
try {
    $ts = Get-Random
    $body = "{`"name`":`"testuser$ts`",`"password`":`"Test123456`",`"email`":`"test$ts@test.com`"}"
    $response = Invoke-WebRequest -Uri "$BASE_URL/auth/register" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 10 -ErrorAction Stop
    Write-Host "  状态码: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "  内容: $($response.Content.Substring(0, [Math]::Min(200, $response.Content.Length)))" -ForegroundColor White
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "  状态码: $statusCode" -ForegroundColor Yellow
    Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 完成 ===" -ForegroundColor Cyan