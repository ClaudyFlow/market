package com.market.repository;

import com.market.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 支付记录数据访问层
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentNo(String paymentNo);
    
    Optional<Payment> findByOrderNo(String orderNo);
    
    Optional<Payment> findByTransactionId(String transactionId);
}
