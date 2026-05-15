package com.market.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 用户资料更新请求数据传输对象
 * <p>
 * 用于接收用户更新个人资料的请求数据。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
public class UserProfileUpdateRequest {

    /**
     * 用户头像 URL
     */
    @Size(max = 500, message = "头像 URL 长度不能超过 500 个字符")
    private String avatarUrl;

    /**
     * 用户手机号
     */
    @Size(max = 20, message = "手机号长度不能超过 20 个字符")
    private String phone;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户简介
     */
    @Size(max = 500, message = "简介长度不能超过 500 个字符")
    private String bio;

    /**
     * 获取用户头像 URL
     *
     * @return 头像 URL
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * 设置用户头像 URL
     *
     * @param avatarUrl 头像 URL
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * 获取用户手机号
     *
     * @return 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置用户手机号
     *
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取用户邮箱
     *
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置用户邮箱
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取用户简介
     *
     * @return 简介
     */
    public String getBio() {
        return bio;
    }

    /**
     * 设置用户简介
     *
     * @param bio 简介
     */
    public void setBio(String bio) {
        this.bio = bio;
    }
}
