@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul

:: ========================================
::     Market Platform - API Test Script
:: ========================================

:: Default configuration
set "BASE_URL=%~1"
if "%BASE_URL%"=="" set "BASE_URL=http://localhost:8080/api"

:: Counters
set "TOTAL=0"
set "PASS=0"
set "FAIL=0"
set "SKIP=0"

echo.
echo ========================================
echo     Market Platform - API Test
echo ========================================
echo.
echo  Backend URL: %BASE_URL%
echo  Test Time: %date% %time%
echo ========================================
echo.

:: 检查服务器是否运行
echo [检查] 正在连接服务器...
curl -s -o nul -w "%%{http_code}" "%BASE_URL%/actuator/health" > test_health.tmp 2>nul
set /p HEALTH_CODE=<test_health.tmp
del /q test_health.tmp >nul 2>&1

if "!HEALTH_CODE!"=="000" (
    echo.
    echo [警告] 无法连接到后端服务器: %BASE_URL%
    echo 请确保后端服务已启动: script\start-backend.bat
    echo.
    echo 提示: 测试仍将继续，但所有接口将显示为 SKIP
    echo 按 Ctrl+C 可中止测试
    echo.
    timeout /t 5 /nobreak >nul
    echo.
) else (
    echo [OK] 服务器响应正常 (HTTP !HEALTH_CODE!)
    echo.
)

:: 测试单个接口
:: 参数: %1=方法 %2=路径 %3=描述 %4=预期状态码 %5=请求体
:test_api
set /a TOTAL+=1
set "METHOD=%~1"
set "URL=%~2"
set "DESC=%~3"
set "EXPECTED=%~4"
if "!EXPECTED!"=="" set "EXPECTED=200"
set "BODY=%~5"

set "FULLURL=!BASE_URL!!URL!"
echo [!TOTAL!] 测试: !DESC!
echo       !METHOD! !FULLURL!

if "!BODY!"=="" (
    curl -s -o test_resp.tmp -w "%%{http_code}" -X "!METHOD!" -H "Content-Type: application/json" "!FULLURL!" > test_code.tmp 2>nul
) else (
    curl -s -o test_resp.tmp -w "%%{http_code}" -X "!METHOD!" -H "Content-Type: application/json" -d "!BODY!" "!FULLURL!" > test_code.tmp 2>nul
)

set /p STATUS=<test_code.tmp
del /q test_code.tmp >nul 2>&1
del /q test_resp.tmp >nul 2>&1

if "!STATUS!"=="!EXPECTED!" (
    echo       PASS (HTTP !STATUS!)
    set /a PASS+=1
) else (
    if "!STATUS!"=="000" (
        echo       SKIP (无法连接服务器)
        set /a SKIP+=1
    ) else (
        echo       FAIL (HTTP !STATUS!, 预期 !EXPECTED!)
        set /a FAIL+=1
    )
)
echo.
goto :eof

:: ========================================
:: 测试开始
:: ========================================

echo === 1. 健康检查 ===
echo.
call :test_api GET "/actuator/health" "系统健康检查" "200"
call :test_api GET "/actuator/info" "系统信息" "200"

echo === 2. 认证模块 (/auth) ===
echo.
call :test_api POST "/auth/login" "用户登录" "200" "{\"username\":\"test\",\"password\":\"test\"}"
call :test_api POST "/auth/register" "用户注册" "200" "{\"username\":\"testapi\",\"password\":\"Test1234!\",\"email\":\"test@api.com\"}"
call :test_api POST "/auth/reset-password" "重置密码" "200" "{\"email\":\"test@api.com\"}"
call :test_api GET "/auth/validate" "验证Token" "401"

echo === 3. 用户模块 (/user) ===
echo.
call :test_api GET "/user/info" "获取当前用户信息" "401"
call :test_api PUT "/user/info" "更新用户信息" "401"
call :test_api GET "/user/addresses" "获取地址列表" "401"
call :test_api GET "/user/favorites" "获取收藏列表" "401"
call :test_api GET "/user/history" "获取浏览历史" "401"
call :test_api GET "/user/points" "获取用户积分" "401"
call :test_api GET "/user/level" "获取用户等级" "401"

echo === 4. 商品模块 (/product) ===
echo.
call :test_api GET "/product" "获取商品列表" "200"
call :test_api GET "/product/search?q=test" "搜索商品" "200"
call :test_api GET "/product/recommended" "推荐商品" "200"
call :test_api GET "/product/hot" "热销商品" "200"
call :test_api GET "/product/new" "新品" "200"
call :test_api GET "/product/sale" "促销商品" "200"
call :test_api GET "/product/categories" "获取商品分类" "200"
call :test_api GET "/product/brands" "获取商品品牌" "200"
call :test_api GET "/product/attributes" "获取商品属性" "200"
call :test_api GET "/product/1" "获取商品详情" "200"
call :test_api GET "/product/1/skus" "获取商品SKU" "200"
call :test_api GET "/product/1/images" "获取商品图片" "200"
call :test_api GET "/product/1/comments" "获取商品评价" "200"
call :test_api GET "/product/1/stock" "获取商品库存" "200"

echo === 5. 购物车模块 (/cart) ===
echo.
call :test_api GET "/cart" "获取购物车" "401"
call :test_api POST "/cart/add" "添加到购物车" "401" "{\"productId\":1,\"quantity\":1}"
call :test_api PUT "/cart/update/1" "更新购物车数量" "401" "{\"quantity\":2}"
call :test_api DELETE "/cart/remove/1" "删除购物车商品" "401"
call :test_api DELETE "/cart/clear" "清空购物车" "401"
call :test_api GET "/cart/total" "获取购物车总数" "401"
call :test_api GET "/cart/selected" "获取选中商品" "401"
call :test_api PUT "/cart/select-all" "全选" "401" "{\"selected\":true}"
call :test_api POST "/cart/merge" "合并购物车" "401"
call :test_api GET "/cart/check-stock" "检查库存" "401"

echo === 6. 订单模块 (/order) ===
echo.
call :test_api POST "/order" "创建订单" "401" "{\"addressId\":1,\"items\":[{\"productId\":1,\"quantity\":1}]}"
call :test_api GET "/order" "获取订单列表" "401"
call :test_api GET "/order/stats" "获取订单统计" "401"
call :test_api GET "/order/1" "获取订单详情" "401"
call :test_api PUT "/order/1/cancel" "取消订单" "401"
call :test_api PUT "/order/1/confirm" "确认收货" "401"
call :test_api POST "/order/1/pay" "订单支付" "401"
call :test_api POST "/order/1/refund" "订单退款" "401"
call :test_api POST "/order/1/review" "订单评价" "401" "{\"content\":\"好评\",\"rating\":5}"
call :test_api GET "/order/1/logistics" "获取订单物流" "401"

echo === 7. 店铺模块 (/shop) ===
echo.
call :test_api GET "/shop" "获取店铺列表" "200"
call :test_api GET "/shop/search?q=test" "搜索店铺" "200"
call :test_api GET "/shop/1" "获取店铺详情" "200"
call :test_api GET "/shop/1/products" "获取店铺商品" "200"
call :test_api GET "/shop/1/coupons" "获取店铺优惠券" "200"
call :test_api GET "/shop/1/announcement" "获取店铺公告" "200"
call :test_api GET "/shop/1/categories" "获取店铺分类" "200"
call :test_api GET "/shop/1/following" "检查是否已关注" "401"
call :test_api GET "/shop/followed" "获取关注店铺列表" "401"

echo === 8. 优惠券模块 (/coupon) ===
echo.
call :test_api GET "/coupon" "获取优惠券列表" "401"
call :test_api GET "/coupon/templates" "获取优惠券模板" "200"
call :test_api GET "/coupon/available" "获取可用优惠券" "401"
call :test_api GET "/coupon/expiring" "获取即将过期优惠券" "401"
call :test_api POST "/coupon/receive" "领取优惠券" "401" "{\"templateId\":1}"

echo === 9. 评价模块 (/review) ===
echo.
call :test_api GET "/review" "获取评价列表" "200"
call :test_api GET "/review/stats" "获取评价统计" "200"
call :test_api GET "/review/tags" "获取评价标签" "200"
call :test_api GET "/review/with-images" "获取带图评价" "200"
call :test_api POST "/review" "创建评价" "401" "{\"orderId\":1,\"content\":\"好评\",\"rating\":5}"

echo === 10. 搜索模块 (/search) ===
echo.
call :test_api GET "/search?q=test" "综合搜索" "200"
call :test_api GET "/search/product?q=test" "搜索商品" "200"
call :test_api GET "/search/shop?q=test" "搜索店铺" "200"
call :test_api GET "/search/suggestions?q=test" "搜索建议" "200"
call :test_api GET "/search/hot" "热门搜索" "200"
call :test_api GET "/search/history" "搜索历史" "401"
call :test_api GET "/search/filters?q=test" "搜索筛选条件" "200"

echo === 11. 关注/收藏模块 ===
echo.
call :test_api GET "/follow/following" "获取关注列表" "401"
call :test_api GET "/follow/followers" "获取粉丝列表" "401"
call :test_api POST "/follow" "关注" "401" "{\"targetId\":1,\"type\":\"shop\"}"
call :test_api GET "/follow/check?targetId=1&type=shop" "检查是否已关注" "401"
call :test_api GET "/favorite" "获取收藏列表" "401"
call :test_api POST "/favorite" "添加收藏" "401" "{\"productId\":1}"
call :test_api GET "/favorite/count" "获取收藏数" "401"
call :test_api GET "/favorite/check?productId=1" "检查是否已收藏" "401"

echo === 12. 论坛模块 (/forum) ===
echo.
call :test_api GET "/forum/boards" "获取版块列表" "200"
call :test_api GET "/forum/posts" "获取帖子列表" "200"
call :test_api GET "/forum/search?q=test" "搜索帖子" "200"
call :test_api GET "/forum/my-posts" "获取我的帖子" "401"
call :test_api POST "/forum/post" "创建帖子" "401" "{\"boardId\":1,\"title\":\"测试\",\"content\":\"内容\"}"

echo === 13. 通知模块 (/notification) ===
echo.
call :test_api GET "/notification" "获取通知列表" "401"
call :test_api GET "/notification/unread-count" "获取未读数量" "401"
call :test_api GET "/notification/system" "系统通知" "401"
call :test_api PUT "/notification/all-read" "全部标记已读" "401"

echo === 14. 首页模块 (/home) ===
echo.
call :test_api GET "/home/banners" "获取轮播图" "200"
call :test_api GET "/home/categories" "获取分类数据" "200"
call :test_api GET "/home/floor-products" "获取楼层商品" "200"
call :test_api GET "/home/recommended-shops" "推荐店铺" "200"
call :test_api GET "/home/activities" "获取活动信息" "200"
call :test_api GET "/home/flash-sales" "获取秒杀活动" "200"
call :test_api GET "/home/brands" "品牌专区" "200"
call :test_api GET "/home/new-user" "新人专享" "200"
call :test_api GET "/home/recommend" "猜你喜欢" "200"

echo === 15. 抽奖模块 (/lottery) ===
echo.
call :test_api GET "/lottery/activity" "获取抽奖活动" "200"
call :test_api GET "/lottery/prizes" "获取奖品列表" "200"
call :test_api POST "/lottery/draw" "抽奖" "401" "{\"activityId\":1}"
call :test_api GET "/lottery/my-prizes" "获取我的奖品" "401"
call :test_api GET "/lottery/records" "获取中奖记录" "200"

echo === 16. 统计模块 (/statistics) ===
echo.
call :test_api GET "/statistics/overview" "获取统计概览" "401"
call :test_api GET "/statistics/products" "商品统计" "401"
call :test_api GET "/statistics/orders" "订单统计" "401"
call :test_api GET "/statistics/users" "用户统计" "401"

echo === 17. 上传模块 (/upload) ===
echo.
call :test_api POST "/upload/image" "上传图片" "401"
call :test_api POST "/upload/file" "上传文件" "401"
call :test_api POST "/upload/token" "获取上传凭证" "401"

echo === 18. VIP 模块 (/vip) ===
echo.
call :test_api GET "/vip/info" "获取VIP信息" "401"
call :test_api GET "/vip/levels" "获取VIP等级列表" "200"
call :test_api GET "/vip/benefits" "获取VIP特权" "200"
call :test_api POST "/vip/purchase" "购买VIP" "401" "{\"levelId\":1}"
call :test_api GET "/vip/records" "充值记录" "401"

echo === 19. 聊天模块 (/chat) ===
echo.
call :test_api GET "/chat/sessions" "获取会话列表" "401"
call :test_api POST "/chat/session" "创建会话" "401" "{\"targetId\":1}"
call :test_api GET "/chat/unread-count" "获取未读消息数" "401"

echo === 20. 管理端 (/admin) ===
echo.
call :test_api GET "/admin/user/list" "管理-用户列表" "403"
call :test_api GET "/admin/merchant/list" "管理-商家列表" "403"
call :test_api GET "/admin/product/audit/list" "管理-待审核商品" "403"
call :test_api GET "/admin/order/list" "管理-订单列表" "403"
call :test_api GET "/admin/coupon/list" "管理-优惠券列表" "403"

echo === 21. 商家端 (/merchant) ===
echo.
call :test_api GET "/merchant/product" "商家-商品列表" "403"
call :test_api POST "/merchant/product" "商家-创建商品" "403" "{\"name\":\"测试\",\"price\":99.99}"
call :test_api GET "/merchant/order/list" "商家-订单列表" "403"
call :test_api GET "/merchant/coupon/list" "商家-优惠券列表" "403"

:: ========================================
:: 输出结果
:: ========================================

echo.
echo ========================================
echo  测试完成!
echo ========================================
echo  总计: %TOTAL%
echo  通过: %PASS%
echo  失败: %FAIL%
echo  跳过: %SKIP%
echo ========================================
echo.

:: 清理临时文件
del /q test_code.tmp >nul 2>&1
del /q test_resp.tmp >nul 2>&1

pause
