package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家活动参与设置实体
 * 记录商家对某个平台活动的参与方式（金额/折扣）或退出
 * 对应数据库表：merchant_activity_opt_out
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "merchant_activity_opt_out", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"merchant_id", "activity_id"})
})
public class MerchantActivityOptOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商家ID
     */
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    /**
     * 活动ID
     */
    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    /**
     * 是否退出该活动（true=退出，false=参与）
     */
    @Column(name = "opted_out", nullable = false)
    private Boolean optedOut = false;

    /**
     * 商家自定义折扣率（0.01~0.99，仅 optedOut=false 时有效）
     */
    @Column(name = "custom_discount_rate", precision = 5, scale = 2)
    private BigDecimal customDiscountRate;

    /**
     * 商家自定义固定减免金额（仅 optedOut=false 且支持金额模式时有效）
     */
    @Column(name = "custom_discount_amount", precision = 10, scale = 2)
    private BigDecimal customDiscountAmount;

    /**
     * 折扣类型（DISCOUNT折扣、MONEY金额）
     * 如果活动是DISCOUNT，商家可选择DISCOUNT或MONEY
     */
    @Column(name = "discount_type", length = 20)
    private String discountType;

    /**
     * 备注（如商家填写退出原因）
     */
    @Column(length = 200)
    private String remark;

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

    public MerchantActivityOptOut() {}

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

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Boolean getOptedOut() { return optedOut; }
    public void setOptedOut(Boolean optedOut) { this.optedOut = optedOut; }

    public BigDecimal getCustomDiscountRate() { return customDiscountRate; }
    public void setCustomDiscountRate(BigDecimal customDiscountRate) { this.customDiscountRate = customDiscountRate; }

    public BigDecimal getCustomDiscountAmount() { return customDiscountAmount; }
    public void setCustomDiscountAmount(BigDecimal customDiscountAmount) { this.customDiscountAmount = customDiscountAmount; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
