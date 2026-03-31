package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户评价实体
 * <p>
 * 记录用户对商品的评价。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@Entity
@Table(name = "product_review")
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 用户名（冗余存储）
     */
    @Column(name = "user_name", length = 50)
    private String userName;

    /**
     * 用户头像（冗余存储）
     */
    @Column(name = "user_avatar", length = 500)
    private String userAvatar;

    /**
     * 商品 ID
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * 商品名称（冗余存储）
     */
    @Column(name = "product_name", length = 200)
    private String productName;

    /**
     * 订单 ID
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * 评分（1-5 星）
     */
    @Column(name = "rating", nullable = false)
    private Integer rating;

    /**
     * 评价内容
     */
    @Column(name = "content", length = 2000)
    private String content;

    /**
     * 评价图片（JSON 数组存储图片 URL）
     */
    @Column(name = "images", length = 2000)
    private String images;

    /**
     * 商家 ID
     */
    @Column(name = "merchant_id")
    private Long merchantId;

    /**
     * 商家回复内容
     */
    @Column(name = "merchant_reply", length = 1000)
    private String merchantReply;

    /**
     * 商家回复时间
     */
    @Column(name = "reply_time")
    private LocalDateTime replyTime;

    /**
     * 审核状态（PENDING, APPROVED, REJECTED）
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "PENDING";

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public ProductReview() {
    }

    public ProductReview(Long userId, Long productId, Long orderId, Integer rating, String content) {
        this.userId = userId;
        this.productId = productId;
        this.orderId = orderId;
        this.rating = rating;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantReply() {
        return merchantReply;
    }

    public void setMerchantReply(String merchantReply) {
        this.merchantReply = merchantReply;
    }

    public LocalDateTime getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(LocalDateTime replyTime) {
        this.replyTime = replyTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
