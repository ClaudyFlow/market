package com.market.repository;

import com.market.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单数据访问层扩展接口（用于推荐系统）
 */
@Repository
public interface OrderRepositoryCustom extends JpaRepository<Order, Long> {
    
    List<Order> findByUserId(Long userId);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status NOT IN ('CANCELLED')")
    List<Order> findValidOrdersByUserId(@Param("userId") Long userId);
    
    @Query("SELECT DISTINCT o.merchant.id FROM Order o WHERE o.user.id = :userId")
    List<Long> findFavoriteMerchantIds(@Param("userId") Long userId);
    
    @Query("SELECT i.product.id FROM Order o JOIN o.item i WHERE o.user.id = :userId")
    List<Long> findPurchasedProductIds(@Param("userId") Long userId);
}
