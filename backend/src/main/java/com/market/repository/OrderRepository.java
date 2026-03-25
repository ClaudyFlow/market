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

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    Optional<Order> findByOrderNo(String orderNo);
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    
    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByUserAndStatus(User user, String status, Pageable pageable);
    
    Page<Order> findByMerchant(User merchant, Pageable pageable);
    Page<Order> findByMerchantAndStatus(User merchant, String status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE " +
           "(:orderNo IS NULL OR o.orderNo LIKE %:orderNo%) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:userId IS NULL OR o.user.id = :userId) AND " +
           "(:merchantId IS NULL OR o.merchant.id = :merchantId) AND " +
           "(:shopName IS NULL OR o.merchant.shopName LIKE %:shopName%) AND " +
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
    
    @Query("SELECT o FROM Order o WHERE o.status = 'REFUNDING' AND o.merchant.id = :merchantId")
    Page<Order> findRefundOrders(@Param("merchantId") Long merchantId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.status = 'REFUNDING'")
    Page<Order> findAllRefundOrders(Pageable pageable);
    
    long countByUser(User user);
    long countByUserAndStatus(User user, String status);
    
    long countByMerchant(User merchant);
    long countByMerchantAndStatus(User merchant, String status);
}
