package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款记录实体类
 * 对应数据库表：payment_refund
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "payment_refund")
public class PaymentRefund {

    /**
     * 退款记录唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 退款单号
     */
    @Column(unique = true, nullable = false, length = 64)
    private String refundNo;

    /**
     * 关联的支付流水号
     */
    @Column(nullable = false, length = 64)
    private String paymentNo;

    /**
     * 关联的订单ID
     */
    @Column(nullable = false)
    private Long orderId;

    /**
     * 申请退款用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 退款金额
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 退款原因
     */
    @Column(length = 500)
    private String reason;

    /**
     * 退款凭证图片（JSON数组）
     */
    @Column(columnDefinition = "TEXT")
    private String images;

    /**
     * 退款状态（PENDING待审核、APPROVED已通过、REJECTED已拒绝、SUCCESS退款成功）
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * 商家处理备注
     */
    @Column(length = 500)
    private String merchantRemark;

    /**
     * 退款申请时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 退款完成时间
     */
    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    public PaymentRefund() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRefundNo() { return refundNo; }
    public void setRefundNo(String refundNo) { this.refundNo = refundNo; }

    public String getPaymentNo() { return paymentNo; }
    public void setPaymentNo(String paymentNo) { this.paymentNo = paymentNo; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMerchantRemark() { return merchantRemark; }
    public void setMerchantRemark(String merchantRemark) { this.merchantRemark = merchantRemark; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getRefundedAt() { return refundedAt; }
    public void setRefundedAt(LocalDateTime refundedAt) { this.refundedAt = refundedAt; }
}
