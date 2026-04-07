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
 * 对应实体：Coupon
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    /**
     * 分页查询商家创建的优惠券
     *
     * @param merchant 商家对象
     * @param pageable 分页参数
     * @return 优惠券分页
     */
    Page<Coupon> findByMerchant(User merchant, Pageable pageable);

    /**
     * 分页查询平台创建的优惠券（全局优惠券）
     *
     * @param pageable 分页参数
     * @return 优惠券分页
     */
    Page<Coupon> findByPlatformIsNull(Pageable pageable);

    /**
     * 查询商家的所有优惠券（不分页）
     *
     * @param merchant 商家对象
     * @return 优惠券列表
     */
    List<Coupon> findByMerchant(User merchant);

    /**
     * 查询商家的所有优惠券（通过商家ID）
     *
     * @param merchantId 商家ID
     * @return 优惠券列表
     */
    List<Coupon> findByMerchantId(Long merchantId);

    /**
     * 查询有效的优惠券列表（商家在有效期内且有余量）
     *
     * @param merchant 商家对象
     * @param now 当前时间
     * @return 有效优惠券列表
     */
    @Query("SELECT c FROM Coupon c WHERE c.merchant = :merchant AND c.status = 'ACTIVE' " +
           "AND c.remainCount > 0 AND c.validFrom <= :now AND c.validTo >= :now")
    List<Coupon> findAvailableCoupons(@Param("merchant") User merchant, @Param("now") LocalDateTime now);

    /**
     * 查询平台发放的有效优惠券
     *
     * @param now 当前时间
     * @return 有效平台优惠券列表
     */
    @Query("SELECT c FROM Coupon c WHERE c.platform IS NULL AND c.merchant IS NULL " +
           "AND c.status = 'ACTIVE' AND c.remainCount > 0 AND c.validFrom <= :now AND c.validTo >= :now")
    List<Coupon> findPlatformAvailableCoupons(@Param("now") LocalDateTime now);

    /**
     * 统计商家优惠券数量
     *
     * @param merchant 商家对象
     * @return 优惠券数量
     */
    long countByMerchant(User merchant);

    /**
     * 统计商家指定状态的优惠券数量
     *
     * @param merchant 商家对象
     * @param status 优惠券状态
     * @return 优惠券数量
     */
    long countByMerchantAndStatus(User merchant, String status);

    /**
     * 查询即将过期的优惠券
     *
     * @param merchant 商家对象
     * @param now 当前时间
     * @param expireTime 过期时间阈值
     * @return 即将过期的优惠券列表
     */
    @Query("SELECT c FROM Coupon c WHERE c.merchant = :merchant AND c.status = 'ACTIVE' " +
           "AND c.validTo > :now AND c.validTo <= :expireTime")
    List<Coupon> findExpiringCoupons(@Param("merchant") User merchant,
                                     @Param("now") LocalDateTime now,
                                     @Param("expireTime") LocalDateTime expireTime);

    /**
     * 按使用量降序查询商家优惠券
     *
     * @param merchant 商家对象
     * @param pageable 分页参数
     * @return 优惠券分页
     */
    @Query("SELECT c FROM Coupon c WHERE c.merchant = :merchant ORDER BY c.usedCount DESC")
    Page<Coupon> findByMerchantOrderByUsedCountDesc(User merchant, Pageable pageable);

    /**
     * 按创建时间降序查询商家优惠券
     *
     * @param merchant 商家对象
     * @param pageable 分页参数
     * @return 优惠券分页
     */
    Page<Coupon> findByMerchantOrderByCreatedAtDesc(User merchant, Pageable pageable);

    /**
     * 按剩余数量升序查询商家优惠券
     *
     * @param merchant 商家对象
     * @param pageable 分页参数
     * @return 优惠券分页
     */
    @Query("SELECT c FROM Coupon c WHERE c.merchant = :merchant ORDER BY c.remainCount ASC")
    Page<Coupon> findByMerchantOrderByRemainCountAsc(User merchant, Pageable pageable);

    /**
     * 查询商家指定状态的优惠券
     *
     * @param merchant 商家对象
     * @param status 优惠券状态
     * @param pageable 分页参数
     * @return 优惠券分页
     */
    Page<Coupon> findByMerchantAndStatus(User merchant, String status, Pageable pageable);

    /**
     * 模糊查询商家优惠券名称
     *
     * @param merchant 商家对象
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 优惠券分页
     */
    Page<Coupon> findByMerchantAndNameContaining(User merchant, String keyword, Pageable pageable);

    /**
     * 统计平台优惠券总数
     *
     * @return 平台优惠券总数
     */
    long countByPlatformIsNull();

    /**
     * 统计平台指定状态的优惠券数量
     *
     * @param status 优惠券状态
     * @return 优惠券数量
     */
    long countByPlatformIsNullAndStatus(String status);

    /**
     * 查询可领取的商家优惠券模板
     *
     * @param merchantId 商家ID
     * @param status 优惠券状态
     * @param now 当前时间（有效期开始）
     * @param now2 当前时间（有效期结束）
     * @param pageable 分页参数
     * @return 优惠券分页
     */
    Page<Coupon> findByMerchantIdAndStatusAndValidFromLessThanEqualAndValidToGreaterThanEqual(
        Long merchantId, String status, LocalDateTime now, LocalDateTime now2, Pageable pageable);

    /**
     * 查询可领取的平台优惠券模板
     *
     * @param status 优惠券状态
     * @param now 当前时间（有效期开始）
     * @param now2 当前时间（有效期结束）
     * @param pageable 分页参数
     * @return 优惠券分页
     */
    Page<Coupon> findByStatusAndValidFromLessThanEqualAndValidToGreaterThanEqual(
        String status, LocalDateTime now, LocalDateTime now2, Pageable pageable);

    /**
     * 查询店铺优惠券（通过自定义SQL查询）
     * 店铺的优惠券即店铺所有者（商家）创建的优惠券
     *
     * @param shopId 店铺ID
     * @param status 优惠券状态
     * @param now 当前时间（有效期开始）
     * @param now2 当前时间（有效期结束）
     * @return 优惠券列表
     */
    @Query("SELECT c FROM Coupon c WHERE c.merchant.id = " +
           "(SELECT s.owner.id FROM Shop s WHERE s.id = :shopId) " +
           "AND c.status = :status AND c.validFrom <= :now AND c.validTo >= :now2")
    List<Coupon> findByShopIdAndStatusAndValidFromLessThanEqualAndValidToGreaterThanEqual(
        @Param("shopId") Long shopId, @Param("status") String status, 
        @Param("now") LocalDateTime now, @Param("now2") LocalDateTime now2);

    /**
     * 查询商品优惠券（通过自定义SQL查询匹配productIds字段）
     *
     * @param productId 商品ID
     * @param status 优惠券状态
     * @param now 当前时间（有效期开始）
     * @param now2 当前时间（有效期结束）
     * @return 优惠券列表
     */
    @Query("SELECT c FROM Coupon c WHERE c.status = :status " +
           "AND c.validFrom <= :now AND c.validTo >= :now2 " +
           "AND (c.productIds LIKE CONCAT('%', :productId, '%') " +
           "OR c.scope = 'ALL')")
    List<Coupon> findByProductIdAndStatusAndValidFromLessThanEqualAndValidToGreaterThanEqual(
        @Param("productId") Long productId, @Param("status") String status,
        @Param("now") LocalDateTime now, @Param("now2") LocalDateTime now2);
}
