package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 * 对应数据库表：order
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "\"order\"")
public class Order {

    /**
     * 订单唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单编号
     */
    @Column(unique = true, nullable = false)
    private String orderNo;

    /**
     * 下单用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 商家用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private User merchant;

    /**
     * 订单商品项列表
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> item = new ArrayList<>();

    /**
     * 订单总金额
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 订单状态（PENDING待支付、PAID已支付、SHIPPED已发货、COMPLETED已完成、CANCELLED已取消、REFUNDING退款中）
     */
    @Column(nullable = false, length = 50)
    private String status = "PENDING";

    /**
     * 收货地址
     */
    @Column(length = 500)
    private String shippingAddress;

    /**
     * 支付方式（ALIPAY支付宝、WECHAT微信、CARD银行卡）
     */
    @Column(length = 50)
    private String paymentMethod;

    /**
     * 物流运单号
     */
    @Column(length = 100)
    private String trackingNo;

    /**
     * 物流承运商
     */
    @Column(length = 50)
    private String carrier;

    /**
     * 退款原因
     */
    @Column(length = 500)
    private String refundReason;

    /**
     * 退款图片（JSON格式）
     */
    @Column(columnDefinition = "TEXT")
    private String refundImages;

    /**
     * 取消原因
     */
    @Column(length = 500)
    private String cancelReason;

    /**
     * 订单创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 订单更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 支付时间
     */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /**
     * 发货时间
     */
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    /**
     * 完成时间
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 取消时间
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    public Order() {}

    public void addItem(OrderItem newItem) {
        item.add(newItem);
        newItem.setOrder(this);
    }

    public void removeItem(OrderItem itemToRemove) {
        item.remove(itemToRemove);
        itemToRemove.setOrder(null);
    }

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

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public User getMerchant() { return merchant; }
    public void setMerchant(User merchant) { this.merchant = merchant; }

    public List<OrderItem> getItem() { return item; }
    public void setItem(List<OrderItem> item) { this.item = item; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }

    public String getRefundImages() { return refundImages; }
    public void setRefundImages(String refundImages) { this.refundImages = refundImages; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getShippedAt() { return shippedAt; }
    public void setShippedAt(LocalDateTime shippedAt) { this.shippedAt = shippedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
