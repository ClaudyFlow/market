package com.market.controller;

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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商家端优惠券控制器
 */
@RestController
@RequestMapping("/api/merchant/coupon")
@CrossOrigin(origins = "*")
public class MerchantCouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 创建优惠券
     */
    @PostMapping
    public Result<Coupon> createCoupon(
            @AuthenticationPrincipal User merchant,
            @RequestBody Coupon coupon) {
        
        Coupon created = couponService.createCoupon(merchant, coupon);
        return Result.success(created);
    }

    /**
     * 更新优惠券
     */
    @PutMapping("/{id}")
    public Result<Coupon> updateCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal User merchant,
            @RequestBody Map<String, Object> updates) {
        
        Coupon updated = couponService.updateCoupon(id, merchant, updates);
        return Result.success(updated);
    }

    /**
     * 删除优惠券
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal User merchant) {
        
        couponService.deleteCoupon(id, merchant);
        return Result.success();
    }

    /**
     * 获取优惠券列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getCouponList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @AuthenticationPrincipal User merchant) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Coupon> couponPage = couponService.getMerchantCoupons(merchant, status, sortBy, pageable);
        
        List<Map<String, Object>> couponList = couponPage.getContent().stream()
            .map(this::convertCouponToMap)
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("list", couponList);
        response.put("total", couponPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        
        return Result.success(response);
    }

    /**
     * 获取优惠券详情
     */
    @GetMapping("/{id}")
    public Result<Coupon> getCouponDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User merchant) {
        
        Coupon coupon = couponService.getCouponDetail(id, merchant);
        return Result.success(coupon);
    }

    /**
     * 上下架优惠券
     */
    @PutMapping("/{id}/status")
    public Result<Coupon> toggleCouponStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal User merchant) {

        Coupon coupon = couponService.toggleCouponStatus(id, merchant, status);
        return Result.success(coupon);
    }

    /**
     * 获取优惠券统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCouponStats(@AuthenticationPrincipal User merchant) {
        Map<String, Object> stats = couponService.getMerchantCouponStats(merchant);
        return Result.success(stats);
    }

    /**
     * 获取即将过期的优惠券
     */
    @GetMapping("/expiring")
    public Result<List<Coupon>> getExpiringCoupons(
            @RequestParam(defaultValue = "7") Integer days,
            @AuthenticationPrincipal User merchant) {

        List<Coupon> coupons = couponService.getExpiringCoupons(merchant, days);
        return Result.success(coupons);
    }

    /**
     * 转换 Coupon 对象为 Map
     */
    private Map<String, Object> convertCouponToMap(Coupon coupon) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", coupon.getId());
        map.put("name", coupon.getName());
        map.put("type", coupon.getType());
        map.put("discountValue", coupon.getDiscountValue());
        map.put("minPurchase", coupon.getMinPurchase());
        map.put("maxDiscount", coupon.getMaxDiscount());
        map.put("validFrom", coupon.getValidFrom());
        map.put("validTo", coupon.getValidTo());
        map.put("totalCount", coupon.getTotalCount());
        map.put("usedCount", coupon.getUsedCount());
        map.put("remainCount", coupon.getRemainCount());
        map.put("status", coupon.getStatus());
        map.put("description", coupon.getDescription());
        map.put("scope", coupon.getScope());
        map.put("categoryIds", coupon.getCategoryIds());
        map.put("productIds", coupon.getProductIds());
        map.put("createdAt", coupon.getCreatedAt());
        map.put("updatedAt", coupon.getUpdatedAt());

        // 计算优惠力度描述
        if ("PERCENT".equals(coupon.getType())) {
            map.put("description", coupon.getDiscountValue() + "折" +
                (coupon.getMaxDiscount() != null ? " 最高减" + coupon.getMaxDiscount() : ""));
        } else {
            map.put("description", "满" + coupon.getMinPurchase() + "减" + coupon.getDiscountValue());
        }

        return map;
    }
}
