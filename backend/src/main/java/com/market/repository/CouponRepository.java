package com.market.repository;

import com.market.entity.Coupon;
import com.market.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券数据访问层
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    /**
     * 分页查询商家创建的优惠券
     */
    Page<Coupon> findByMerchant(User merchant, Pageable pageable);

    /**
     * 分页查询平台创建的优惠券（全局优惠券）
     */
    Page<Coupon> findByPlatformIsNull(Pageable pageable);

    /**
     * 查询商家的所有优惠券（不分页）
     */
    List<Coupon> findByMerchant(User merchant);

    /**
     * 查询有效的优惠券列表
     */
    @Query("SELECT c FROM Coupon c WHERE c.merchant = :merchant AND c.status = 'ACTIVE' " +
           "AND c.remainCount > 0 AND c.validFrom <= :now AND c.validTo >= :now")
    List<Coupon> findAvailableCoupons(@Param("merchant") User merchant, @Param("now") LocalDateTime now);

    /**
     * 查询平台发放的有效优惠券
     */
    @Query("SELECT c FROM Coupon c WHERE c.platform IS NULL AND c.merchant IS NULL " +
           "AND c.status = 'ACTIVE' AND c.remainCount > 0 AND c.validFrom <= :now AND c.validTo >= :now")
    List<Coupon> findPlatformAvailableCoupons(@Param("now") LocalDateTime now);

    /**
     * 统计商家优惠券数量
     */
    long countByMerchant(User merchant);

    /**
     * 统计商家某种状态的优惠券数量
     */
    long countByMerchantAndStatus(User merchant, String status);

    /**
     * 查询即将过期的优惠券
     */
    @Query("SELECT c FROM Coupon c WHERE c.merchant = :merchant AND c.status = 'ACTIVE' " +
           "AND c.validTo > :now AND c.validTo <= :expireTime")
    List<Coupon> findExpiringCoupons(@Param("merchant") User merchant, 
                                     @Param("now") LocalDateTime now,
                                     @Param("expireTime") LocalDateTime expireTime);

    /**
     * 按使用量排序查询优惠券
     */
    @Query("SELECT c FROM Coupon c WHERE c.merchant = :merchant ORDER BY c.usedCount DESC")
    Page<Coupon> findByMerchantOrderByUsedCountDesc(User merchant, Pageable pageable);

    /**
     * 按创建时间排序查询优惠券
     */
    Page<Coupon> findByMerchantOrderByCreatedAtDesc(User merchant, Pageable pageable);

    /**
     * 按剩余数量排序查询优惠券
     */
    @Query("SELECT c FROM Coupon c WHERE c.merchant = :merchant ORDER BY c.remainCount ASC")
    Page<Coupon> findByMerchantOrderByRemainCountAsc(User merchant, Pageable pageable);

    /**
     * 查询优惠券（带状态过滤）
     */
    Page<Coupon> findByMerchantAndStatus(User merchant, String status, Pageable pageable);

    /**
     * 模糊查询优惠券名称
     */
    Page<Coupon> findByMerchantAndNameContaining(User merchant, String keyword, Pageable pageable);

    /**
     * 统计平台优惠券总数
     */
    long countByPlatformIsNull();

    /**
     * 统计平台优惠券各状态数量
     */
    long countByPlatformIsNullAndStatus(String status);
}
