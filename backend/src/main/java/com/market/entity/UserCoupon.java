package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体类
 * 对应数据库表：user_coupon
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "user_coupon")
public class UserCoupon {

    /**
     * 用户优惠券唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 关联的优惠券
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    /**
     * 使用时间
     */
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    /**
     * 使用的订单ID
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 记录创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 获取时间
     */
    @Column(name = "obtained_at")
    private LocalDateTime obtainedAt;

    /**
     * 优惠券状态（UNUSED未使用、USED已使用、EXPIRED已过期）
     */
    @Column(nullable = false)
    private String status = "UNUSED";
    
    public UserCoupon() {}
    
    public UserCoupon(User user, Coupon coupon) {
        this.user = user;
        this.coupon = coupon;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        obtainedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Coupon getCoupon() { return coupon; }
    public void setCoupon(Coupon coupon) { this.coupon = coupon; }

    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getObtainedAt() { return obtainedAt; }
    public void setObtainedAt(LocalDateTime obtainedAt) { this.obtainedAt = obtainedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
