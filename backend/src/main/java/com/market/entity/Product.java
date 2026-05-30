package com.market.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体类
 */
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 1000)
    private String imageUrls;

    @Column(nullable = false, length = 100)
    private String category;

    /**
     * 分类ID（关联category表）
     */
    @Column(name = "category_id")
    private Long categoryId;

    @Column(length = 100)
    private String brand;

    @Column(nullable = false)
    private Boolean available = true;

    @Column
    private Integer status = 1; // 0-下架，1-上架

    @Column
    private Integer auditStatus = 1; // 0-待审核，1-审核通过，2-审核拒绝

    @Column(length = 500)
    private String rejectReason;

    @Column
    private Double rating = 0.0;

    @Column
    private Integer reviewCount = 0;

    @Column
    private Integer sales = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(length = 500)
    private String colors;

    @Column(length = 500)
    private String versions;

    @Column(name = "detail_images", length = 2000)
    private String detailImages;

    @Column(name = "detail_text", length = 5000)
    private String detailText;

    public Product() {}

    public Product(String name, String description, BigDecimal price, Integer stock, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getAuditStatus() { return auditStatus; }
    public void setAuditStatus(Integer auditStatus) { this.auditStatus = auditStatus; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public Integer getSales() { return sales; }
    public void setSales(Integer sales) { this.sales = sales; }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public User getMerchant() { return merchant; }
    public void setMerchant(User merchant) { this.merchant = merchant; }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getColors() { return colors; }
    public void setColors(String colors) { this.colors = colors; }

    public String getVersions() { return versions; }
    public void setVersions(String versions) { this.versions = versions; }

    public String getDetailImages() { return detailImages; }
    public void setDetailImages(String detailImages) { this.detailImages = detailImages; }

    public String getDetailText() { return detailText; }
    public void setDetailText(String detailText) { this.detailText = detailText; }

    public java.util.List<String> getColorsList() {
        if (colors == null || colors.isEmpty()) return new java.util.ArrayList<>();
        return java.util.Arrays.asList(colors.split(","));
    }

    @JsonProperty("colors")
    public java.util.List<String> getColorsListJson() {
        return getColorsList();
    }

    @JsonProperty("versions")
    public java.util.List<String> getVersionsList() {
        if (versions == null || versions.isEmpty()) return new java.util.ArrayList<>();
        return java.util.Arrays.asList(versions.split(","));
    }

    @JsonProperty("detailImages")
    public java.util.List<String> getDetailImagesList() {
        if (detailImages == null || detailImages.isEmpty()) return new java.util.ArrayList<>();
        return java.util.Arrays.asList(detailImages.split(","));
    }

    public void setColorsList(java.util.List<String> colorsList) {
        if (colorsList == null || colorsList.isEmpty()) {
            this.colors = "";
        } else {
            this.colors = String.join(",", colorsList);
        }
    }

    public void setVersionsList(java.util.List<String> versionsList) {
        if (versionsList == null || versionsList.isEmpty()) {
            this.versions = "";
        } else {
            this.versions = String.join(",", versionsList);
        }
    }

    public void setDetailImagesList(java.util.List<String> imagesList) {
        if (imagesList == null || imagesList.isEmpty()) {
            this.detailImages = "";
        } else {
            this.detailImages = String.join(",", imagesList);
        }
    }

    public int getDiscount() {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 100;
        }
        return (int) (price.multiply(BigDecimal.valueOf(100)).divide(originalPrice, 0).doubleValue());
    }

    /**
     * 获取商品图片 URL（别名方法）
     */
    public String getImage() {
        return this.imageUrl;
    }
}
