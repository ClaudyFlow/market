package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * VIP 等级实体类
 */
@Entity
@Table(name = "vip_level")
public class VipLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer level;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 500)
    private String icon;

    @Column(name = "growth_value_required", nullable = false)
    private Integer growthValueRequired = 0;

    @Column(name = "discount_rate", nullable = false, precision = 3, scale = 2)
    private BigDecimal discountRate = BigDecimal.ONE;

    @Column(name = "daily_credit", nullable = false)
    private Integer dailyCredit = 0;

    @Column(name = "monthly_credit", nullable = false)
    private Integer monthlyCredit = 0;

    @Column(name = "free_shipping_count", nullable = false)
    private Integer freeShippingCount = 0;

    @Column(name = "refund_priority", nullable = false)
    private Boolean refundPriority = false;

    @Column(name = "exclusive_service", nullable = false)
    private Boolean exclusiveService = false;

    @Column(length = 500)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String privileges;

    @Column(name = "background_color", length = 20)
    private String backgroundColor;

    @Column(name = "text_color", length = 20)
    private String textColor;

    public VipLevel() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getGrowthValueRequired() { return growthValueRequired; }
    public void setGrowthValueRequired(Integer growthValueRequired) { this.growthValueRequired = growthValueRequired; }

    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }

    public Integer getDailyCredit() { return dailyCredit; }
    public void setDailyCredit(Integer dailyCredit) { this.dailyCredit = dailyCredit; }

    public Integer getMonthlyCredit() { return monthlyCredit; }
    public void setMonthlyCredit(Integer monthlyCredit) { this.monthlyCredit = monthlyCredit; }

    public Integer getFreeShippingCount() { return freeShippingCount; }
    public void setFreeShippingCount(Integer freeShippingCount) { this.freeShippingCount = freeShippingCount; }

    public Boolean getRefundPriority() { return refundPriority; }
    public void setRefundPriority(Boolean refundPriority) { this.refundPriority = refundPriority; }

    public Boolean getExclusiveService() { return exclusiveService; }
    public void setExclusiveService(Boolean exclusiveService) { this.exclusiveService = exclusiveService; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPrivileges() { return privileges; }
    public void setPrivileges(String privileges) { this.privileges = privileges; }

    public String getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }

    public String getTextColor() { return textColor; }
    public void setTextColor(String textColor) { this.textColor = textColor; }
}
