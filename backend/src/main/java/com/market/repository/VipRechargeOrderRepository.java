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
 * VIP 充值订单数据访问层
 */
@Repository
public interface VipRechargeOrderRepository extends JpaRepository<VipRechargeOrder, Long> {
    
    Optional<VipRechargeOrder> findByOrderNo(String orderNo);
    
    List<VipRechargeOrder> findByUser(User user);
    
    Page<VipRechargeOrder> findByUser(User user, Pageable pageable);
    
    @Query("SELECT o FROM VipRechargeOrder o WHERE o.user = :user AND o.status = 'PENDING' " +
           "AND o.createdAt >= :expireTime")
    List<VipRechargeOrder> findPendingOrders(@Param("user") User user, 
                                             @Param("expireTime") LocalDateTime expireTime);
    
    @Query("SELECT SUM(o.amount) FROM VipRechargeOrder o WHERE o.user = :user AND o.status = 'PAID'")
    java.math.BigDecimal getTotalRechargeAmount(@Param("user") User user);
    
    @Query("SELECT SUM(o.growthValue) FROM VipRechargeOrder o WHERE o.user = :user AND o.status = 'PAID'")
    Integer getTotalGrowthValue(@Param("user") User user);
    
    List<VipRechargeOrder> findByUserAndStatus(User user, String status);
    
    @Query("SELECT o FROM VipRechargeOrder o WHERE o.user = :user ORDER BY o.createdAt DESC")
    Page<VipRechargeOrder> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
