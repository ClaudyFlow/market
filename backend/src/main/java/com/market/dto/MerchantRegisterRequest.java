package com.market.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 商家注册请求 DTO
 */
public class MerchantRegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在 3-50 之间")
    private String name;

    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在 6-50 之间")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @NotBlank(message = "店铺名称不能为空")
    @Size(min = 2, max = 100, message = "店铺名称长度必须在 2-100 之间")
    private String shopName;

    @Size(max = 500, message = "店铺描述不能超过 500 字")
    private String shopDescription;

    @Size(max = 20, message = "手机号格式不正确")
    private String phone;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getShopDescription() { return shopDescription; }
    public void setShopDescription(String shopDescription) { this.shopDescription = shopDescription; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
