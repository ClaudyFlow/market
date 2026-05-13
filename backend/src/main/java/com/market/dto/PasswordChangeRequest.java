package com.market.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 密码修改请求数据传输对象
 * <p>
 * 用于接收用户修改密码的请求数据。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
public class PasswordChangeRequest {

    /**
     * 当前密码
     */
    @NotBlank(message = "当前密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    /**
     * 确认新密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 获取当前密码
     *
     * @return 当前密码
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * 设置当前密码
     *
     * @param oldPassword 当前密码
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * 获取新密码
     *
     * @return 新密码
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * 设置新密码
     *
     * @param newPassword 新密码
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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
}
