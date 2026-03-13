package com.market.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 注册请求数据传输对象
 * <p>
 * 用于接收用户注册请求的数据，包含用户名、邮箱、密码、确认密码和验证码字段。
 * 使用Jakarta Validation进行参数验证。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
public class RegisterRequest {

    /**
     * 用户名
     * 不能为空，长度必须在3-50之间
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    private String name;

    /**
     * 邮箱
     * 不能为空，必须符合邮箱格式
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 密码
     * 不能为空，长度必须在6-50之间
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50之间")
    private String password;

    /**
     * 确认密码
     * 不能为空，用于验证密码输入一致性
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 验证码
     * 不能为空，必须是4-6位数字，用于邮箱验证
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 6, message = "验证码必须是4-6位数字")
    private String verificationCode;

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户名
     *
     * @param name 用户名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取邮箱
     *
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取确认密码
     *
     * @return 确认密码
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * 设置确认密码
     *
     * @param confirmPassword 确认密码
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * 获取验证码
     *
     * @return 验证码
     */
    public String getVerificationCode() {
        return verificationCode;
    }

    /**
     * 设置验证码
     *
     * @param verificationCode 验证码
     */
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}