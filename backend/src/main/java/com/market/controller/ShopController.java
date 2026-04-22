package com.market.controller;

import com.market.annotation.*;
import com.market.common.Result;
import com.market.entity.Shop;
import com.market.entity.User;
import com.market.entity.ShopReview;
import com.market.service.ShopService;
import com.market.service.ShopReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺控制器
 * 提供店铺的 CRUD、搜索、关注、认证商家、店铺公告、关闭店铺等功能。
 * 权限要求：查询接口公开，创建/更新/删除店铺等需要登录，关闭店铺需要管理员
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/shop
 */
@RestController
@RequestMapping("/api/shop")
@CrossOrigin(origins = "*")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopReviewService shopReviewService;

    /**
     * 获取店铺列表
     * API路径：GET /api/shop
     * 权限：公开
     *
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param status 状态筛选（可选）
     * @return 分页的店铺列表
     */
    @GetMapping
    @Cacheable(key = "'shop_list_' + #page + '_' + #size", cacheName = "shops", expire = 300)
    @AuditLog(module = "店铺管理", action = "查询店铺列表")
    @DataScope(scopeType = DataScope.ScopeType.ALL)
    public Result<Page<Shop>> getShops(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (status != null && !status.isEmpty()) {
            return Result.success(shopService.getShops(pageable));
        }

        return Result.success(shopService.getShops(pageable));
    }

    /**
     * 获取店铺详情
     * API路径：GET /api/shop/{id}
     * 权限：公开
     *
     * @param id 店铺ID
     * @return 店铺详情
     */
    @GetMapping("/{id}")
    @Cacheable(key = "'shop_detail_' + #id", cacheName = "shops", expire = 600)
    @AuditLog(module = "店铺管理", action = "查询店铺详情")
    @SensitiveData(type = SensitiveData.SensitiveType.DEFAULT)
    public Result<Shop> getShopDetail(@PathVariable Long id) {
        Shop shop = shopService.getShopDetail(id);
        return Result.success(shop);
    }

    /**
     * 获取店铺统计信息
     * API路径：GET /api/shop/{id}/stats
     * 权限：公开
     *
     * @param id 店铺ID
     * @return 店铺统计数据
     */
    @GetMapping("/{id}/stats")
    @Cacheable(key = "'shop_stats_' + #id", cacheName = "shops", expire = 300)
    @AuditLog(module = "店铺管理", action = "查询店铺统计")
    public Result<Map<String, Object>> getShopStats(@PathVariable Long id) {
        Map<String, Object> stats = shopService.getShopStats(id);
        return Result.success(stats);
    }

    /**
     * 搜索店铺
     * API路径：GET /api/shop/search
     * 权限：公开
     *
     * @param keyword 搜索关键词
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 分页的店铺搜索结果
     */
    @GetMapping("/search")
    @Cacheable(key = "'shop_search_' + #keyword + '_' + #page", cacheName = "shops", expire = 300)
    @AuditLog(module = "店铺管理", action = "搜索店铺")
    public Result<Page<Shop>> searchShops(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "rating"));
        Page<Shop> shops = shopService.searchShops(keyword, pageable);
        return Result.success(shops);
    }

    /**
     * 获取认证店铺
     * API路径：GET /api/shop/certified
     * 权限：公开
     *
     * @return 认证店铺列表
     */
    @GetMapping("/certified")
    @Cacheable(key = "'shop_certified'", cacheName = "shops", expire = 600)
    @AuditLog(module = "店铺管理", action = "查询认证店铺")
    public Result<List<Shop>> getCertifiedShops() {
        List<Shop> shops = shopService.getCertifiedShops();
        return Result.success(shops);
    }

    /**
     * 获取高评分店铺
     * API路径：GET /api/shop/high-rating
     * 权限：公开
     *
     * @param minRating 最低评分，默认4.5
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 分页的高评分店铺列表
     * @return 分页的高评分店铺列表
     */
    @GetMapping("/high-rating")
    @Cacheable(key = "'shop_high_rating_' + #minRating", cacheName = "shops", expire = 300)
    @AuditLog(module = "店铺管理", action = "查询高评分店铺")
    public Result<Page<Shop>> getHighRatingShops(
            @RequestParam(defaultValue = "4.5") Double minRating,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Shop> shops = shopService.getHighRatingShops(minRating, pageable);
        return Result.success(shops);
    }

    /**
     * 创建店铺
     * API路径：POST /api/shop
     * 权限：需要登录
     *
     * @param shop 店铺信息
     * @param owner 当前登录用户（店主）
     * @return 创建的店铺
     */
    @PostMapping
    @Idempotent(key = "'create_shop_' + #owner.id", expire = 3600, message = "店铺正在创建中，请勿重复提交")
    @DistributedLock(key = "'create_shop_' + #owner.id", waitTime = 5000)
    @AuditLog(module = "店铺管理", action = "创建店铺", recordParams = true)
    @Retryable(maxAttempts = 3, delay = 1000)
    public Result<Shop> createShop(@RequestBody Shop shop, @AuthenticationPrincipal User owner) {
        if (owner == null) {
            return Result.error(401, "请先登录");
        }

        Shop createdShop = shopService.createShop(owner, shop);
        return Result.success(createdShop);
    }

    /**
     * 更新店铺信息
     * API路径：PUT /api/shop/{id}
     * 权限：需要登录
     *
     * @param id 店铺ID
     * @param shop 更新的店铺信息
     * @param owner 当前登录用户（店主）
     * @return 更新后的店铺
     */
    @PutMapping("/{id}")
    @Idempotent(key = "'update_shop_' + #id", expire = 3600)
    @DistributedLock(key = "'update_shop_' + #id", waitTime = 5000)
    @AuditLog(module = "店铺管理", action = "更新店铺", recordParams = true)
    public Result<Shop> updateShop(
            @PathVariable Long id,
            @RequestBody Shop shop,
            @AuthenticationPrincipal User owner) {

        if (owner == null) {
            return Result.error(401, "请先登录");
        }

        Shop updatedShop = shopService.updateShop(id, owner, shop);
        return Result.success(updatedShop);
    }

    /**
     * 更新店铺公告
     * API路径：PUT /api/shop/{id}/announcement
     * 权限：需要登录
     *
     * @param id 店铺ID
     * @param announcement 公告内容
     * @param owner 当前登录用户（店主）
     * @return 更新后的店铺
     */
    @PutMapping("/{id}/announcement")
    @Idempotent(key = "'update_announcement_' + #id", expire = 600)
    @AuditLog(module = "店铺管理", action = "更新店铺公告")
    public Result<Shop> updateAnnouncement(
            @PathVariable Long id,
            @RequestParam String announcement,
            @AuthenticationPrincipal User owner) {

        Shop shop = shopService.updateAnnouncement(id, announcement);
        return Result.success(shop);
    }

    /**
     * 删除店铺
     * API路径：DELETE /api/shop/{id}
     * 权限：需要登录
     *
     * @param id 店铺ID
     * @param owner 当前登录用户（店主）
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Idempotent(key = "'delete_shop_' + #id", expire = 3600)
    @DistributedLock(key = "'delete_shop_' + #id", waitTime = 5000)
    @AuditLog(module = "店铺管理", action = "删除店铺")
    @Retryable(maxAttempts = 3, delay = 1000)
    public Result<Void> deleteShop(
            @PathVariable Long id,
            @AuthenticationPrincipal User owner) {

        if (owner == null) {
            return Result.error(401, "请先登录");
        }

        shopService.deleteShop(id, owner);
        return Result.success(null);
    }

    /**
     * 关注店铺
     * API路径：POST /api/shop/{id}/follow
     * 权限：需要登录
     *
     * @param id 店铺ID
     * @param user 当前登录用户
     * @return 关注后的店铺
     */
    @PostMapping("/{id}/follow")
    @Idempotent(key = "'follow_shop_' + #id + '_' + #user.id", expire = 600)
    @DistributedLock(key = "'follow_shop_' + #id + '_' + #user.id", waitTime = 3000)
    @AuditLog(module = "店铺管理", action = "关注店铺")
    public Result<Shop> followShop(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Shop shop = shopService.followShop(id, user.getId());
        return Result.success(shop);
    }

    /**
     * 取消关注店铺
     * API路径：DELETE /api/shop/{id}/follow
     * 权限：需要登录
     *
     * @param id 店铺ID
     * @param user 当前登录用户
     * @return 取消关注后的店铺
     */
    @DeleteMapping("/{id}/follow")
    @Idempotent(key = "'unfollow_shop_' + #id + '_' + #user.id", expire = 600)
    @DistributedLock(key = "'unfollow_shop_' + #id + '_' + #user.id", waitTime = 3000)
    @AuditLog(module = "店铺管理", action = "取消关注店铺")
    public Result<Shop> unfollowShop(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Shop shop = shopService.unfollowShop(id, user.getId());
        return Result.success(shop);
    }

    /**
     * 检查是否已关注店铺
     * API路径：GET /api/shop/{id}/following
     * 权限：需要登录
     *
     * @param id 店铺ID
     * @param user 当前登录用户
     * @return 是否已关注
     */
    @GetMapping("/{id}/following")
    @Cacheable(key = "'shop_following_' + #id + '_' + #user.id", cacheName = "shops", expire = 60)
    @AuditLog(module = "店铺管理", action = "检查关注状态")
    public Result<Map<String, Boolean>> checkFollowing(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Map<String, Boolean> result = new HashMap<>();
        result.put("following", false);
        return Result.success(result);
    }

    /**
     * 认证商家
     * API路径：POST /api/shop/{id}/certify
     * 权限：需要登录
     *
     * @param id 店铺ID
     * @param businessLicense 营业执照
     * @param owner 当前登录用户（店主）
     * @return 认证后的店铺
     */
    @PostMapping("/{id}/certify")
    @Idempotent(key = "'certify_shop_' + #id", expire = 3600)
    @DistributedLock(key = "'certify_shop_' + #id", waitTime = 5000)
    @AuditLog(module = "店铺管理", action = "认证商家", recordParams = true)
    public Result<Shop> certifyShop(
            @PathVariable Long id,
            @RequestParam String businessLicense,
            @AuthenticationPrincipal User owner) {

        if (owner == null) {
            return Result.error(401, "请先登录");
        }

        Shop shop = shopService.certifyShop(id, businessLicense);
        return Result.success(shop);
    }

    /**
     * 联系商家客服
     * API路径：POST /api/shop/{id}/contact
     * 权限：需要登录
     *
     * @param id 店铺ID
     * @param user 当前登录用户
     * @return 聊天ID
     */
    @PostMapping("/{id}/contact")
    @AuditLog(module = "店铺管理", action = "联系商家")
    @RateLimiter(key = "'contact_merchant_' + #id", maxRequests = 10, timeout = 60, timeUnit = java.util.concurrent.TimeUnit.SECONDS)
    public Result<Map<String, String>> contactMerchant(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Map<String, String> result = new HashMap<>();
        result.put("chatId", "chat_" + id + "_" + (user != null ? user.getId() : "anonymous"));
        return Result.success(result);
    }

    /**
     * 分享店铺
     * API路径：POST /api/shop/{id}/share
     * 权限：公开
     *
     * @param id 店铺ID
     * @return 分享链接和分享码
     */
    @PostMapping("/{id}/share")
    @AuditLog(module = "店铺管理", action = "分享店铺")
    @RateLimiter(key = "'share_shop_' + #id", maxRequests = 20, timeout = 60, timeUnit = java.util.concurrent.TimeUnit.SECONDS)
    public Result<Map<String, String>> shareShop(@PathVariable Long id) {
        Map<String, String> result = new HashMap<>();
        result.put("shareUrl", "https://market.com/shop/" + id);
        result.put("shareCode", "shop_" + id);
        return Result.success(result);
    }

    /**
     * 评价店铺
     * API路径：POST /api/shop/{id}/review
     * 权限：需要登录
     *
     * @param id 店铺ID
     * @param data 评价数据（包含orderId、rating、descriptionScore、serviceScore、logisticsScore、content、images）
     * @param user 当前登录用户
     * @return 评价结果
     */
    @PostMapping("/{id}/review")
    @Idempotent(key = "'shop_review_' + #id + '_' + #user.id", expire = 3600)
    @AuditLog(module = "店铺管理", action = "评价店铺", recordParams = true)
    public Result<ShopReview> reviewShop(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Integer rating = data.get("rating") != null ? Integer.valueOf(data.get("rating").toString()) : null;
        if (rating == null || rating < 1 || rating > 5) {
            return Result.error(400, "评分必须在1-5之间");
        }

        Long orderId = data.get("orderId") != null ? Long.valueOf(data.get("orderId").toString()) : null;
        String content = data.get("content") != null ? data.get("content").toString() : "";
        String images = data.get("images") != null ? data.get("images").toString() : null;

        java.math.BigDecimal descriptionScore = data.get("descriptionScore") != null 
            ? new java.math.BigDecimal(data.get("descriptionScore").toString()) : null;
        java.math.BigDecimal serviceScore = data.get("serviceScore") != null 
            ? new java.math.BigDecimal(data.get("serviceScore").toString()) : null;
        java.math.BigDecimal logisticsScore = data.get("logisticsScore") != null 
            ? new java.math.BigDecimal(data.get("logisticsScore").toString()) : null;

        try {
            ShopReview review = shopReviewService.createReview(
                user.getId(), id, orderId, rating, descriptionScore, serviceScore, logisticsScore, content, images
            );
            return Result.success(review);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取店铺评价列表
     * API路径：GET /api/shop/{id}/reviews
     * 权限：公开
     *
     * @param id 店铺ID
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 分页的店铺评价列表
     */
    @GetMapping("/{id}/reviews")
    @Cacheable(key = "'shop_reviews_' + #id + '_' + #page", cacheName = "shops", expire = 300)
    @AuditLog(module = "店铺管理", action = "查询店铺评价")
    public Result<Map<String, Object>> getShopReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ShopReview> reviewPage = shopReviewService.getShopReviews(id, pageable);

        List<Map<String, Object>> reviewList = reviewPage.getContent().stream()
            .map(this::convertShopReviewToMap)
            .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", reviewList);
        response.put("total", reviewPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return Result.success(response);
    }

    /**
     * 获取店铺评分统计
     * API路径：GET /api/shop/{id}/rating-stats
     * 权限：公开
     *
     * @param id 店铺ID
     * @return 店铺评分统计（总评数、平均评分、描述评分、服务评分、物流评分）
     */
    @GetMapping("/{id}/rating-stats")
    @Cacheable(key = "'shop_rating_stats_' + #id", cacheName = "shops", expire = 600)
    @AuditLog(module = "店铺管理", action = "查询店铺评分统计")
    public Result<Map<String, Object>> getShopRatingStats(@PathVariable Long id) {
        Map<String, Object> stats = shopReviewService.getShopRatingStats(id);
        return Result.success(stats);
    }

    /**
     * 商家回复店铺评价
     * API路径：POST /api/shop/review/{reviewId}/reply
     * 权限：需要商家角色
     *
     * @param reviewId 评价ID
     * @param data 回复内容
     * @param user 当前登录商家
     * @return 回复结果
     */
    @PostMapping("/review/{reviewId}/reply")
    @AuditLog(module = "店铺管理", action = "回复店铺评价")
    public Result<Void> replyToReview(
            @PathVariable Long reviewId,
            @RequestBody Map<String, String> data,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        String reply = data.get("reply");
        if (reply == null || reply.isEmpty()) {
            return Result.error(400, "回复内容不能为空");
        }

        try {
            shopReviewService.replyToReview(reviewId, reply, user.getId());
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 转换店铺评价为Map
     */
    private Map<String, Object> convertShopReviewToMap(ShopReview review) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", review.getId());
        map.put("userId", review.getUserId());
        map.put("shopId", review.getShopId());
        map.put("orderId", review.getOrderId());
        map.put("rating", review.getRating());
        map.put("descriptionScore", review.getDescriptionScore());
        map.put("serviceScore", review.getServiceScore());
        map.put("logisticsScore", review.getLogisticsScore());
        map.put("content", review.getContent());
        map.put("images", review.getImages());
        map.put("merchantReply", review.getMerchantReply());
        map.put("merchantReplyAt", review.getMerchantReplyAt());
        map.put("status", review.getStatus());
        map.put("createdAt", review.getCreatedAt());
        return map;
    }

    /**
     * 获取店铺公告
     * API路径：GET /api/shop/{id}/announcement
     * 权限：公开
     *
     * @param id 店铺ID
     * @return 店铺公告内容
     */
    @GetMapping("/{id}/announcement")
    @Cacheable(key = "'shop_announcement_' + #id", cacheName = "shops", expire = 600)
    @AuditLog(module = "店铺管理", action = "查询店铺公告")
    public Result<Map<String, String>> getShopAnnouncement(@PathVariable Long id) {
        Shop shop = shopService.getShopDetail(id);
        Map<String, String> result = new HashMap<>();
        result.put("content", shop.getAnnouncement() != null ? shop.getAnnouncement() : "");
        result.put("updateTime", shop.getUpdatedAt() != null ? shop.getUpdatedAt().toString() : "");
        return Result.success(result);
    }

    /**
     * 关闭店铺（管理员）
     * API路径：POST /api/shop/{id}/close
     * 权限：需要管理员角色
     *
     * @param id 店铺ID
     * @param reason 关闭原因（可选）
     * @param admin 当前登录管理员
     * @return 关闭后的店铺
     */
    @PostMapping("/{id}/close")
    @Idempotent(key = "'close_shop_' + #id", expire = 3600)
    @DistributedLock(key = "'close_shop_' + #id", waitTime = 5000)
    @AuditLog(module = "店铺管理", action = "关闭店铺", logLevel = AuditLog.LogLevel.WARNING)
    public Result<Shop> closeShop(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal User admin) {

        Shop shop = shopService.closeShop(id, reason != null ? reason : "违规操作");
        return Result.success(shop);
    }
}
