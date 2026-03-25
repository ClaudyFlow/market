package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体类
 */
@Entity
@Table(name = "user_coupon")
public class UserCoupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "obtained_at")
    private LocalDateTime obtainedAt;

    @Column(nullable = false)
    private String status = "UNUSED"; // UNUSED, USED, EXPIRED
    
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
