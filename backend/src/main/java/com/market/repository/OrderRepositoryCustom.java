package com.market.repository;

import com.market.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单数据访问层扩展接口（用于推荐系统）
 * 对应实体：Order
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface OrderRepositoryCustom extends JpaRepository<Order, Long> {

    /**
     * 根据用户ID查询订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(Long userId);

    /**
     * 查询用户的有效订单（排除已取消订单）
     *
     * @param userId 用户ID
     * @return 有效订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status NOT IN ('CANCELLED')")
    List<Order> findValidOrdersByUserId(@Param("userId") Long userId);

    /**
     * 查询用户常购商家ID列表
     *
     * @param userId 用户ID
     * @return 商家ID列表
     */
    @Query("SELECT DISTINCT o.merchant.id FROM Order o WHERE o.user.id = :userId")
    List<Long> findFavoriteMerchantIds(@Param("userId") Long userId);

    /**
     * 查询用户购买过的商品ID列表
     *
     * @param userId 用户ID
     * @return 商品ID列表
     */
    @Query("SELECT i.product.id FROM Order o JOIN o.item i WHERE o.user.id = :userId")
    List<Long> findPurchasedProductIds(@Param("userId") Long userId);
    
    /**
     * 查询购买过指定商品的用户同时购买的其他商品ID
     * 用于协同过滤推荐
     */
    @Query(value = """
        SELECT DISTINCT oi.product_id 
        FROM order_item oi 
        JOIN orders o ON oi.order_id = o.id 
        WHERE o.user_id IN (
            SELECT o2.user_id 
            FROM orders o2 
            JOIN order_item oi2 ON o2.id = oi2.order_id 
            WHERE oi2.product_id = :productId AND o2.status NOT IN ('CANCELLED')
        )
        AND oi.product_id != :productId
        AND o.status NOT IN ('CANCELLED')
        LIMIT 50
        """, nativeQuery = true)
    List<Long> findCoPurchasedProductIds(@Param("productId") Long productId);

}
