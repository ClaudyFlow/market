package com.market.service;

import com.market.entity.Coupon;
import com.market.entity.User;
import com.market.entity.UserCoupon;
import com.market.repository.CouponRepository;
import com.market.repository.UserCouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 优惠券服务类
 */
@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    // ==================== 商家端优惠券管理 ====================

    /**
     * 商家创建优惠券
     */
    @Transactional
    public Coupon createCoupon(User merchant, Coupon coupon) {
        coupon.setMerchant(merchant);
        coupon.setRemainCount(coupon.getTotalCount());
        coupon.updateStatus();
        return couponRepository.save(coupon);
    }

    /**
     * 商家更新优惠券
     */
    @Transactional
    public Coupon updateCoupon(Long couponId, User merchant, Map<String, Object> updates) {
        Coupon coupon = couponRepository.findById(couponId)
            .filter(c -> c.getMerchant().getId().equals(merchant.getId()))
            .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        if (updates.get("name") != null) {
            coupon.setName((String) updates.get("name"));
        }
        if (updates.get("type") != null) {
            coupon.setType((String) updates.get("type"));
        }
        if (updates.get("discountValue") != null) {
            coupon.setDiscountValue(new BigDecimal(updates.get("discountValue").toString()));
        }
        if (updates.get("minPurchase") != null) {
            coupon.setMinPurchase(new BigDecimal(updates.get("minPurchase").toString()));
        }
        if (updates.get("maxDiscount") != null) {
            coupon.setMaxDiscount(new BigDecimal(updates.get("maxDiscount").toString()));
        }
        if (updates.get("validFrom") != null) {
            coupon.setValidFrom((LocalDateTime) updates.get("validFrom"));
        }
        if (updates.get("validTo") != null) {
            coupon.setValidTo((LocalDateTime) updates.get("validTo"));
        }
        if (updates.get("totalCount") != null) {
            coupon.setTotalCount((Integer) updates.get("totalCount"));
            coupon.setRemainCount(coupon.getTotalCount() - coupon.getUsedCount());
        }
        if (updates.get("description") != null) {
            coupon.setDescription((String) updates.get("description"));
        }
        if (updates.get("scope") != null) {
            coupon.setScope((String) updates.get("scope"));
        }
        if (updates.get("categoryIds") != null) {
            coupon.setCategoryIds((String) updates.get("categoryIds"));
        }
        if (updates.get("productIds") != null) {
            coupon.setProductIds((String) updates.get("productIds"));
        }

        coupon.updateStatus();
        return couponRepository.save(coupon);
    }

    /**
     * 商家删除优惠券
     */
    @Transactional
    public void deleteCoupon(Long couponId, User merchant) {
        Coupon coupon = couponRepository.findById(couponId)
            .filter(c -> c.getMerchant().getId().equals(merchant.getId()))
            .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        if (coupon.getUsedCount() > 0) {
            throw new RuntimeException("已使用的优惠券不能删除");
        }

        couponRepository.delete(coupon);
    }

    /**
     * 商家查询优惠券列表
     */
    public Page<Coupon> getMerchantCoupons(User merchant, String status, String sortBy, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            return couponRepository.findByMerchantAndStatus(merchant, status, pageable);
        }

        if ("usedCount".equals(sortBy)) {
            return couponRepository.findByMerchantOrderByUsedCountDesc(merchant, pageable);
        } else if ("remainCount".equals(sortBy)) {
            return couponRepository.findByMerchantOrderByRemainCountAsc(merchant, pageable);
        } else {
            return couponRepository.findByMerchantOrderByCreatedAtDesc(merchant, pageable);
        }
    }

    /**
     * 商家查询优惠券详情
     */
    public Coupon getCouponDetail(Long couponId, User merchant) {
        return couponRepository.findById(couponId)
            .filter(c -> c.getMerchant().getId().equals(merchant.getId()))
            .orElseThrow(() -> new RuntimeException("优惠券不存在"));
    }

    /**
     * 商家统计优惠券
     */
    public Map<String, Object> getCouponStats(User merchant) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", couponRepository.countByMerchant(merchant));
        stats.put("active", couponRepository.countByMerchantAndStatus(merchant, "ACTIVE"));
        stats.put("inactive", couponRepository.countByMerchantAndStatus(merchant, "INACTIVE"));
        stats.put("expired", couponRepository.countByMerchantAndStatus(merchant, "EXPIRED"));
        stats.put("usedUp", couponRepository.countByMerchantAndStatus(merchant, "USED_UP"));

        // 计算总优惠金额和核销率
        List<Coupon> allCoupons = couponRepository.findByMerchant(merchant);
        BigDecimal totalDiscount = allCoupons.stream()
            .map(c -> c.getDiscountValue().multiply(new BigDecimal(c.getUsedCount())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalDiscount", totalDiscount);

        int totalUsed = allCoupons.stream().mapToInt(Coupon::getUsedCount).sum();
        int totalCount = allCoupons.stream().mapToInt(Coupon::getTotalCount).sum();
        double redemptionRate = totalCount > 0 ? (double) totalUsed / totalCount * 100 : 0;
        stats.put("redemptionRate", String.format("%.2f", redemptionRate) + "%");

        return stats;
    }

    /**
     * 获取即将过期的优惠券
     */
    public List<Coupon> getExpiringCoupons(User merchant, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusDays(days);
        return couponRepository.findExpiringCoupons(merchant, now, expireTime);
    }

    /**
     * 获取商家优惠券统计
     */
    public Map<String, Object> getMerchantCouponStats(User merchant) {
        Map<String, Object> stats = new HashMap<>();
        List<Coupon> coupons = couponRepository.findByMerchantId(merchant.getId());

        int total = coupons.size();
        int active = 0;
        int inactive = 0;
        int expired = 0;
        int usedUp = 0;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        LocalDateTime now = LocalDateTime.now();

        for (Coupon coupon : coupons) {
            if ("ACTIVE".equals(coupon.getStatus())) {
                active++;
            } else if ("INACTIVE".equals(coupon.getStatus())) {
                inactive++;
            }
            if (coupon.getValidTo() != null && coupon.getValidTo().isBefore(now)) {
                expired++;
            }
            if (coupon.getRemainCount() == 0) {
                usedUp++;
            }
            // 计算已使用的优惠金额
            if (coupon.getUsedCount() > 0 && coupon.getDiscountValue() != null) {
                totalDiscount = totalDiscount.add(coupon.getDiscountValue().multiply(new BigDecimal(coupon.getUsedCount())));
            }
        }

        stats.put("total", total);
        stats.put("active", active);
        stats.put("inactive", inactive);
        stats.put("expired", expired);
        stats.put("usedUp", usedUp);
        stats.put("totalDiscount", totalDiscount);
        stats.put("redemptionRate", total > 0 ? String.format("%.2f", (total - inactive) * 100.0 / total) : "0.00");

        return stats;
    }

    /**
     * 上下架优惠券
     */
    @Transactional
    public Coupon toggleCouponStatus(Long couponId, User merchant, String status) {
        Coupon coupon = couponRepository.findById(couponId)
            .filter(c -> c.getMerchant().getId().equals(merchant.getId()))
            .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        coupon.setStatus(status);
        return couponRepository.save(coupon);
    }

    // ==================== 用户端优惠券功能 ====================

    /**
     * 用户领取优惠券
     */
    @Transactional
    public UserCoupon takeCoupon(Long couponId, User user) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        if (!coupon.isAvailable()) {
            throw new RuntimeException("优惠券已不可用");
        }

        // 检查用户是否已领取
        boolean alreadyTaken = userCouponRepository.existsByUserIdAndCouponId(user.getId(), couponId);
        if (alreadyTaken) {
            throw new RuntimeException("您已领取过该优惠券");
        }

        // 创建用户优惠券记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setStatus("UNUSED");

        // 更新优惠券数量
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        coupon.setRemainCount(coupon.getRemainCount() - 1);
        coupon.updateStatus();

        couponRepository.save(coupon);
        return userCouponRepository.save(userCoupon);
    }

    /**
     * 用户查询可用优惠券列表
     */
    public List<UserCoupon> getAvailableCoupons(User user, Long merchantId) {
        if (merchantId != null) {
            return userCouponRepository.findAvailableCoupons(user.getId(), merchantId, LocalDateTime.now());
        }
        return userCouponRepository.findAvailableCoupons(user.getId(), LocalDateTime.now());
    }

    /**
     * 用户查询所有优惠券
     */
    public Page<UserCoupon> getUserCoupons(User user, String status, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            return userCouponRepository.findByUserAndStatus(user, status, pageable);
        }
        return userCouponRepository.findByUser(user, pageable);
    }

    /**
     * 查询用户优惠券详情
     */
    public UserCoupon getUserCouponDetail(Long userCouponId, User user) {
        return userCouponRepository.findById(userCouponId)
            .filter(uc -> uc.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("优惠券不存在"));
    }

    /**
     * 使用优惠券
     */
    @Transactional
    public void useCoupon(Long userCouponId, User user) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
            .filter(uc -> uc.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        if (!"UNUSED".equals(userCoupon.getStatus())) {
            throw new RuntimeException("优惠券已使用");
        }

        userCoupon.setStatus("USED");
        userCoupon.setUsedAt(LocalDateTime.now());
        userCouponRepository.save(userCoupon);
    }

    /**
     * 退还优惠券（订单取消时）
     */
    @Transactional
    public void returnCoupon(Long userCouponId, User user) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
            .filter(uc -> uc.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("优惠券不存在"));

        if (!"USED".equals(userCoupon.getStatus())) {
            throw new RuntimeException("优惠券未使用");
        }

        // 检查优惠券是否过期
        Coupon coupon = userCoupon.getCoupon();
        if (!coupon.isAvailable()) {
            userCoupon.setStatus("EXPIRED");
        } else {
            userCoupon.setStatus("UNUSED");
            // 恢复优惠券数量
            coupon.setUsedCount(coupon.getUsedCount() - 1);
            coupon.setRemainCount(coupon.getRemainCount() + 1);
            couponRepository.save(coupon);
        }

        userCouponRepository.save(userCoupon);
    }

    // ==================== 平台端优惠券功能 ====================

    /**
     * 平台创建优惠券（发放给所有用户）
     */
    @Transactional
    public Coupon createPlatformCoupon(Coupon coupon) {
        coupon.setRemainCount(coupon.getTotalCount());
        coupon.updateStatus();
        return couponRepository.save(coupon);
    }

    /**
     * 平台查询所有优惠券
     */
    public Page<Coupon> getPlatformCoupons(String status, String sortBy, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            // 需要自定义查询
            return couponRepository.findAll(pageable);
        }

        if ("usedCount".equals(sortBy)) {
            return couponRepository.findAll(pageable);
        } else {
            return couponRepository.findAll(pageable);
        }
    }

    /**
     * 平台优惠券统计
     */
    public Map<String, Object> getPlatformCouponStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", couponRepository.countByPlatformIsNull());
        stats.put("active", couponRepository.countByPlatformIsNullAndStatus("ACTIVE"));
        stats.put("expired", couponRepository.countByPlatformIsNullAndStatus("EXPIRED"));
        stats.put("usedUp", couponRepository.countByPlatformIsNullAndStatus("USED_UP"));

        List<Coupon> allCoupons = couponRepository.findAll();
        BigDecimal totalDiscount = allCoupons.stream()
            .map(c -> c.getDiscountValue().multiply(new BigDecimal(c.getUsedCount())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalDiscount", totalDiscount);

        return stats;
    }

    // ==================== 订单优惠券计算 ====================

    /**
     * 计算订单可用优惠券
     */
    public List<Coupon> getAvailableCouponsForOrder(User user, Long merchantId, BigDecimal orderAmount, List<Long> productIds, List<String> categories) {
        List<Coupon> availableCoupons = new ArrayList<>();

        // 获取用户的所有可用优惠券
        List<UserCoupon> userCoupons = userCouponRepository.findAvailableCoupons(user.getId(), merchantId, LocalDateTime.now());

        for (UserCoupon userCoupon : userCoupons) {
            Coupon coupon = userCoupon.getCoupon();
            if (coupon.isAvailable() && orderAmount.compareTo(coupon.getMinPurchase()) >= 0) {
                // 检查使用范围
                if ("ALL".equals(coupon.getScope())) {
                    availableCoupons.add(coupon);
                } else if ("CATEGORY".equals(coupon.getScope()) && coupon.getCategoryIds() != null) {
                    // 品类券
                    List<String> couponCategories = Arrays.asList(coupon.getCategoryIds().split(","));
                    if (categories.stream().anyMatch(couponCategories::contains)) {
                        availableCoupons.add(coupon);
                    }
                } else if ("PRODUCT".equals(coupon.getScope()) && coupon.getProductIds() != null) {
                    // 商品券
                    List<Long> couponProductIds = Arrays.stream(coupon.getProductIds().split(","))
                        .map(Long::parseLong).collect(Collectors.toList());
                    if (productIds.stream().anyMatch(couponProductIds::contains)) {
                        availableCoupons.add(coupon);
                    }
                }
            }
        }

        return availableCoupons;
    }

    /**
     * 计算最优优惠券
     */
    public Coupon calculateBestCoupon(List<Coupon> coupons, BigDecimal orderAmount) {
        if (coupons == null || coupons.isEmpty()) {
            return null;
        }

        return coupons.stream()
            .max(Comparator.comparing(c -> c.calculateDiscount(orderAmount)))
            .orElse(null);
    }

    /**
     * 查询可领取的优惠券模板
     */
    public Page<Coupon> getCouponTemplates(Long merchantId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        if (merchantId != null) {
            return couponRepository.findByMerchantIdAndStatusAndValidStartLessThanEqualAndValidEndGreaterThanEqual(
                merchantId, "ACTIVE", now, now, pageable);
        }
        return couponRepository.findByStatusAndValidStartLessThanEqualAndValidEndGreaterThanEqual(
            "ACTIVE", now, now, pageable);
    }

    /**
     * 查询优惠券模板详情
     */
    public Coupon getCouponTemplateDetail(Long couponId) {
        return couponRepository.findById(couponId)
            .orElseThrow(() -> new RuntimeException("优惠券模板不存在"));
    }

    /**
     * 删除用户优惠券
     */
    @Transactional
    public void deleteUserCoupon(Long userCouponId, User user) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
            .filter(uc -> uc.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("优惠券不存在"));
        
        if (!"UNUSED".equals(userCoupon.getStatus())) {
            throw new RuntimeException("只能删除未使用的优惠券");
        }
        
        userCouponRepository.delete(userCoupon);
    }

    /**
     * 检查优惠券是否可用
     */
    public boolean isCouponAvailable(Long userCouponId, User user, BigDecimal orderAmount) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
            .filter(uc -> uc.getUser().getId().equals(user.getId()))
            .orElse(null);
        
        if (userCoupon == null) return false;
        if (!"UNUSED".equals(userCoupon.getStatus())) return false;
        
        Coupon coupon = userCoupon.getCoupon();
        if (coupon.getMinPurchase() != null && orderAmount.compareTo(coupon.getMinPurchase()) < 0) return false;
        
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidTo().isBefore(now)) return false;
        
        return true;
    }

    /**
     * 查询即将过期的优惠券（用户）
     */
    public List<UserCoupon> getExpiringCouponsForUser(User user, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusDays(days);
        List<UserCoupon> userCoupons = userCouponRepository.findByUserAndStatus(user, "UNUSED");
        
        return userCoupons.stream()
            .filter(uc -> {
                LocalDateTime validTo = uc.getCoupon().getValidTo();
                return validTo != null && validTo.isAfter(now) && validTo.isBefore(threshold);
            })
            .toList();
    }

    /**
     * 查询店铺优惠券
     */
    public List<Coupon> getShopCoupons(Long shopId) {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByShopIdAndStatusAndValidStartLessThanEqualAndValidEndGreaterThanEqual(
            shopId, "ACTIVE", now, now);
    }

    /**
     * 查询商品优惠券
     */
    public List<Coupon> getProductCoupons(Long productId) {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByProductIdAndStatusAndValidStartLessThanEqualAndValidEndGreaterThanEqual(
            productId, "ACTIVE", now, now);
    }
}
