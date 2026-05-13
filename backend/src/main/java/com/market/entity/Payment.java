package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 * 对应数据库表：payment
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "payment")
public class Payment {

    /**
     * 支付记录唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 支付流水号
     */
    @Column(unique = true, nullable = false, length = 64)
    private String paymentNo;

    /**
     * 关联的订单编号
     */
    @Column(unique = true, nullable = false, length = 64)
    private String orderNo;

    /**
     * 支付用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 支付方式（ALIPAY支付宝、WECHAT微信、BANK银行卡）
     */
    @Column(nullable = false, length = 20)
    private String paymentMethod;

    /**
     * 支付金额
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 支付状态（PENDING待支付、SUCCESS支付成功、FAILED支付失败、REFUNDED已退款）
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * 第三方支付流水号
     */
    @Column(length = 64)
    private String transactionId;

    /**
     * 回调原始数据
     */
    @Column(columnDefinition = "TEXT")
    private String callbackData;

    /**
     * 支付成功时间
     */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /**
     * 记录创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 记录更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Payment() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentNo() { return paymentNo; }
    public void setPaymentNo(String paymentNo) { this.paymentNo = paymentNo; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getCallbackData() { return callbackData; }
    public void setCallbackData(String callbackData) { this.callbackData = callbackData; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
