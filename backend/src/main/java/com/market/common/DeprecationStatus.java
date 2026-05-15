package com.market.common;

/**
 * API 弃用状态枚举
 */
public enum DeprecationStatus {

    /**
     * 尚未弃用（在 since 日期之前）
     */
    NOT_DEPRECATED("尚未弃用", "NOT_DEPRECATED", StatusLevel.INFO),

    /**
     * 即将弃用（在 since 和 until 之间）
     */
    PENDING_DEPRECATED("即将弃用", "PENDING_DEPRECATED", StatusLevel.WARNING),

    /**
     * 已弃用（在 until 日期之后）
     */
    DEPRECATED("已弃用", "DEPRECATED", StatusLevel.ERROR);

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 状态码
     */
    private final String code;

    /**
     * 状态级别
     */
    private final StatusLevel level;

    DeprecationStatus(String description, String code, StatusLevel level) {
        this.description = description;
        this.code = code;
        this.level = level;
    }

    /**
     * 获取状态描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取状态码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取状态级别
     */
    public StatusLevel getLevel() {
        return level;
    }

    /**
     * 状态级别枚举
     */
    public enum StatusLevel {
        /**
         * 信息
         */
        INFO("信息", "INFO"),
        /**
         * 警告
         */
        WARNING("警告", "WARNING"),
        /**
         * 错误
         */
        ERROR("错误", "ERROR");

        private final String description;
        private final String code;

        StatusLevel(String description, String code) {
            this.description = description;
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public String getCode() {
            return code;
        }
    }
}
