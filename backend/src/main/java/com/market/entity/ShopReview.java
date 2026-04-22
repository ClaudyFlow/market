package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 店铺评价实体类
 * 对应数据库表：shop_review
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "shop_review")
public class ShopReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "description_score", precision = 3, scale = 2)
    private BigDecimal descriptionScore;

    @Column(name = "service_score", precision = 3, scale = 2)
    private BigDecimal serviceScore;

    @Column(name = "logistics_score", precision = 3, scale = 2)
    private BigDecimal logisticsScore;

    @Column(length = 1000)
    private String content;

    @Column(length = 500)
    private String images;

    @Column(name = "merchant_reply", length = 500)
    private String merchantReply;

    @Column(name = "merchant_reply_at")
    private LocalDateTime merchantReplyAt;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", insertable = false, updatable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public ShopReview() {}

    public ShopReview(Long userId, Long shopId, Integer rating) {
        this.userId = userId;
        this.shopId = shopId;
        this.rating = rating;
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public BigDecimal getDescriptionScore() { return descriptionScore; }
    public void setDescriptionScore(BigDecimal descriptionScore) { this.descriptionScore = descriptionScore; }

    public BigDecimal getServiceScore() { return serviceScore; }
    public void setServiceScore(BigDecimal serviceScore) { this.serviceScore = serviceScore; }

    public BigDecimal getLogisticsScore() { return logisticsScore; }
    public void setLogisticsScore(BigDecimal logisticsScore) { this.logisticsScore = logisticsScore; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getMerchantReply() { return merchantReply; }
    public void setMerchantReply(String merchantReply) { this.merchantReply = merchantReply; }

    public LocalDateTime getMerchantReplyAt() { return merchantReplyAt; }
    public void setMerchantReplyAt(LocalDateTime merchantReplyAt) { this.merchantReplyAt = merchantReplyAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}