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
 * 用户端优惠券控制器
 */
@RestController
@RequestMapping("/api/user/coupon")
@CrossOrigin(origins = "*")
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 领取优惠券
     */
    @PostMapping("/take/{id}")
    public Result<UserCoupon> takeCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        UserCoupon userCoupon = couponService.takeCoupon(id, user);
        return Result.success(userCoupon);
    }

    /**
     * 获取我的优惠券列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getMyCoupons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal User user) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "obtainedAt"));
        Page<UserCoupon> couponPage = couponService.getUserCoupons(user, status, pageable);
        
        List<Map<String, Object>> couponList = couponPage.getContent().stream()
            .map(this::convertUserCouponToMap)
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("list", couponList);
        response.put("total", couponPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        
        return Result.success(response);
    }

    /**
     * 获取可用优惠券（下单时选择）
     */
    @GetMapping("/available")
    public Result<List<Map<String, Object>>> getAvailableCoupons(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) BigDecimal orderAmount,
            @AuthenticationPrincipal User user) {
        
        List<UserCoupon> userCoupons = couponService.getAvailableCoupons(user, merchantId);
        
        List<Map<String, Object>> couponList = userCoupons.stream()
            .map(this::convertUserCouponToMap)
            .collect(Collectors.toList());
        
        return Result.success(couponList);
    }

    /**
     * 获取订单可用优惠券（智能推荐）
     */
    @GetMapping("/order/available")
    public Result<List<Coupon>> getOrderAvailableCoupons(
            @RequestParam Long merchantId,
            @RequestParam BigDecimal orderAmount,
            @RequestParam(required = false) List<Long> productIds,
            @RequestParam(required = false) List<String> categories,
            @AuthenticationPrincipal User user) {
        
        List<Coupon> availableCoupons = couponService.getAvailableCouponsForOrder(
            user, merchantId, orderAmount, productIds, categories);
        
        return Result.success(availableCoupons);
    }

    /**
     * 获取最优优惠券
     */
    @GetMapping("/best")
    public Result<Coupon> getBestCoupon(
            @RequestParam Long merchantId,
            @RequestParam BigDecimal orderAmount,
            @RequestParam(required = false) List<Long> productIds,
            @RequestParam(required = false) List<String> categories,
            @AuthenticationPrincipal User user) {
        
        List<Coupon> availableCoupons = couponService.getAvailableCouponsForOrder(
            user, merchantId, orderAmount, productIds, categories);
        
        Coupon bestCoupon = couponService.calculateBestCoupon(availableCoupons, orderAmount);
        
        return Result.success(bestCoupon);
    }

    /**
     * 使用优惠券
     */
    @PostMapping("/{id}/use")
    public Result<Void> useCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        couponService.useCoupon(id, user);
        return Result.success();
    }

    /**
     * 退还优惠券（订单取消时）
     */
    @PostMapping("/{id}/return")
    public Result<Void> returnCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        couponService.returnCoupon(id, user);
        return Result.success();
    }

    /**
     * 获取优惠券统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCouponStats(@AuthenticationPrincipal User user) {
        Map<String, Object> stats = new HashMap<>();
        
        List<UserCoupon> allCoupons = couponService.getUserCoupons(user, null, 
            PageRequest.of(0, 1000)).getContent();
        
        stats.put("total", allCoupons.size());
        stats.put("unused", allCoupons.stream().filter(c -> "UNUSED".equals(c.getStatus())).count());
        stats.put("used", allCoupons.stream().filter(c -> "USED".equals(c.getStatus())).count());
        stats.put("expired", allCoupons.stream().filter(c -> "EXPIRED".equals(c.getStatus())).count());
        
        // 计算已节省金额
        BigDecimal savedAmount = allCoupons.stream()
            .filter(c -> "USED".equals(c.getStatus()))
            .map(uc -> uc.getCoupon().calculateDiscount(BigDecimal.valueOf(1000)))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("savedAmount", savedAmount);
        
        return Result.success(stats);
    }

    /**
     * 转换 UserCoupon 对象为 Map
     */
    private Map<String, Object> convertUserCouponToMap(UserCoupon userCoupon) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", userCoupon.getId());
        map.put("userCouponId", userCoupon.getId());
        map.put("couponId", userCoupon.getCoupon().getId());
        map.put("name", userCoupon.getCoupon().getName());
        map.put("type", userCoupon.getCoupon().getType());
        map.put("discountValue", userCoupon.getCoupon().getDiscountValue());
        map.put("minPurchase", userCoupon.getCoupon().getMinPurchase());
        map.put("maxDiscount", userCoupon.getCoupon().getMaxDiscount());
        map.put("validFrom", userCoupon.getCoupon().getValidFrom());
        map.put("validTo", userCoupon.getCoupon().getValidTo());
        map.put("status", userCoupon.getStatus());
        map.put("description", userCoupon.getCoupon().getDescription());
        map.put("scope", userCoupon.getCoupon().getScope());
        map.put("obtainedAt", userCoupon.getObtainedAt());
        map.put("usedAt", userCoupon.getUsedAt());
        
        // 计算优惠描述
        Coupon coupon = userCoupon.getCoupon();
        if ("PERCENT".equals(coupon.getType())) {
            map.put("label", coupon.getDiscountValue() + "折" + 
                (coupon.getMaxDiscount() != null ? " 最高减¥" + coupon.getMaxDiscount() : ""));
        } else {
            map.put("label", "满¥" + coupon.getMinPurchase() + "减¥" + coupon.getDiscountValue());
        }
        
        // 商户信息
        if (coupon.getMerchant() != null) {
            map.put("merchantId", coupon.getMerchant().getId());
            map.put("merchantName", coupon.getMerchant().getShopName());
        }
        
        return map;
    }
}
