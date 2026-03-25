package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * VIP 礼包实体类
 */
@Entity
@Table(name = "vip_gift")
public class VipGift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String type; // DAILY, MONTHLY, BEGINNER, LEVEL_UP

    @Column(name = "vip_level_required", nullable = false)
    private Integer vipLevelRequired = 0;

    @Column(name = "credit_reward", nullable = false)
    private Integer creditReward = 0;

    @Column(name = "coupon_ids", length = 500)
    private String couponIds;

    @Column(name = "product_ids", length = 500)
    private String productIds;

    @Column(name = "claim_type", nullable = false, length = 20)
    private String claimType; // DAILY, MONTHLY, ONCE

    @Column(name = "claim_interval_hours", nullable = false)
    private Integer claimIntervalHours = 24;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String image;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public VipGift() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getVipLevelRequired() { return vipLevelRequired; }
    public void setVipLevelRequired(Integer vipLevelRequired) { this.vipLevelRequired = vipLevelRequired; }

    public Integer getCreditReward() { return creditReward; }
    public void setCreditReward(Integer creditReward) { this.creditReward = creditReward; }

    public String getCouponIds() { return couponIds; }
    public void setCouponIds(String couponIds) { this.couponIds = couponIds; }

    public String getProductIds() { return productIds; }
    public void setProductIds(String productIds) { this.productIds = productIds; }

    public String getClaimType() { return claimType; }
    public void setClaimType(String claimType) { this.claimType = claimType; }

    public Integer getClaimIntervalHours() { return claimIntervalHours; }
    public void setClaimIntervalHours(Integer claimIntervalHours) { this.claimIntervalHours = claimIntervalHours; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
