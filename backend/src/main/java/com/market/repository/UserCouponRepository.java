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

/**
 * 用户优惠券数据访问层
 * 对应实体：UserCoupon
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    /**
     * 根据用户查询优惠券列表
     *
     * @param user 用户对象
     * @return 用户优惠券列表
     */
    List<UserCoupon> findByUser(User user);

    /**
     * 根据用户和状态查询优惠券列表
     *
     * @param user 用户对象
     * @param status 优惠券状态
     * @return 用户优惠券列表
     */
    List<UserCoupon> findByUserAndStatus(User user, String status);

    /**
     * 分页查询用户优惠券
     *
     * @param user 用户对象
     * @param pageable 分页参数
     * @return 用户优惠券分页
     */
    Page<UserCoupon> findByUser(User user, Pageable pageable);

    /**
     * 分页查询用户指定状态的优惠券
     *
     * @param user 用户对象
     * @param status 优惠券状态
     * @param pageable 分页参数
     * @return 用户优惠券分页
     */
    Page<UserCoupon> findByUserAndStatus(User user, String status, Pageable pageable);

    /**
     * 查询用户可用的优惠券（有效期内且未使用）
     *
     * @param userId 用户ID
     * @param now 当前时间
     * @return 可用优惠券列表
     */
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.user.id = :userId AND uc.status = 'UNUSED' " +
           "AND uc.coupon.validFrom <= :now AND uc.coupon.validTo >= :now")
    List<UserCoupon> findAvailableCoupons(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * 查询用户指定商家的可用优惠券
     *
     * @param userId 用户ID
     * @param merchantId 商家ID
     * @param now 当前时间
     * @return 可用优惠券列表
     */
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.user.id = :userId AND uc.status = 'UNUSED' " +
           "AND uc.coupon.validFrom <= :now AND uc.coupon.validTo >= :now " +
           "AND (uc.coupon.merchant.id = :merchantId OR uc.coupon.merchant IS NULL)")
    List<UserCoupon> findAvailableCoupons(@Param("userId") Long userId,
                                          @Param("merchantId") Long merchantId,
                                          @Param("now") LocalDateTime now);

    /**
     * 检查用户是否已领取指定优惠券
     *
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 是否已领取
     */
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    /**
     * 查询用户即将过期的优惠券
     *
     * @param userId 用户ID
     * @param now 当前时间
     * @param expireTime 过期时间阈值
     * @return 即将过期的优惠券列表
     */
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.user.id = :userId AND uc.status = 'UNUSED' " +
           "AND uc.coupon.validTo > :now AND uc.coupon.validTo <= :expireTime")
    List<UserCoupon> findExpiringCoupons(@Param("userId") Long userId,
                                         @Param("now") LocalDateTime now,
                                         @Param("expireTime") LocalDateTime expireTime);

    /**
     * 统计用户优惠券数量
     *
     * @param user 用户对象
     * @return 优惠券数量
     */
    long countByUser(User user);

    /**
     * 统计用户指定状态的优惠券数量
     *
     * @param user 用户对象
     * @param status 优惠券状态
     * @return 优惠券数量
     */
    long countByUserAndStatus(User user, String status);
}
