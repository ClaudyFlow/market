package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 店铺实体类
 */
@Entity
@Table(name = "shop")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String logo;

    @Column(length = 1000)
    private String banner;

    @Column(length = 500)
    private String description;

    @Column(length = 1000)
    private String slogan;

    @Column(nullable = false)
    private Double rating = 0.0;

    @Column(nullable = false)
    private Integer followers = 0;

    @Column(nullable = false)
    private Integer productCount = 0;

    @Column(nullable = false)
    private Double positiveRate = 0.0;

    @Column
    private Integer openYears = 0;

    @Column(length = 1000)
    private String announcement;

    @Column
    private Boolean certified = false;

    @Column(length = 500)
    private String tags;

    @Column(length = 100)
    private String status = "active"; // active, inactive, closed

    @Column(length = 500)
    private String businessLicense;

    @Column(length = 200)
    private String location;

    @Column(precision = 10, scale = 2)
    private Double descriptionScore = 0.0;

    @Column(precision = 10, scale = 2)
    private Double serviceScore = 0.0;

    @Column(precision = 10, scale = 2)
    private Double logisticsScore = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getFollowers() { return followers; }
    public void setFollowers(Integer followers) { this.followers = followers; }

    public Integer getProductCount() { return productCount; }
    public void setProductCount(Integer productCount) { this.productCount = productCount; }

    public Double getPositiveRate() { return positiveRate; }
    public void setPositiveRate(Double positiveRate) { this.positiveRate = positiveRate; }

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

    public Double getDescriptionScore() { return descriptionScore; }
    public void setDescriptionScore(Double descriptionScore) { this.descriptionScore = descriptionScore; }

    public Double getServiceScore() { return serviceScore; }
    public void setServiceScore(Double serviceScore) { this.serviceScore = serviceScore; }

    public Double getLogisticsScore() { return logisticsScore; }
    public void setLogisticsScore(Double logisticsScore) { this.logisticsScore = logisticsScore; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
