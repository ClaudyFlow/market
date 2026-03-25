package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 */
@Entity
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id")
    private User platform; // 平台创建者为空，商家创建者指向商家用户

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private User merchant; // 创建商家

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String type; // PERCENT-折扣券，FIXED-满减券

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue; // 优惠金额或折扣比例

    @Column(precision = 10, scale = 2)
    private BigDecimal minPurchase; // 最低消费金额

    @Column(precision = 10, scale = 2)
    private BigDecimal maxDiscount; // 最大优惠金额（折扣券用）

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(nullable = false)
    private Integer totalCount = 100;

    @Column(nullable = false)
    private Integer usedCount = 0;

    @Column(nullable = false)
    private Integer remainCount = 100;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, EXPIRED, USED_UP

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String scope; // ALL-全场通用，CATEGORY-品类券，PRODUCT-商品券

    @Column(name = "category_ids", columnDefinition = "TEXT")
    private String categoryIds; // 适用品类 ID 列表，逗号分隔

    @Column(name = "product_ids", columnDefinition = "TEXT")
    private String productIds; // 适用商品 ID 列表，逗号分隔

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Coupon() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        remainCount = totalCount;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getPlatform() { return platform; }
    public void setPlatform(User platform) { this.platform = platform; }

    public User getMerchant() { return merchant; }
    public void setMerchant(User merchant) { this.merchant = merchant; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public BigDecimal getMinPurchase() { return minPurchase; }
    public void setMinPurchase(BigDecimal minPurchase) { this.minPurchase = minPurchase; }

    public BigDecimal getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }

    public Integer getRemainCount() { return remainCount; }
    public void setRemainCount(Integer remainCount) { this.remainCount = remainCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getCategoryIds() { return categoryIds; }
    public void setCategoryIds(String categoryIds) { this.categoryIds = categoryIds; }

    public String getProductIds() { return productIds; }
    public void setProductIds(String productIds) { this.productIds = productIds; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * 检查优惠券是否可用
     */
    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return "ACTIVE".equals(status) && 
               remainCount > 0 &&
               (validFrom == null || now.isAfter(validFrom) || now.isEqual(validFrom)) &&
               (validTo == null || now.isBefore(validTo) || now.isEqual(validTo));
    }

    /**
     * 计算优惠金额
     */
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (!isAvailable() || orderAmount.compareTo(minPurchase) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if ("PERCENT".equals(type)) {
            // 折扣券：订单金额 * 折扣比例
            discount = orderAmount.multiply(discountValue.divide(new BigDecimal("100")));
            // 有最大优惠限制
            if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
                discount = maxDiscount;
            }
        } else {
            // 满减券：固定金额
            discount = discountValue;
        }

        // 优惠不能超过订单金额
        return discount.min(orderAmount);
    }

    /**
     * 更新优惠券状态
     */
    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (remainCount <= 0) {
            status = "USED_UP";
        } else if (validTo != null && now.isAfter(validTo)) {
            status = "EXPIRED";
        } else if (!"ACTIVE".equals(status)) {
            status = "INACTIVE";
        } else {
            status = "ACTIVE";
        }
    }
}
