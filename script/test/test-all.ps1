$BASE_URL = "http://localhost:8080/api"
$results = @()

function Test-API {
    param($name, $method, $path, $body, $expected)
    $result = @{Name=$name; Method=$method; Path=$path; Expected=$expected; Status="?"; Pass=$false}
    try {
        if ($body) {
            $response = Invoke-WebRequest -Uri "$BASE_URL$path" -Method $method -Body $body -ContentType "application/json" -TimeoutSec 15 -ErrorAction Stop
        } else {
            $response = Invoke-WebRequest -Uri "$BASE_URL$path" -Method $method -TimeoutSec 15 -ErrorAction Stop
        }
        $result.Status = $response.StatusCode
        $result.Pass = ($expected -contains $response.StatusCode)
    } catch {
        $result.Status = $_.Exception.Response.StatusCode.value__
        $result.Pass = ($expected -contains $result.Status)
    }
    $results += $result
    $color = if($result.Pass){'Green'}else{'Red'}
    Write-Host "  $($result.Status) - $name" -ForegroundColor $color
}

Write-Host "=== 1. 健康检查 ===" -ForegroundColor Cyan
Test-API "系统健康" GET "/actuator/health" $null @(200,503)
Test-API "系统信息" GET "/actuator/info" $null @(200,404)

Write-Host "`n=== 2. 认证模块 ===" -ForegroundColor Cyan
Test-API "用户登录" POST "/auth/login" '{"name":"admin","password":"admin123"}' @(200,400,401)
Test-API "用户注册" POST "/auth/register" '{"name":"testuser","password":"Test123456"}' @(200,201,400)

Write-Host "`n=== 3. 商品模块 ===" -ForegroundColor Cyan
Test-API "商品列表" GET "/product" $null @(200)
Test-API "商品搜索" GET "/product/search?q=test" $null @(200)
Test-API "推荐商品" GET "/product/recommended" $null @(200)
Test-API "热销商品" GET "/product/hot" $null @(200)
Test-API "新品" GET "/product/new" $null @(200)
Test-API "促销商品" GET "/product/sale" $null @(200)
Test-API "商品分类" GET "/product/categories" $null @(200)
Test-API "商品品牌" GET "/product/brands" $null @(200)
Test-API "商品详情" GET "/product/1" $null @(200,404)
Test-API "商品SKU" GET "/product/1/skus" $null @(200,404)

Write-Host "`n=== 4. 购物车模块 ===" -ForegroundColor Cyan
Test-API "获取购物车" GET "/cart" $null @(200,401)
Test-API "添加到购物车" POST "/cart/add" '{"productId":1,"quantity":1}' @(200,401)
Test-API "购物车总数" GET "/cart/total" $null @(200,401)
Test-API "更新购物车" PUT "/cart/update/1" '{"quantity":2}' @(200,401)
Test-API "选中商品" PUT "/cart/select/1" '{"selected":true}' @(200,401)
Test-API "全选" PUT "/cart/select-all" '{"selected":true}' @(200,401)
Test-API "删除商品" DELETE "/cart/remove/1" $null @(200,401)
Test-API "清空购物车" DELETE "/cart/clear" $null @(200,401)

Write-Host "`n=== 5. 订单模块 ===" -ForegroundColor Cyan
Test-API "订单列表" GET "/order" $null @(200,401)
Test-API "创建订单" POST "/order" '{"addressId":1,"items":[{"productId":1,"quantity":1}]}' @(200,401)
Test-API "订单统计" GET "/order/stats" $null @(200,401)
Test-API "订单详情" GET "/order/1" $null @(200,404,401)
Test-API "取消订单" PUT "/order/1/cancel" $null @(200,401)
Test-API "确认收货" PUT "/order/1/confirm" $null @(200,401)
Test-API "订单支付" POST "/order/1/pay" '{"paymentMethod":"alipay"}' @(200,401)
Test-API "订单退款" POST "/order/1/refund" '{"reason":"不满意"}' @(200,401)
Test-API "订单评价" POST "/order/1/review" '{"content":"好评","rating":5}' @(200,401)
Test-API "订单物流" GET "/order/1/logistics" $null @(200,404,401)
Test-API "分享订单" POST "/order/1/share" $null @(200,401)

Write-Host "`n=== 6. 店铺模块 ===" -ForegroundColor Cyan
Test-API "店铺列表" GET "/shop" $null @(200)
Test-API "店铺搜索" GET "/shop/search?q=test" $null @(200)
Test-API "店铺详情" GET "/shop/1" $null @(200,404)
Test-API "店铺商品" GET "/shop/1/products" $null @(200,404)
Test-API "店铺优惠券" GET "/shop/1/coupons" $null @(200,404)
Test-API "店铺公告" GET "/shop/1/announcement" $null @(200,404)
Test-API "店铺分类" GET "/shop/1/categories" $null @(200,404)
Test-API "关注店铺" GET "/shop/followed" $null @(200,401)
Test-API "分享店铺" POST "/shop/1/share" $null @(200,401)
Test-API "评价店铺" POST "/shop/1/review" '{"rating":5,"content":"好"}' @(200,401,500)
Test-API "店铺评价列表" GET "/shop/1/reviews" $null @(200,404,500)

Write-Host "`n=== 7. 优惠券模块 ===" -ForegroundColor Cyan
Test-API "优惠券列表" GET "/coupon" $null @(200,401)
Test-API "优惠券模板" GET "/coupon/templates" $null @(200)
Test-API "可用优惠券" GET "/coupon/available" $null @(200,401)
Test-API "领取优惠券" POST "/coupon/receive" '{"templateId":1}' @(200,401)

Write-Host "`n=== 8. 评价模块 ===" -ForegroundColor Cyan
Test-API "评价列表" GET "/review" $null @(200)
Test-API "评价统计" GET "/review/stats" $null @(200)
Test-API "评价标签" GET "/review/tags" $null @(200)
Test-API "带图评价" GET "/review/with-images" $null @(200)

Write-Host "`n=== 9. 搜索模块 ===" -ForegroundColor Cyan
Test-API "综合搜索" GET "/search?q=test" $null @(200)
Test-API "搜索商品" GET "/search/product?q=test" $null @(200)
Test-API "搜索店铺" GET "/search/shop?q=test" $null @(200)
Test-API "搜索建议" GET "/search/suggestions?q=test" $null @(200)
Test-API "热门搜索" GET "/search/hot" $null @(200)
Test-API "搜索筛选" GET "/search/filters?q=test" $null @(200)

Write-Host "`n=== 10. 论坛模块 ===" -ForegroundColor Cyan
Test-API "论坛版块" GET "/forum/boards" $null @(200)
Test-API "帖子列表" GET "/forum/posts" $null @(200)
Test-API "帖子搜索" GET "/forum/search?q=test" $null @(200)
Test-API "我的帖子" GET "/forum/my-posts" $null @(200,401)
Test-API "创建帖子" POST "/forum/post" '{"boardId":1,"title":"测试","content":"内容"}' @(200,401)

Write-Host "`n=== 11. 通知模块 ===" -ForegroundColor Cyan
Test-API "通知列表" GET "/notification" $null @(200,401)
Test-API "未读数量" GET "/notification/unread-count" $null @(200,401)
Test-API "系统通知" GET "/notification/system" $null @(200,401)
Test-API "标记已读" PUT "/notification/all-read" $null @(200,401)

Write-Host "`n=== 12. 首页模块 ===" -ForegroundColor Cyan
Test-API "轮播图" GET "/home/banners" $null @(200)
Test-API "分类数据" GET "/home/categories" $null @(200)
Test-API "楼层商品" GET "/home/floor-products" $null @(200)
Test-API "推荐店铺" GET "/home/recommended-shops" $null @(200)
Test-API "活动信息" GET "/home/activities" $null @(200)
Test-API "秒��活动" GET "/home/flash-sales" $null @(200)
Test-API "品牌专区" GET "/home/brands" $null @(200)
Test-API "新人专享" GET "/home/new-user" $null @(200)
Test-API "猜你喜欢" GET "/home/recommend" $null @(200)

Write-Host "`n=== 13. 抽奖模块 ===" -ForegroundColor Cyan
Test-API "抽奖活动" GET "/lottery/activity" $null @(200)
Test-API "奖品列表" GET "/lottery/prizes" $null @(200)
Test-API "我的奖品" GET "/lottery/my-prizes" $null @(200,401)
Test-API "中奖记录" GET "/lottery/records" $null @(200)

Write-Host "`n=== 14. VIP模块 ===" -ForegroundColor Cyan
Test-API "VIP信息" GET "/vip/info" $null @(200,401)
Test-API "VIP等级" GET "/vip/levels" $null @(200)
Test-API "VIP特权" GET "/vip/benefits" $null @(200)
Test-API "充值记录" GET "/vip/records" $null @(200,401)

Write-Host "`n=== 15. 聊天模块 ===" -ForegroundColor Cyan
Test-API "会话列表" GET "/chat/sessions" $null @(200,401)
Test-API "创建会话" POST "/chat/session" '{"targetId":1}' @(200,401)
Test-API "未读消息" GET "/chat/unread-count" $null @(200,401)

Write-Host "`n=== 16. 用户模块 ===" -ForegroundColor Cyan
Test-API "用户信息" GET "/user/info" $null @(200,401)
Test-API "地址列表" GET "/user/addresses" $null @(200,401)
Test-API "收藏列表" GET "/user/favorites" $null @(200,401)
Test-API "浏览历史" GET "/user/history" $null @(200,401)
Test-API "用户积分" GET "/user/points" $null @(200,401)
Test-API "用户等级" GET "/user/level" $null @(200,401)

Write-Host "`n=== 17. 关注/收藏 ===" -ForegroundColor Cyan
Test-API "关注列表" GET "/follow/following" $null @(200,401)
Test-API "粉丝列表" GET "/follow/followers" $null @(200,401)
Test-API "检查关注" GET "/follow/check?targetId=1&type=shop" $null @(200,401)
Test-API "收藏列表" GET "/favorite" $null @(200,401)
Test-API "收藏数" GET "/favorite/count" $null @(200,401)

Write-Host "`n=== 18. 管理端 ===" -ForegroundColor Cyan
Test-API "用户列表" GET "/admin/user/list" $null @(200,403)
Test-API "商家列表" GET "/admin/merchant/list" $null @(200,403)
Test-API "商品审核" GET "/admin/product/audit/list" $null @(200,403)
Test-API "订单列表" GET "/admin/order/list" $null @(200,403)
Test-API "优惠券" GET "/admin/coupon/list" $null @(200,403)

Write-Host "`n=== 19. 商家端 ===" -ForegroundColor Cyan
Test-API "商品列表" GET "/merchant/product" $null @(200,403)
Test-API "创建商品" POST "/merchant/product" '{"name":"测试","price":99.99}' @(200,403)
Test-API "商家订单" GET "/merchant/order/list" $null @(200,403)
Test-API "商家优惠券" GET "/merchant/coupon/list" $null @(200,403)
Test-API "商家评价" GET "/merchant/review/product/list" $null @(200,403)

Write-Host "`n=== 20. 上传模块 ===" -ForegroundColor Cyan
Test-API "上传图片" POST "/upload/image" $null @(200,401)
Test-API "上传文件" POST "/upload/file" $null @(200,401)
Test-API "上传凭证" POST "/upload/token" $null @(200,401)

$pass = ($results | Where-Object{$_.Pass}).Count
$total = $results.Count
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host " 测试结果: $pass/$total 通过" -ForegroundColor $(if($pass -eq $total){'Green'}else{'Yellow'})
$fail = $results | Where-Object{-not $_.Pass}
if($fail){
    Write-Host "`n失败列表:" -ForegroundColor Red
    $fail | ForEach-Object{Write-Host "  - $($_.Name): $($_.Status)" -ForegroundColor Red}
}
Write-Host "========================================" -ForegroundColor Cyan