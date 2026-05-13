$wr = [System.Net.WebRequest]::Create("http://localhost:8080/api/product")
$wr.Method = "GET"
$wr.Timeout = 10000
try {
    $resp = $wr.GetResponse()
    $status = [int]$resp.StatusCode
    Write-Host "商品列表: $status" -ForegroundColor $(if($status -eq 200){'Green'}else{'Red'})
    $resp.Close()
} catch {
    Write-Host "商品列表: 错误 - $($_.Exception.Message)" -ForegroundColor Red
}

$wr = [System.Net.WebRequest]::Create("http://localhost:8080/api/shop")
$wr.Method = "GET"
$wr.Timeout = 10000
try {
    $resp = $wr.GetResponse()
    $status = [int]$resp.StatusCode
    Write-Host "店铺列表: $status" -ForegroundColor $(if($status -eq 200){'Green'}else{'Red'})
    $resp.Close()
} catch {
    Write-Host "店铺列表: 错误 - $($_.Exception.Message)" -ForegroundColor Red
}

$wr = [System.Net.WebRequest]::Create("http://localhost:8080/api/home/banners")
$wr.Method = "GET"
$wr.Timeout = 10000
try {
    $resp = $wr.GetResponse()
    $status = [int]$resp.StatusCode
    Write-Host "首页轮播: $status" -ForegroundColor $(if($status -eq 200){'Green'}else{'Red'})
    $resp.Close()
} catch {
    Write-Host "首页轮播: 错误 - $($_.Exception.Message)" -ForegroundColor Red
}

$wr = [System.Net.WebRequest]::Create("http://localhost:8080/api/search/hot")
$wr.Method = "GET"
$wr.Timeout = 10000
try {
    $resp = $wr.GetResponse()
    $status = [int]$resp.StatusCode
    Write-Host "热门搜索: $status" -ForegroundColor $(if($status -eq 200){'Green'}else{'Red'})
    $resp.Close()
} catch {
    Write-Host "热门搜索: 错误 - $($_.Exception.Message)" -ForegroundColor Red
}

$wr = [System.Net.WebRequest]::Create("http://localhost:8080/api/forum/boards")
$wr.Method = "GET"
$wr.Timeout = 10000
try {
    $resp = $wr.GetResponse()
    $status = [int]$resp.StatusCode
    Write-Host "论坛版块: $status" -ForegroundColor $(if($status -eq 200){'Green'}else{'Red'})
    $resp.Close()
} catch {
    Write-Host "论坛版块: 错误 - $($_.Exception.Message)" -ForegroundColor Red
}

$wr = [System.Net.WebRequest]::Create("http://localhost:8080/api/actuator/health")
$wr.Method = "GET"
$wr.Timeout = 10000
try {
    $resp = $wr.GetResponse()
    $status = [int]$resp.StatusCode
    Write-Host "健康检查: $status" -ForegroundColor $(if($status -eq 200){'Green'}else{'Red'})
    $resp.Close()
} catch {
    Write-Host "健康检查: 错误 - $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Cyan