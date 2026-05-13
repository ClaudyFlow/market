package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品评价实体类
 * 对应数据库表：review
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "review")
public class Review {

    /**
     * 评价唯一标识
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
     * 评分（1-5星）
     */
    @Column(nullable = false)
    private Integer rating;

    /**
     * 评价内容
     */
    @Column(length = 1000)
    private String content;

    /**
     * 用户名（冗余存储）
     */
    @Column(name = "user_name", nullable = false)
    private String userName;

    /**
     * 用户头像
     */
    @Column(name = "user_avatar")
    private String userAvatar;

    /**
     * 商品名称（冗余存储）
     */
    @Column(name = "product_name", nullable = false)
    private String productName;

    /**
     * 商品图片
     */
    @Column(name = "product_image")
    private String productImage;

    /**
     * 商品价格
     */
    @Column(name = "product_price", precision = 10, scale = 2)
    private BigDecimal productPrice;

    /**
     * 关联的订单ID
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 评价图片（JSON数组）
     */
    @Column(name = "images", length = 2000)
    private String images;

    /**
     * 审核状态 (PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, FILTERED-已过滤)
     */
    @Column(name = "audit_status", length = 20)
    private String auditStatus = "PENDING";

    /**
     * 审核原因（拒绝或过滤原因）
     */
    @Column(name = "audit_reason", length = 500)
    private String auditReason;

    /**
     * 过滤后的内容（敏感词替换后）
     */
    @Column(name = "filtered_content", length = 1000)
    private String filteredContent;

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

    /**
     * 关联的用户信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    /**
     * 关联的商品信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;

    public Review() {}

    public Review(Long userId, Long productId, Integer rating, String content,
                  String userName, String userAvatar, String productName,
                  String productImage, BigDecimal productPrice) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.content = content;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }

    public String getAuditReason() { return auditReason; }
    public void setAuditReason(String auditReason) { this.auditReason = auditReason; }

    public String getFilteredContent() { return filteredContent; }
    public void setFilteredContent(String filteredContent) { this.filteredContent = filteredContent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
