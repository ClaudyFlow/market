package com.market.repository;

import com.market.entity.PaymentRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 退款记录数据访问层
 * 对应实体：PaymentRefund
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface PaymentRefundRepository extends JpaRepository<PaymentRefund, Long> {

    /**
     * 根据退款单号查询退款记录
     *
     * @param refundNo 退款单号
     * @return 退款记录对象，不存在返回empty
     */
    Optional<PaymentRefund> findByRefundNo(String refundNo);

    /**
     * 根据支付单号查询退款记录列表
     *
     * @param paymentNo 支付单号
     * @return 退款记录列表
     */
    List<PaymentRefund> findByPaymentNo(String paymentNo);

    /**
     * 根据订单ID查询退款记录列表
     *
     * @param orderId 订单ID
     * @return 退款记录列表
     */
    List<PaymentRefund> findByOrderId(Long orderId);

    /**
     * 根据用户ID查询退款记录列表
     *
     * @param userId 用户ID
     * @return 退款记录列表
     */
    List<PaymentRefund> findByUserId(Long userId);
}
