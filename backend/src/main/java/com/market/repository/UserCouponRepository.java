package com.market.repository;

import com.market.entity.User;
import com.market.entity.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    
    List<UserCoupon> findByUser(User user);
    
    List<UserCoupon> findByUserAndStatus(User user, String status);
    
    Page<UserCoupon> findByUser(User user, Pageable pageable);
    
    Page<UserCoupon> findByUserAndStatus(User user, String status, Pageable pageable);
    
    /**
     * 查询用户可用的优惠券
     */
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.user.id = :userId AND uc.status = 'UNUSED' " +
           "AND uc.coupon.validFrom <= :now AND uc.coupon.validTo >= :now")
    List<UserCoupon> findAvailableCoupons(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    /**
     * 查询用户可用优惠券（指定商家）
     */
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.user.id = :userId AND uc.status = 'UNUSED' " +
           "AND uc.coupon.validFrom <= :now AND uc.coupon.validTo >= :now " +
           "AND (uc.coupon.merchant.id = :merchantId OR uc.coupon.merchant IS NULL)")
    List<UserCoupon> findAvailableCoupons(@Param("userId") Long userId, 
                                          @Param("merchantId") Long merchantId,
                                          @Param("now") LocalDateTime now);
    
    /**
     * 检查用户是否已领取某优惠券
     */
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
    
    /**
     * 查询用户即将过期的优惠券
     */
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.user.id = :userId AND uc.status = 'UNUSED' " +
           "AND uc.coupon.validTo > :now AND uc.coupon.validTo <= :expireTime")
    List<UserCoupon> findExpiringCoupons(@Param("userId") Long userId,
                                         @Param("now") LocalDateTime now,
                                         @Param("expireTime") LocalDateTime expireTime);
    
    /**
     * 统计用户优惠券数量
     */
    long countByUser(User user);
    
    /**
     * 统计用户某种状态的优惠券数量
     */
    long countByUserAndStatus(User user, String status);
}
