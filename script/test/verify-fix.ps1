Start-Sleep -Seconds 20

Write-Host "=== 验证修复结果 ===" -ForegroundColor Cyan

$w = [System.Net.WebRequest]::Create("http://localhost:8080/api/forum/boards")
$w.Method = "GET"
$w.Timeout = 10000
try {
    $r = $w.GetResponse()
    Write-Host "论坛版块: $($r.StatusCode)" -ForegroundColor Green
    $r.Close()
} catch {
    Write-Host "论坛版块: 错误" -ForegroundColor Red
}

$w = [System.Net.WebRequest]::Create("http://localhost:8080/actuator/health")
$w.Method = "GET"
$w.Timeout = 10000
try {
    $r = $w.GetResponse()
    Write-Host "健康检查: $($r.StatusCode)" -ForegroundColor Green
    $r.Close()
} catch {
    Write-Host "健康检查: 错误" -ForegroundColor Red
}

$w = [System.Net.WebRequest]::Create("http://localhost:8080/api/product")
$w.Method = "GET"
$w.Timeout = 10000
try {
    $r = $w.GetResponse()
    Write-Host "商品列表: $($r.StatusCode)" -ForegroundColor Green
    $r.Close()
} catch {
    Write-Host "商品列表: 错误" -ForegroundColor Red
}

Write-Host "=== 验证完成 ===" -ForegroundColor Cyan