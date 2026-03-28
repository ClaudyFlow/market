package com.market.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户地址创建/更新请求数据传输对象
 *
 * @author Market Team
 * @since 1.0.0
 */
public class UserAddressRequest {

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过 50 个字符")
    private String receiverName;

    /**
     * 收货人手机号
     */
    @NotBlank(message = "收货人手机号不能为空")
    @Size(max = 20, message = "收货人手机号长度不能超过 20 个字符")
    private String receiverPhone;

    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    @Size(max = 50, message = "省份长度不能超过 50 个字符")
    private String province;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    @Size(max = 50, message = "城市长度不能超过 50 个字符")
    private String city;

    /**
     * 区县
     */
    @NotBlank(message = "区县不能为空")
    @Size(max = 50, message = "区县长度不能超过 50 个字符")
    private String district;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过 200 个字符")
    private String detailAddress;

    /**
     * 邮政编码
     */
    @Size(max = 10, message = "邮政编码长度不能超过 10 个字符")
    private String postalCode;

    /**
     * 是否为默认地址
     */
    private Boolean isDefault = false;

    /**
     * 地址标签（家、公司、学校等）
     */
    @Size(max = 20, message = "地址标签长度不能超过 20 个字符")
    private String addressTag;

    public UserAddressRequest() {
    }

    // Getters and Setters
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
}
