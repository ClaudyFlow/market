package com.market.controller;

import com.market.annotation.*;
import com.market.common.Result;
import com.market.entity.Coupon;
import com.market.entity.User;
import com.market.entity.UserCoupon;
import com.market.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 优惠券控制器
 */
@RestController
@RequestMapping("/api/coupon")
@CrossOrigin(origins = "*")
public class CouponController {

    @Autowired
    private CouponService couponService;

    // ==================== 用户端优惠券功能 ====================

    /**
     * 获取优惠券列表（用户已领取的）
     */
    @GetMapping
    @Cacheable(key = "'user_coupons_' + #user.id + '_' + #page + '_' + #status", 
               cacheName = "coupons", expire = 300)
    @AuditLog(module = "优惠券管理", action = "查询用户优惠券")
    public Result<Page<UserCoupon>> getCoupons(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<UserCoupon> coupons = couponService.getUserCoupons(user, status, pageable);
        return Result.success(coupons);
    }

    /**
     * 获取优惠券模板列表（可领取的）
     */
    @GetMapping("/templates")
    @Cacheable(key = "'coupon_templates_' + #page + '_' + #merchantId", 
               cacheName = "coupons", expire = 300)
    @AuditLog(module = "优惠券管理", action = "查询优惠券模板")
    public Result<Page<Coupon>> getCouponTemplates(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long merchantId) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // TODO: 查询可领取的优惠券模板
        return Result.success(Page.empty());
    }

    /**
     * 获取优惠券详情
     */
    @GetMapping("/{id}")
    @Cacheable(key = "'user_coupon_detail_' + #id", cacheName = "coupons", expire = 600)
    @AuditLog(module = "优惠券管理", action = "查询优惠券详情")
    public Result<UserCoupon> getCouponDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        // TODO: 查询用户优惠券详情
        return Result.error(404, "优惠券不存在");
    }

    /**
     * 获取优惠券模板详情
     */
    @GetMapping("/templates/{id}")
    @Cacheable(key = "'coupon_template_detail_' + #id", cacheName = "coupons", expire = 600)
    @AuditLog(module = "优惠券管理", action = "查询优惠券模板详情")
    public Result<Coupon> getCouponTemplateDetail(@PathVariable Long id) {
        // TODO: 查询优惠券模板详情
        return Result.error(404, "优惠券模板不存在");
    }

    /**
     * 领取优惠券
     */
    @PostMapping("/receive")
    @Idempotent(key = "'receive_coupon_' + #templateId + '_' + #user.id", 
                expire = 600, message = "正在领取优惠券，请勿重复提交")
    @DistributedLock(key = "'receive_coupon_' + #templateId + '_' + #user.id", waitTime = 3000)
    @AuditLog(module = "优惠券管理", action = "领取优惠券")
    @Retryable(maxAttempts = 3, delay = 500)
    public Result<UserCoupon> receiveCoupon(
            @RequestParam Long templateId,
            @AuthenticationPrincipal User user) {
        
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        UserCoupon userCoupon = couponService.takeCoupon(templateId, user);
        return Result.success(userCoupon);
    }

    /**
     * 批量领取优惠券
     */
    @PostMapping("/batch-receive")
    @Idempotent(key = "'batch_receive_coupon_' + #user.id", expire = 600)
    @DistributedLock(key = "'batch_receive_coupon_' + #user.id", waitTime = 5000)
    @AuditLog(module = "优惠券管理", action = "批量领取优惠券")
    public Result<List<UserCoupon>> batchReceiveCoupon(
            @RequestBody List<Long> templateIds,
            @AuthenticationPrincipal User user) {
        
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        List<UserCoupon> results = new ArrayList<>();
        for (Long templateId : templateIds) {
            try {
                UserCoupon userCoupon = couponService.takeCoupon(templateId, user);
                results.add(userCoupon);
            } catch (Exception e) {
                // 跳过领取失败的
            }
        }
        
        return Result.success(results);
    }

    /**
     * 使用优惠券
     */
    @PostMapping("/{id}/use")
    @Idempotent(key = "'use_coupon_' + #id + '_' + #orderId", expire = 3600)
    @AuditLog(module = "优惠券管理", action = "使用优惠券")
    public Result<Void> useCoupon(
            @PathVariable Long id,
            @RequestParam Long orderId,
            @AuthenticationPrincipal User user) {
        
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        couponService.useCoupon(id, user);
        return Result.success(null);
    }

    /**
     * 退还优惠券
     */
    @PostMapping("/{id}/return")
    @Idempotent(key = "'return_coupon_' + #id", expire = 600)
    @AuditLog(module = "优惠券管理", action = "退还优惠券")
    public Result<Void> returnCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        couponService.returnCoupon(id, user);
        return Result.success(null);
    }

    /**
     * 删除优惠券
     */
    @DeleteMapping("/{id}")
    @Idempotent(key = "'delete_coupon_' + #id", expire = 600)
    @AuditLog(module = "优惠券管理", action = "删除优惠券")
    public Result<Void> deleteCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        // TODO: 删除用户优惠券
        return Result.success(null);
    }

    /**
     * 检查优惠券是否可用
     */
    @GetMapping("/{id}/check")
    @Cacheable(key = "'coupon_check_' + #id + '_' + #amount", cacheName = "coupons", expire = 60)
    @AuditLog(module = "优惠券管理", action = "检查优惠券可用性")
    public Result<Map<String, Object>> checkCouponAvailable(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal amount,
            @AuthenticationPrincipal User user) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("available", true); // TODO: 实际检查
        return Result.success(result);
    }

    /**
     * 获取可用优惠券列表
     */
    @GetMapping("/available")
    @Cacheable(key = "'available_coupons_' + #user.id + '_' + #amount + '_' + #shopId", 
               cacheName = "coupons", expire = 300)
    @AuditLog(module = "优惠券管理", action = "查询可用优惠券")
    public Result<List<com.market.entity.UserCoupon>> getAvailableCoupons(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) Long shopId) {
        
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        List<com.market.entity.UserCoupon> coupons = couponService.getAvailableCoupons(user, shopId);
        return Result.success(coupons);
    }

    /**
     * 获取即将过期优惠券
     */
    @GetMapping("/expiring")
    @Cacheable(key = "'expiring_coupons_' + #user.id + '_' + #days", cacheName = "coupons", expire = 300)
    @AuditLog(module = "优惠券管理", action = "查询即将过期优惠券")
    public Result<List<UserCoupon>> getExpiringCoupons(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "7") Integer days) {
        
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        // TODO: 查询即将过期的优惠券
        return Result.success(new ArrayList<>());
    }

    /**
     * 店铺优惠券列表
     */
    @GetMapping("/shop/{shopId}")
    @Cacheable(key = "'shop_coupons_' + #shopId", cacheName = "coupons", expire = 300)
    @AuditLog(module = "优惠券管理", action = "查询店铺优惠券")
    public Result<List<com.market.entity.Coupon>> getShopCoupons(@PathVariable Long shopId) {
        // TODO: 查询店铺优惠券
        return Result.success(new ArrayList<>());
    }

    /**
     * 商品优惠券列表
     */
    @GetMapping("/product/{productId}")
    @Cacheable(key = "'product_coupons_' + #productId", cacheName = "coupons", expire = 300)
    @AuditLog(module = "优惠券管理", action = "查询商品优惠券")
    public Result<List<com.market.entity.Coupon>> getProductCoupons(@PathVariable Long productId) {
        // TODO: 查询商品优惠券
        return Result.success(new ArrayList<>());
    }

    // ==================== 商家端优惠券管理 ====================

    /**
     * 商家创建优惠券
     */
    @PostMapping("/template")
    @Idempotent(key = "'create_coupon_template_' + #merchant.id", expire = 3600)
    @DistributedLock(key = "'create_coupon_template_' + #merchant.id", waitTime = 5000)
    @AuditLog(module = "优惠券管理", action = "创建优惠券模板", recordParams = true)
    public Result<Coupon> createCouponTemplate(
            @RequestBody Coupon coupon,
            @AuthenticationPrincipal User merchant) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        Coupon createdCoupon = couponService.createCoupon(merchant, coupon);
        return Result.success(createdCoupon);
    }

    /**
     * 商家更新优惠券
     */
    @PutMapping("/template/{id}")
    @Idempotent(key = "'update_coupon_template_' + #id", expire = 3600)
    @DistributedLock(key = "'update_coupon_template_' + #id", waitTime = 5000)
    @AuditLog(module = "优惠券管理", action = "更新优惠券模板", recordParams = true)
    public Result<Coupon> updateCouponTemplate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal User merchant) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        Coupon updatedCoupon = couponService.updateCoupon(id, merchant, updates);
        return Result.success(updatedCoupon);
    }

    /**
     * 商家删除优惠券
     */
    @DeleteMapping("/template/{id}")
    @Idempotent(key = "'delete_coupon_template_' + #id", expire = 3600)
    @DistributedLock(key = "'delete_coupon_template_' + #id", waitTime = 5000)
    @AuditLog(module = "优惠券管理", action = "删除优惠券模板")
    public Result<Void> deleteCouponTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal User merchant) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        couponService.deleteCoupon(id, merchant);
        return Result.success(null);
    }

    /**
     * 商家查询优惠券列表
     */
    @GetMapping("/merchant/list")
    @Cacheable(key = "'merchant_coupons_' + #merchant.id + '_' + #page + '_' + #status", 
               cacheName = "coupons", expire = 300)
    @AuditLog(module = "优惠券管理", action = "查询商家优惠券")
    public Result<Page<Coupon>> getMerchantCoupons(
            @AuthenticationPrincipal User merchant,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Coupon> coupons = couponService.getMerchantCoupons(merchant, status, sortBy, pageable);
        return Result.success(coupons);
    }

    /**
     * 商家查询优惠券详情
     */
    @GetMapping("/merchant/{id}")
    @Cacheable(key = "'merchant_coupon_detail_' + #id", cacheName = "coupons", expire = 600)
    @AuditLog(module = "优惠券管理", action = "查询商家优惠券详情")
    public Result<Coupon> getMerchantCouponDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User merchant) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        Coupon coupon = couponService.getCouponDetail(id, merchant);
        return Result.success(coupon);
    }

    /**
     * 商家统计优惠券
     */
    @GetMapping("/merchant/stats")
    @Cacheable(key = "'merchant_coupon_stats_' + #merchant.id", cacheName = "coupons", expire = 300)
    @AuditLog(module = "优惠券管理", action = "查询商家优惠券统计")
    public Result<Map<String, Object>> getMerchantCouponStats(
            @AuthenticationPrincipal User merchant) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        Map<String, Object> stats = couponService.getMerchantCouponStats(merchant);
        return Result.success(stats);
    }

    /**
     * 上下架优惠券
     */
    @PutMapping("/template/{id}/status")
    @Idempotent(key = "'toggle_coupon_status_' + #id", expire = 600)
    @AuditLog(module = "优惠券管理", action = "上下架优惠券")
    public Result<Coupon> toggleCouponStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal User merchant) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        Coupon coupon = couponService.toggleCouponStatus(id, merchant, status);
        return Result.success(coupon);
    }

    /**
     * 获取即将过期的优惠券
     */
    @GetMapping("/merchant/expiring")
    @Cacheable(key = "'merchant_expiring_coupons_' + #merchant.id + '_' + #days", 
               cacheName = "coupons", expire = 300)
    @AuditLog(module = "优惠券管理", action = "查询即将过期优惠券")
    public Result<List<Coupon>> getMerchantExpiringCoupons(
            @AuthenticationPrincipal User merchant,
            @RequestParam(defaultValue = "7") Integer days) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        List<Coupon> coupons = couponService.getExpiringCoupons(merchant, days);
        return Result.success(coupons);
    }
}
