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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 平台端优惠券控制器
 */
@RestController
@RequestMapping("/api/admin/coupon")
@CrossOrigin(origins = "*")
public class AdminCouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 创建平台优惠券
     */
    @PostMapping
    public Result<Coupon> createPlatformCoupon(@RequestBody Coupon coupon) {
        Coupon created = couponService.createPlatformCoupon(coupon);
        return Result.success(created);
    }

    /**
     * 更新平台优惠券
     */
    @PutMapping("/{id}")
    public Result<Coupon> updatePlatformCoupon(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        
        // 平台优惠券没有 merchant，传 null
        Coupon updated = couponService.updateCoupon(id, null, updates);
        return Result.success(updated);
    }

    /**
     * 删除平台优惠券
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePlatformCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id, null);
        return Result.success();
    }

    /**
     * 获取平台优惠券列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getPlatformCouponList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Coupon> couponPage = couponService.getPlatformCoupons(status, sortBy, pageable);
        
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
     * 获取平台优惠券详情
     */
    @GetMapping("/{id}")
    public Result<Coupon> getPlatformCouponDetail(@PathVariable Long id) {
        Coupon coupon = couponService.getCouponDetail(id, null);
        return Result.success(coupon);
    }

    /**
     * 获取平台优惠券统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getPlatformCouponStats() {
        Map<String, Object> stats = couponService.getPlatformCouponStats();
        return Result.success(stats);
    }

    /**
     * 获取所有商家优惠券列表（平台管理）
     */
    @GetMapping("/merchant/list")
    public Result<Map<String, Object>> getMerchantCouponList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        
        Pageable pageable = PageRequest.of(page - 1, size);
        // 这里需要扩展 Service 方法来支持平台查看所有商家优惠券
        Page<Coupon> couponPage = couponService.getPlatformCoupons(status, sortBy, pageable);
        
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
     * 优惠券排行统计
     */
    @GetMapping("/rank")
    public Result<List<Map<String, Object>>> getCouponRank(
            @RequestParam(defaultValue = "usedCount") String type,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        // TODO: 实现优惠券排行查询
        List<Map<String, Object>> rank = new java.util.ArrayList<>();
        return Result.success(rank);
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
        
        if (coupon.getMerchant() != null) {
            map.put("merchantId", coupon.getMerchant().getId());
            map.put("merchantName", coupon.getMerchant().getShopName());
        }
        
        return map;
    }
}
