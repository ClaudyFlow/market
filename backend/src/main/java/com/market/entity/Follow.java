package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 店铺关注实体类
 * 对应数据库表：follow
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "follow")
public class Follow {

    /**
     * 关注记录唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 店铺ID
     */
    @Column(nullable = false)
    private Long shopId;

    /**
     * 店铺名称
     */
    @Column(name = "shop_name", nullable = false)
    private String shopName;

    /**
     * 店铺头像
     */
    @Column(name = "shop_avatar")
    private String shopAvatar;

    /**
     * 关注时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 关联的用户信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    public Follow() {}

    public Follow(Long userId, Long shopId, String shopName) {
        this.userId = userId;
        this.shopId = shopId;
        this.shopName = shopName;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getShopAvatar() { return shopAvatar; }
    public void setShopAvatar(String shopAvatar) { this.shopAvatar = shopAvatar; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
