package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 店铺实体类
 * 对应数据库表：shop
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "shop")
public class Shop {

    /**
     * 店铺唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 店铺名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 店铺Logo
     */
    @Column(length = 500)
    private String logo;

    /**
     * 店铺Banner图片
     */
    @Column(length = 1000)
    private String banner;

    /**
     * 店铺描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 店铺标语
     */
    @Column(length = 1000)
    private String slogan;

    /**
     * 店铺评分
     */
    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    /**
     * 粉丝数量
     */
    @Column(nullable = false)
    private Integer followers = 0;

    /**
     * 商品数量
     */
    @Column(nullable = false)
    private Integer productCount = 0;

    /**
     * 好评率
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal positiveRate = BigDecimal.ZERO;

    /**
     * 开店年限
     */
    @Column
    private Integer openYears = 0;

    /**
     * 店铺公告
     */
    @Column(length = 1000)
    private String announcement;

    /**
     * 是否已认证
     */
    @Column
    private Boolean certified = false;

    /**
     * 店铺标签（JSON数组）
     */
    @Column(length = 500)
    private String tags;

    /**
     * 店铺状态（active营业中、inactive停业、closed已关闭）
     */
    @Column(length = 100)
    private String status = "active";

    /**
     * 营业执照信息
     */
    @Column(length = 500)
    private String businessLicense;

    /**
     * 店铺位置
     */
    @Column(length = 200)
    private String location;

    /**
     * 描述评分
     */
    @Column(precision = 3, scale = 2)
    private BigDecimal descriptionScore = BigDecimal.ZERO;

    /**
     * 服务评分
     */
    @Column(precision = 3, scale = 2)
    private BigDecimal serviceScore = BigDecimal.ZERO;

    /**
     * 物流评分
     */
    @Column(precision = 3, scale = 2)
    private BigDecimal logisticsScore = BigDecimal.ZERO;

    /**
     * 店铺所有者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

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

    public Shop() {}

    public Shop(String name, String description, User owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
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

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public String getBanner() { return banner; }
    public void setBanner(String banner) { this.banner = banner; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSlogan() { return slogan; }
    public void setSlogan(String slogan) { this.slogan = slogan; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Integer getFollowers() { return followers; }
    public void setFollowers(Integer followers) { this.followers = followers; }

    public Integer getProductCount() { return productCount; }
    public void setProductCount(Integer productCount) { this.productCount = productCount; }

    public BigDecimal getPositiveRate() { return positiveRate; }
    public void setPositiveRate(BigDecimal positiveRate) { this.positiveRate = positiveRate; }

    public Integer getOpenYears() { return openYears; }
    public void setOpenYears(Integer openYears) { this.openYears = openYears; }

    public String getAnnouncement() { return announcement; }
    public void setAnnouncement(String announcement) { this.announcement = announcement; }

    public Boolean getCertified() { return certified; }
    public void setCertified(Boolean certified) { this.certified = certified; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBusinessLicense() { return businessLicense; }
    public void setBusinessLicense(String businessLicense) { this.businessLicense = businessLicense; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public BigDecimal getDescriptionScore() { return descriptionScore; }
    public void setDescriptionScore(BigDecimal descriptionScore) { this.descriptionScore = descriptionScore; }

    public BigDecimal getServiceScore() { return serviceScore; }
    public void setServiceScore(BigDecimal serviceScore) { this.serviceScore = serviceScore; }

    public BigDecimal getLogisticsScore() { return logisticsScore; }
    public void setLogisticsScore(BigDecimal logisticsScore) { this.logisticsScore = logisticsScore; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
