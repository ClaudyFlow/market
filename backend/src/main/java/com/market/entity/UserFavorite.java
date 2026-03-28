package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户收藏实体
 * <p>
 * 记录用户收藏的商品。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@Entity
@Table(name = "user_favorite",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class UserFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 商品 ID
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * 商品名称（冗余存储，方便展示）
     */
    @Column(name = "product_name", length = 200)
    private String productName;

    /**
     * 商品图片（冗余存储，方便展示）
     */
    @Column(name = "product_image", length = 500)
    private String productImage;

    /**
     * 商品价格（冗余存储，方便展示）
     */
    @Column(name = "product_price", precision = 10, scale = 2)
    private java.math.BigDecimal productPrice;

    /**
     * 店铺 ID
     */
    @Column(name = "shop_id")
    private Long shopId;

    /**
     * 店铺名称（冗余存储，方便展示）
     */
    @Column(name = "shop_name", length = 100)
    private String shopName;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UserFavorite() {
    }

    public UserFavorite(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
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

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public java.math.BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(java.math.BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
