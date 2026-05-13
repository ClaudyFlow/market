package com.market.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求数据传输对象
 * <p>
 * 用于接收用户登录请求的数据，包含用户名和密码字段。
 * 使用Jakarta Validation进行参数验证。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
public class LoginRequest {

    /**
     * 用户名
     * 不能为空
     */
    @NotBlank(message = "用户名不能为空")
    private String name;

    /**
     * 密码
     * 不能为空
     */
    @NotBlank(message = "密码不能为空")
    private String password;

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
}