package com.market.dto;

/**
 * 认证响应数据传输对象
 * <p>
 * 用于返回用户认证（登录、注册）操作的结果，包含操作状态、消息、JWT token和用户信息。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
public class AuthResponse {

    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 响应消息
     * 包含操作结果的描述信息
     */
    private String message;

    /**
     * JWT token
     * 成功认证后返回的令牌，用于后续请求的身份验证
     */
    private String token;

    /**
     * 用户信息
     * 包含用户的基本信息
     */
    private UserDTO user;

    /**
     * 无参构造函数
     */
    public AuthResponse() {
    }

    /**
     * 带参构造函数
     *
     * @param success 操作是否成功
     * @param message 响应消息
     * @param token JWT token
     * @param user 用户信息
     */
    public AuthResponse(boolean success, String message, String token, UserDTO user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    /**
     * 获取操作是否成功
     *
     * @return 操作是否成功
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 设置操作是否成功
     *
     * @param success 操作是否成功
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 获取响应消息
     *
     * @return 响应消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置响应消息
     *
     * @param message 响应消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取JWT token
     *
     * @return JWT token
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置JWT token
     *
     * @param token JWT token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * 设置用户信息
     *
     * @param user 用户信息
     */
    public void setUser(UserDTO user) {
        this.user = user;
    }

    /**
     * 创建成功的认证响应
     *
     * @param message 成功消息
     * @param token JWT token
     * @param user 用户信息
     * @return 成功的AuthResponse对象
     */
    public static AuthResponse success(String message, String token, UserDTO user) {
        return new AuthResponse(true, message, token, user);
    }

    /**
     * 创建失败的认证响应
     *
     * @param message 失败消息
     * @return 失败的AuthResponse对象
     */
    public static AuthResponse failure(String message) {
        return new AuthResponse(false, message, null, null);
    }

    /**
     * 用户数据传输对象
     * <p>
     * 用于在认证响应中返回用户的基本信息，不包含敏感数据如密码。
     * </p>
     */
    public static class UserDTO {

        /**
         * 用户ID
         */
        private Long id;

        /**
         * 用户名
         */
        private String name;

        /**
         * 邮箱
         */
        private String email;

        /**
         * 头像URL
         */
        private String avatarUrl;

        /**
         * 无参构造函数
         */
        public UserDTO() {
        }

        /**
         * 带参构造函数
         *
         * @param id 用户ID
         * @param name 用户名
         * @param email 邮箱
         * @param avatarUrl 头像URL
         */
        public UserDTO(Long id, String name, String email, String avatarUrl) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.avatarUrl = avatarUrl;
        }

        /**
         * 获取用户ID
         *
         * @return 用户ID
         */
        public Long getId() {
            return id;
        }

        /**
         * 设置用户ID
         *
         * @param id 用户ID
         */
        public void setId(Long id) {
            this.id = id;
        }

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
         * 获取头像URL
         *
         * @return 头像URL
         */
        public String getAvatarUrl() {
            return avatarUrl;
        }

        /**
         * 设置头像URL
         *
         * @param avatarUrl 头像URL
         */
        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}