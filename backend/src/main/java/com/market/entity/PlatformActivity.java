package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 平台活动实体类
 * 平台管理员创建的全平台统一折扣活动
 * 对应数据库表：platform_activity
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "platform_activity")
public class PlatformActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 活动名称（如：618大促、双十一）
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 活动描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 活动类型（DISCOUNT折扣、CASH返现）
     */
    @Column(length = 20)
    private String type = "DISCOUNT";

    /**
     * 活动封面图
     */
    @Column(length = 500)
    private String image;

    /**
     * 折扣值
     * - DISCOUNT类型：填0.85表示85折，管理端只能设置折扣
     * - 范围：0.01 ~ 0.99
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal discountRate;

    /**
     * 活动开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 活动状态（DRAFT草稿、ACTIVE进行中、PAUSED暂停、ENDED已结束）
     */
    @Column(length = 20)
    private String status = "DRAFT";

    /**
     * 每人最大参与次数
     */
    @Column(name = "max_per_user")
    private Integer maxPerUser;

    /**
     * 活动总库存/数量限制
     */
    @Column(name = "total_quota")
    private Integer totalQuota;

    /**
     * 已使用数量
     */
    @Column(name = "used_quota")
    private Integer usedQuota = 0;

    /**
     * 参与商家数量
     */
    @Column(name = "participating_merchant_count")
    private Integer participatingMerchantCount = 0;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

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

    public PlatformActivity() {}

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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getMaxPerUser() { return maxPerUser; }
    public void setMaxPerUser(Integer maxPerUser) { this.maxPerUser = maxPerUser; }

    public Integer getTotalQuota() { return totalQuota; }
    public void setTotalQuota(Integer totalQuota) { this.totalQuota = totalQuota; }

    public Integer getUsedQuota() { return usedQuota; }
    public void setUsedQuota(Integer usedQuota) { this.usedQuota = usedQuota; }

    public Integer getParticipatingMerchantCount() { return participatingMerchantCount; }
    public void setParticipatingMerchantCount(Integer participatingMerchantCount) { this.participatingMerchantCount = participatingMerchantCount; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
