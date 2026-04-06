package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商品收藏实体类
 * 对应数据库表：favorite
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "favorite")
public class Favorite {

    /**
     * 收藏记录唯一标识
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
     * 商品ID
     */
    @Column(nullable = false)
    private Long productId;

    /**
     * 收藏时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 关联的商品信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;

    public Favorite() {}

    public Favorite(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
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

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
