package com.market.repository;

import com.market.entity.Order;
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
 * 订单数据访问层
 * 对应实体：Order
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据用户查询订单列表
     *
     * @param user 用户对象
     * @return 订单列表
     */
    List<Order> findByUser(User user);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单对象，不存在返回empty
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据用户查询订单列表，按创建时间倒序
     *
     * @param user 用户对象
     * @return 订单列表
     */
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 分页查询用户订单
     *
     * @param user 用户对象
     * @param pageable 分页参数
     * @return 订单分页
     */
    Page<Order> findByUser(User user, Pageable pageable);

    /**
     * 分页查询用户指定状态的订单
     *
     * @param user 用户对象
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 订单分页
     */
    Page<Order> findByUserAndStatus(User user, String status, Pageable pageable);

    /**
     * 分页查询商家的订单
     *
     * @param merchant 商家对象
     * @param pageable 分页参数
     * @return 订单分页
     */
    Page<Order> findByMerchant(User merchant, Pageable pageable);

    /**
     * 分页查询商家指定状态的订单
     *
     * @param merchant 商家对象
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 订单分页
     */
    Page<Order> findByMerchantAndStatus(User merchant, String status, Pageable pageable);

    /**
     * 分页查询指定状态的订单
     *
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 订单分页
     */
    Page<Order> findByStatus(String status, Pageable pageable);

    /**
     * 统计指定时间之后的订单数量
     *
     * @param createdAt 起始时间
     * @return 订单数量
     */
    long countByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * 统计指定时间范围内的订单数量
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 订单数量
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 统计指定时间范围内的订单总金额
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 订单总金额
     */
    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    java.math.BigDecimal sumTotalAmountByCreatedAtBetween(@org.springframework.data.repository.query.Param("start") LocalDateTime start, @org.springframework.data.repository.query.Param("end") LocalDateTime end);

    /**
     * 多条件查询订单列表
     *
     * @param orderNo 订单号（模糊匹配）
     * @param status 订单状态
     * @param userId 用户ID
     * @param merchantId 商家ID
     * @param shopName 店铺名称（模糊匹配）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageable 分页参数
     * @return 订单分页
     */
    @Query("SELECT o FROM Order o WHERE " +
           "(:orderNo IS NULL OR o.orderNo LIKE CONCAT('%', :orderNo, '%')) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:userId IS NULL OR o.user.id = :userId) AND " +
           "(:merchantId IS NULL OR o.merchant.id = :merchantId) AND " +
           "(:shopName IS NULL OR o.merchant.shopName LIKE CONCAT('%', :shopName, '%')) AND " +
           "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR o.createdAt <= :endDate)")
    Page<Order> findOrders(@Param("orderNo") String orderNo,
                          @Param("status") String status,
                          @Param("userId") Long userId,
                          @Param("merchantId") Long merchantId,
                          @Param("shopName") String shopName,
                          @Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate,
                          Pageable pageable);

    /**
     * 查询商家的退款订单
     *
     * @param merchantId 商家ID
     * @param pageable 分页参数
     * @return 退款订单分页
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'REFUNDING' AND o.merchant.id = :merchantId")
    Page<Order> findRefundOrders(@Param("merchantId") Long merchantId, Pageable pageable);

    /**
     * 查询所有退款订单
     *
     * @param pageable 分页参数
     * @return 退款订单分页
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'REFUNDING'")
    Page<Order> findAllRefundOrders(Pageable pageable);

    /**
     * 统计用户订单数量
     *
     * @param user 用户对象
     * @return 订单数量
     */
    long countByUser(User user);

    /**
     * 统计用户指定状态的订单数量
     *
     * @param user 用户对象
     * @param status 订单状态
     * @return 订单数量
     */
    long countByUserAndStatus(User user, String status);

    /**
     * 统计商家订单数量
     *
     * @param merchant 商家对象
     * @return 订单数量
     */
    long countByMerchant(User merchant);

    /**
     * 统计商家指定状态的订单数量
     *
     * @param merchant 商家对象
     * @param status 订单状态
     * @return 订单数量
     */
    long countByMerchantAndStatus(User merchant, String status);

    /**
     * 统计指定状态的订单数量
     *
     * @param status 订单状态
     * @return 订单数量
     */
    long countByStatus(String status);

    /**
     * 统计总订单数
     *
     * @return 总订单数
     */
    @Query("SELECT COUNT(o) FROM Order o")
    long countTotal();

    /**
     * 查询商品销量排行
     *
     * @param limit 限制数量
     * @return 商品销量排行数据（商品ID、名称、图片、销量、销售额）
     */
    @Query("SELECT oi.product.id, oi.product.name, oi.product.imageUrl, SUM(oi.quantity), SUM(oi.price * oi.quantity) " +
           "FROM OrderItem oi GROUP BY oi.product.id ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findProductSalesRank(int limit);

    /**
     * 查询店铺销量排行
     *
     * @param limit 限制数量
     * @return 店铺销量排行数据（店铺ID、名称、总销售额）
     */
    @Query("SELECT m.id, m.shopName, SUM(o.totalAmount) " +
           "FROM Order o JOIN o.merchant m WHERE o.status = 'COMPLETED' " +
           "GROUP BY m.id ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> findShopSalesRank(int limit);
}
