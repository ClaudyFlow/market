package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * VIP礼包实体类
 * 对应数据库表：vip_gift
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "vip_gift")
public class VipGift {

    /**
     * 礼包唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 礼包名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 礼包类型（DAILY每日、MONTHLY每月、BEGINNER新手、LEVEL_UP升级）
     */
    @Column(nullable = false, length = 20)
    private String type;

    /**
     * 所需VIP等级
     */
    @Column(name = "vip_level_required", nullable = false)
    private Integer vipLevelRequired = 0;

    /**
     * 奖励积分
     */
    @Column(name = "credit_reward", nullable = false)
    private Integer creditReward = 0;

    /**
     * 关联的优惠券ID列表（逗号分隔）
     */
    @Column(name = "coupon_ids", length = 500)
    private String couponIds;

    /**
     * 关联的商品ID列表（逗号分隔）
     */
    @Column(name = "product_ids", length = 500)
    private String productIds;

    /**
     * 领取类型（DAILY每日、MONTHLY每月、ONCE一次性）
     */
    @Column(name = "claim_type", nullable = false, length = 20)
    private String claimType;

    /**
     * 领取间隔小时数
     */
    @Column(name = "claim_interval_hours", nullable = false)
    private Integer claimIntervalHours = 24;

    /**
     * 礼包描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 礼包图片URL
     */
    @Column(length = 500)
    private String image;

    /**
     * 礼包状态（ACTIVE启用、INACTIVE停用）
     */
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    /**
     * 创建时间
     */
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
