package com.market.service;

import com.market.entity.Payment;
import com.market.entity.PaymentRefund;
import com.market.entity.User;
import com.market.repository.PaymentRefundRepository;
import com.market.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 支付服务类
 */
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentRefundRepository refundRepository;

    /**
     * 创建支付单
     */
    @Transactional
    public Payment createPayment(User user, String orderNo, BigDecimal amount, String paymentMethod) {
        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderNo(orderNo);
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("PENDING");
        return paymentRepository.save(payment);
    }

    /**
     * 模拟支付成功（预留接口）
     */
    @Transactional
    public Payment simulatePaymentSuccess(String paymentNo, String transactionId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentNo(paymentNo);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus("SUCCESS");
            payment.setTransactionId(transactionId);
            payment.setPaidAt(LocalDateTime.now());
            payment.setCallbackData(generateMockCallbackData(paymentNo, "SUCCESS"));
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("支付单不存在");
    }

    /**
     * 模拟支付失败（预留接口）
     */
    @Transactional
    public Payment simulatePaymentFailed(String paymentNo, String reason) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentNo(paymentNo);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus("FAILED");
            payment.setCallbackData(generateMockCallbackData(paymentNo, "FAILED"));
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("支付单不存在");
    }

    /**
     * 查询支付状态
     */
    public Payment getPaymentByPaymentNo(String paymentNo) {
        return paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new RuntimeException("支付单不存在"));
    }

    /**
     * 根据订单号查询支付单
     */
    public Payment getPaymentByOrderNo(String orderNo) {
        return paymentRepository.findByOrderNo(orderNo)
                .orElse(null);
    }

    /**
     * 创建退款申请
     */
    @Transactional
    public PaymentRefund createRefund(User user, Long orderId, String paymentNo, BigDecimal amount, String reason, List<String> images) {
        PaymentRefund refund = new PaymentRefund();
        refund.setRefundNo(generateRefundNo());
        refund.setPaymentNo(paymentNo);
        refund.setOrderId(orderId);
        refund.setUser(user);
        refund.setAmount(amount);
        refund.setReason(reason);
        refund.setImages(images != null ? String.join(",", images) : null);
        refund.setStatus("PENDING");
        return refundRepository.save(refund);
    }

    /**
     * 审核退款（商家/管理员）
     */
    @Transactional
    public PaymentRefund approveRefund(String refundNo, boolean approved, String remark) {
        Optional<PaymentRefund> refundOpt = refundRepository.findByRefundNo(refundNo);
        if (refundOpt.isPresent()) {
            PaymentRefund refund = refundOpt.get();
            if (approved) {
                refund.setStatus("SUCCESS");
                refund.setRefundedAt(LocalDateTime.now());
            } else {
                refund.setStatus("REJECTED");
            }
            refund.setMerchantRemark(remark);
            return refundRepository.save(refund);
        }
        throw new RuntimeException("退款单不存在");
    }

    /**
     * 查询退款单
     */
    public PaymentRefund getRefundByRefundNo(String refundNo) {
        return refundRepository.findByRefundNo(refundNo)
                .orElseThrow(() -> new RuntimeException("退款单不存在"));
    }

    /**
     * 获取用户退款列表
     */
    public List<PaymentRefund> getUserRefunds(Long userId) {
        return refundRepository.findByUserId(userId);
    }

    /**
     * 生成支付单号
     */
    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    /**
     * 生成退款单号
     */
    private String generateRefundNo() {
        return "REF" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    /**
     * 生成模拟回调数据（预留接口）
     */
    private String generateMockCallbackData(String paymentNo, String status) {
        return String.format("{\"paymentNo\":\"%s\",\"status\":\"%s\",\"timestamp\":\"%s\"}", 
                paymentNo, status, LocalDateTime.now());
    }
}
