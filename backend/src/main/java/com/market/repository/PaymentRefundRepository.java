package com.market.repository;

import com.market.entity.PaymentRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 退款记录数据访问层
 */
@Repository
public interface PaymentRefundRepository extends JpaRepository<PaymentRefund, Long> {
    
    Optional<PaymentRefund> findByRefundNo(String refundNo);
    
    List<PaymentRefund> findByPaymentNo(String paymentNo);
    
    List<PaymentRefund> findByOrderId(Long orderId);
    
    List<PaymentRefund> findByUserId(Long userId);
}
