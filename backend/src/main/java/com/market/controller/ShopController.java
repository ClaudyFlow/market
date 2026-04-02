package com.market.controller;

import com.market.annotation.*;
import com.market.common.Result;
import com.market.entity.Shop;
import com.market.entity.User;
import com.market.service.ShopService;
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
 */
@RestController
@RequestMapping("/api/shop")
@CrossOrigin(origins = "*")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 获取店铺列表
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
     * 检查是否已关注
     */
    @GetMapping("/{id}/following")
    @Cacheable(key = "'shop_following_' + #id + '_' + #user.id", cacheName = "shops", expire = 60)
    @AuditLog(module = "店铺管理", action = "检查关注状态")
    public Result<Map<String, Boolean>> checkFollowing(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        Map<String, Boolean> result = new HashMap<>();
        // 这里应该查询关注表，暂时返回 false
        result.put("following", false);
        return Result.success(result);
    }

    /**
     * 认证商家
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
     * 获取店铺公告
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
