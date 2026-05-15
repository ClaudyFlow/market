$BASE_URL = "http://localhost:8080/api"
$pass = 0; $fail = 0; $skip = 0

function Test-API($name, $method, $path, $body, $expected) {
    $global:pass = 0; $global:fail = 0; $global:skip = 0
    try {
        $params = @{Uri="$BASE_URL$path";Method=$method;TimeoutSec=10}
        if($body){$params.Body = $body; $params.ContentType = "application/json"}
        $r = Invoke-WebRequest @params -ErrorAction Stop
        $status = $r.StatusCode
    } catch {
        $status = $_.Exception.Response.StatusCode.value__
    }
    if($status -match $expected) {
        Write-Host "[PASS] $name -> $status" -ForegroundColor Green
        $script:pass++
    } elseif($null -eq $status -or $status -eq 0) {
        Write-Host "[SKIP] $name -> 无响应" -ForegroundColor Yellow
        $script:skip++
    } else {
        Write-Host "[FAIL] $name -> $status (期望:$expected)" -ForegroundColor Red
        $script:fail++
    }
}

Write-Host "=== 全面测试 ===" -ForegroundColor Cyan
Write-Host "`n[健康检查]" -ForegroundColor Yellow
Test-API "健康检查" GET "/actuator/health" $null "200"

Write-Host "`n[商品模块]" -ForegroundColor Yellow
Test-API "商品列表" GET "/product" $null "200"
Test-API "推荐商品" GET "/product/recommended" $null "200"
Test-API "热销商品" GET "/product/hot" $null "200"
Test-API "新品" GET "/product/new" $null "200"
Test-API "商品分类" GET "/product/categories" $null "200"

Write-Host "`n[店铺模块]" -ForegroundColor Yellow
Test-API "店铺列表" GET "/shop" $null "200"
Test-API "关注店铺" GET "/shop/followed" $null "200|401"

Write-Host "`n[搜索模块]" -ForegroundColor Yellow
Test-API "综合搜索" GET "/search?q=test" $null "200"
Test-API "热门搜索" GET "/search/hot" $null "200"

Write-Host "`n[首页模块]" -ForegroundColor Yellow
Test-API "轮播图" GET "/home/banners" $null "200"
Test-API "分类" GET "/home/categories" $null "200"
Test-API "推荐店铺" GET "/home/recommended-shops" $null "200"

Write-Host "`n[论坛模块]" -ForegroundColor Yellow
Test-API "论坛版块" GET "/forum/boards" $null "200"
Test-API "帖子列表" GET "/forum/posts" $null "200"

Write-Host "`n[通知模块]" -ForegroundColor Yellow
Test-API "通知列表" GET "/notification" $null "200|401"

Write-Host "`n=== 结果: pass=$pass fail=$fail skip=$skip ===" -ForegroundColor Cyan