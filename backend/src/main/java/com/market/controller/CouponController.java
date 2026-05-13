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
 * 提供用户端优惠券领取、使用、查询功能，以及商家端优惠券模板的 CRUD 管理功能。
 * 权限要求：用户端需要登录，商家端需要商家角色登录
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/coupon
 */
@RestController
@RequestMapping("/api/coupon")
@CrossOrigin(origins = "*")
public class CouponController {

    @Autowired
    private CouponService couponService;

    // ==================== 用户端优惠券功能 ====================

    /**
     * 获取用户已领取的优惠券列表
     * API路径：GET /api/coupon
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param status 优惠券状态（可选）
     * @return 分页的用户优惠券列表
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
     * 获取可领取的优惠券模板列表
     * API路径：GET /api/coupon/templates
     * 权限：公开
     *
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param merchantId 商家ID（可选，用于筛选）
     * @return 分页的优惠券模板列表
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
        Page<Coupon> templates = couponService.getCouponTemplates(merchantId, pageable);
        return Result.success(templates);
    }

    /**
     * 获取用户优惠券详情
     * API路径：GET /api/coupon/{id}
     * 权限：需要登录
     *
     * @param id 用户优惠券ID
     * @param user 当前登录用户
     * @return 优惠券详情
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

        try {
            UserCoupon coupon = couponService.getUserCouponDetail(id, user);
            return Result.success(coupon);
        } catch (RuntimeException e) {
            return Result.error(404, e.getMessage());
        }
    }

    /**
     * 获取优惠券模板详情
     * API路径：GET /api/coupon/templates/{id}
     * 权限：公开
     *
     * @param id 优惠券模板ID
     * @return 优惠券模板详情
     */
    @GetMapping("/templates/{id}")
    @Cacheable(key = "'coupon_template_detail_' + #id", cacheName = "coupons", expire = 600)
    @AuditLog(module = "优惠券管理", action = "查询优惠券模板详情")
    public Result<Coupon> getCouponTemplateDetail(@PathVariable Long id) {
        try {
            Coupon coupon = couponService.getCouponTemplateDetail(id);
            return Result.success(coupon);
        } catch (RuntimeException e) {
            return Result.error(404, e.getMessage());
        }
    }

    /**
     * 领取优惠券
     * API路径：POST /api/coupon/receive
     * 权限：需要登录
     *
     * @param templateId 优惠券模板ID
     * @param user 当前登录用户
     * @return 领取后的用户优惠券
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
     * API路径：POST /api/coupon/batch-receive
     * 权限：需要登录
     *
     * @param templateIds 优惠券模板ID列表
     * @param user 当前登录用户
     * @return 成功领取的优惠券列表
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
     * API路径：POST /api/coupon/{id}/use
     * 权限：需要登录
     *
     * @param id 用户优惠券ID
     * @param orderId 订单ID
     * @param user 当前登录用户
     * @return 操作结果
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
     * API路径：POST /api/coupon/{id}/return
     * 权限：需要登录
     *
     * @param id 用户优惠券ID
     * @param user 当前登录用户
     * @return 操作结果
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
     * 删除用户优惠券
     * API路径：DELETE /api/coupon/{id}
     * 权限：需要登录
     *
     * @param id 用户优惠券ID
     * @param user 当前登录用户
     * @return 操作结果
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

        try {
            couponService.deleteUserCoupon(id, user);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 检查优惠券是否可用
     * API路径：GET /api/coupon/{id}/check
     * 权限：需要登录
     *
     * @param id 用户优惠券ID
     * @param amount 订单金额（可选）
     * @param user 当前登录用户
     * @return 优惠券是否可用
     */
    @GetMapping("/{id}/check")
    @Cacheable(key = "'coupon_check_' + #id + '_' + #amount", cacheName = "coupons", expire = 60)
    @AuditLog(module = "优惠券管理", action = "检查优惠券可用性")
    public Result<Map<String, Object>> checkCouponAvailable(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal amount,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        boolean available = couponService.isCouponAvailable(id, user, amount != null ? amount : BigDecimal.ZERO);
        Map<String, Object> result = new HashMap<>();
        result.put("available", available);
        return Result.success(result);
    }

    /**
     * 获取可用优惠券列表
     * API路径：GET /api/coupon/available
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param amount 订单金额（可选）
     * @param shopId 店铺ID（可选）
     * @return 可用优惠券列表
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
     * API路径：GET /api/coupon/expiring
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param days 天数范围，默认7天
     * @return 即将过期的优惠券列表
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

        List<UserCoupon> coupons = couponService.getExpiringCouponsForUser(user, days);
        return Result.success(coupons);
    }

    /**
     * 获取店铺优惠券列表
     * API路径：GET /api/coupon/shop/{shopId}
     * 权限：公开
     *
     * @param shopId 店铺ID
     * @return 店铺优惠券列表
     */
    @GetMapping("/shop/{shopId}")
    @Cacheable(key = "'shop_coupons_' + #shopId", cacheName = "coupons", expire = 300)
    @AuditLog(module = "优惠券管理", action = "查询店铺优惠券")
    public Result<List<Coupon>> getShopCoupons(@PathVariable Long shopId) {
        List<Coupon> coupons = couponService.getShopCoupons(shopId);
        return Result.success(coupons);
    }

    /**
     * 获取商品优惠券列表
     * API路径：GET /api/coupon/product/{productId}
     * 权限：公开
     *
     * @param productId 商品ID
     * @return 商品优惠券列表
     */
    @GetMapping("/product/{productId}")
    @Cacheable(key = "'product_coupons_' + #productId", cacheName = "coupons", expire = 300)
    @AuditLog(module = "优惠券管理", action = "查询商品优惠券")
    public Result<List<Coupon>> getProductCoupons(@PathVariable Long productId) {
        List<Coupon> coupons = couponService.getProductCoupons(productId);
        return Result.success(coupons);
    }

    // ==================== 商家端优惠券管理 ====================

    /**
     * 商家创建优惠券模板
     * API路径：POST /api/coupon/template
     * 权限：需要商家登录
     *
     * @param coupon 优惠券模板信息
     * @param merchant 当前登录商家
     * @return 创建的优惠券模板
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
     * 商家更新优惠券模板
     * API路径：PUT /api/coupon/template/{id}
     * 权限：需要商家登录
     *
     * @param id 优惠券模板ID
     * @param updates 更新字段映射
     * @param merchant 当前登录商家
     * @return 更新后的优惠券模板
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
     * 商家删除优惠券模板
     * API路径：DELETE /api/coupon/template/{id}
     * 权限：需要商家登录
     *
     * @param id 优惠券模板ID
     * @param merchant 当前登录商家
     * @return 操作结果
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
     * 商家查询自己的优惠券列表
     * API路径：GET /api/coupon/merchant/list
     * 权限：需要商家登录
     *
     * @param merchant 当前登录商家
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param status 状态筛选（可选）
     * @param sortBy 排序字段，默认createdAt
     * @return 分页的商家优惠券列表
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
     * 商家查询优惠券模板详情
     * API路径：GET /api/coupon/merchant/{id}
     * 权限：需要商家登录
     *
     * @param id 优惠券模板ID
     * @param merchant 当前登录商家
     * @return 优惠券模板详情
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
     * 商家优惠券统计
     * API路径：GET /api/coupon/merchant/stats
     * 权限：需要商家登录
     *
     * @param merchant 当前登录商家
     * @return 优惠券统计数据
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
     * 商家上下架优惠券模板
     * API路径：PUT /api/coupon/template/{id}/status
     * 权限：需要商家登录
     *
     * @param id 优惠券模板ID
     * @param status 新状态
     * @param merchant 当前登录商家
     * @return 更新后的优惠券模板
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
     * 商家获取即将过期的优惠券模板
     * API路径：GET /api/coupon/merchant/expiring
     * 权限：需要商家登录
     *
     * @param merchant 当前登录商家
     * @param days 天数范围，默认7天
     * @return 即将过期的优惠券模板列表
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
