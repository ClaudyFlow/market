package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户收货地址实体
 * <p>
 * 记录用户的收货地址信息。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@Entity
@Table(name = "user_address")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 收货人姓名
     */
    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;

    /**
     * 收货人手机号
     */
    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    /**
     * 省份
     */
    @Column(name = "province", nullable = false, length = 50)
    private String province;

    /**
     * 城市
     */
    @Column(name = "city", nullable = false, length = 50)
    private String city;

    /**
     * 区县
     */
    @Column(name = "district", nullable = false, length = 50)
    private String district;

    /**
     * 详细地址
     */
    @Column(name = "detail_address", nullable = false, length = 200)
    private String detailAddress;

    /**
     * 邮政编码
     */
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    /**
     * 是否为默认地址
     */
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    /**
     * 地址标签（家、公司、学校等）
     */
    @Column(name = "address_tag", length = 20)
    private String addressTag;

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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UserAddress() {
    }

    public UserAddress(Long userId, String receiverName, String receiverPhone,
                       String province, String city, String district, String detailAddress) {
        this.userId = userId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detailAddress = detailAddress;
    }

    // Getters and Setters
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

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getAddressTag() {
        return addressTag;
    }

    public void setAddressTag(String addressTag) {
        this.addressTag = addressTag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取完整地址
     */
    public String getFullAddress() {
        return province + city + district + detailAddress;
    }
}
