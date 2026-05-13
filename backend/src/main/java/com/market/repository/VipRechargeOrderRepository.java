package com.market.repository;

import com.market.entity.VipRechargeOrder;
import com.market.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * VIP充值订单数据访问层
 * 对应实体：VipRechargeOrder
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface VipRechargeOrderRepository extends JpaRepository<VipRechargeOrder, Long> {

    /**
     * 根据订单号查询充值订单
     *
     * @param orderNo 订单号
     * @return 充值订单对象，不存在返回empty
     */
    Optional<VipRechargeOrder> findByOrderNo(String orderNo);

    /**
     * 根据用户查询充值订单列表
     *
     * @param user 用户对象
     * @return 充值订单列表
     */
    List<VipRechargeOrder> findByUser(User user);

    /**
     * 分页查询用户充值订单
     *
     * @param user 用户对象
     * @param pageable 分页参数
     * @return 充值订单分页
     */
    Page<VipRechargeOrder> findByUser(User user, Pageable pageable);

    /**
     * 查询用户待支付且未过期的订单
     *
     * @param user 用户对象
     * @param expireTime 过期时间
     * @return 待支付订单列表
     */
    @Query("SELECT o FROM VipRechargeOrder o WHERE o.user = :user AND o.status = 'PENDING' " +
           "AND o.createdAt >= :expireTime")
    List<VipRechargeOrder> findPendingOrders(@Param("user") User user,
                                             @Param("expireTime") LocalDateTime expireTime);

    /**
     * 统计用户已支付充值总额
     *
     * @param user 用户对象
     * @return 充值总额
     */
    @Query("SELECT SUM(o.amount) FROM VipRechargeOrder o WHERE o.user = :user AND o.status = 'PAID'")
    java.math.BigDecimal getTotalRechargeAmount(@Param("user") User user);

    /**
     * 统计用户已获得成长值
     *
     * @param user 用户对象
     * @return 成长值总额
     */
    @Query("SELECT SUM(o.growthValue) FROM VipRechargeOrder o WHERE o.user = :user AND o.status = 'PAID'")
    Integer getTotalGrowthValue(@Param("user") User user);

    /**
     * 根据用户和状态查询充值订单
     *
     * @param user 用户对象
     * @param status 订单状态
     * @return 充值订单列表
     */
    List<VipRechargeOrder> findByUserAndStatus(User user, String status);

    /**
     * 分页查询用户充值订单，按创建时间倒序
     *
     * @param user 用户对象
     * @param pageable 分页参数
     * @return 充值订单分页
     */
    @Query("SELECT o FROM VipRechargeOrder o WHERE o.user = :user ORDER BY o.createdAt DESC")
    Page<VipRechargeOrder> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
