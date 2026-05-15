package com.market.repository;

import com.market.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 支付记录数据访问层
 * 对应实体：Payment
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 根据支付单号查询支付记录
     *
     * @param paymentNo 支付单号
     * @return 支付记录对象，不存在返回empty
     */
    Optional<Payment> findByPaymentNo(String paymentNo);

    /**
     * 根据订单号查询支付记录
     *
     * @param orderNo 订单号
     * @return 支付记录对象，不存在返回empty
     */
    Optional<Payment> findByOrderNo(String orderNo);

    /**
     * 根据交易ID查询支付记录
     *
     * @param transactionId 交易ID
     * @return 支付记录对象，不存在返回empty
     */
    Optional<Payment> findByTransactionId(String transactionId);
}
