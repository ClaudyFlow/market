package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * VIP 礼包领取记录实体类
 */
@Entity
@Table(name = "vip_gift_record")
public class VipGiftRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_id", nullable = false)
    private VipGift gift;

    @Column(name = "claimed_at", nullable = false, updatable = false)
    private LocalDateTime claimedAt;

    @Column(name = "reward_type", length = 20)
    private String rewardType;

    @Column(name = "reward_value", length = 500)
    private String rewardValue;

    public VipGiftRecord() {}

    @PrePersist
    protected void onCreate() {
        claimedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public VipGift getGift() { return gift; }
    public void setGift(VipGift gift) { this.gift = gift; }

    public LocalDateTime getClaimedAt() { return claimedAt; }
    public void setClaimedAt(LocalDateTime claimedAt) { this.claimedAt = claimedAt; }

    public String getRewardType() { return rewardType; }
    public void setRewardType(String rewardType) { this.rewardType = rewardType; }

    public String getRewardValue() { return rewardValue; }
    public void setRewardValue(String rewardValue) { this.rewardValue = rewardValue; }
}
