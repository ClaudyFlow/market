package com.market.common;

import lombok.Getter;

/**
 * API 可用性状态码
 * 
 * 参考系统消息状态设计：
 * - ChatMessage.status: 1000-发送中，2000-已发送，3000-已送达，4000-已读，5000-失败
 * - UserNotification.level: INFO, WARNING, URGENT
 * - SystemMessage.priority: 1-5
 */
@Getter
public enum ApiStatusCode {

    // ==================== 成功状态 (2000-2999) ====================
    /**
     * 服务可用
     */
    AVAILABLE(2000, "服务可用", Level.INFO),
    
    /**
     * 服务可用（降级模式）
     */
    AVAILABLE_DEGRADED(2001, "服务可用（降级模式）", Level.INFO),
    
    /**
     * 服务可用（部分功能受限）
     */
    AVAILABLE_LIMITED(2002, "服务可用（部分功能受限）", Level.WARNING),

    // ==================== 处理中状态 (3000-3999) ====================
    /**
     * 正在检测
     */
    CHECKING(3000, "正在检测", Level.INFO),
    
    /**
     * 正在重试
     */
    RETRYING(3100, "正在重试", Level.INFO),
    
    /**
     * 等待依赖服务
     */
    WAITING_DEPENDENCY(3200, "等待依赖服务", Level.INFO),

    // ==================== 失败状态 (4000-4999) ====================
    /**
     * 服务不可用
     */
    UNAVAILABLE(4000, "服务不可用", Level.URGENT),
    
    /**
     * 服务超时
     */
    TIMEOUT(4001, "服务超时", Level.URGENT),
    
    /**
     * 依赖服务不可用
     */
    DEPENDENCY_UNAVAILABLE(4100, "依赖服务不可用", Level.URGENT),
    
    /**
     * 数据库不可用
     */
    DATABASE_UNAVAILABLE(4101, "数据库不可用", Level.URGENT),
    
    /**
     * Redis 不可用
     */
    REDIS_UNAVAILABLE(4102, "Redis 不可用", Level.URGENT),
    
    /**
     * 外部 API 不可用
     */
    EXTERNAL_API_UNAVAILABLE(4103, "外部 API 不可用", Level.URGENT),
    
    /**
     * 检测器执行失败
     */
    DETECTOR_ERROR(4200, "检测器执行失败", Level.URGENT),
    
    /**
     * 服务异常
     */
    SERVICE_ERROR(4300, "服务异常", Level.URGENT),
    
    /**
     * 连续失败超过阈值
     */
    CONTINUOUS_FAILURE(4400, "连续失败超过阈值", Level.URGENT),

    // ==================== 未知状态 (5000) ====================
    /**
     * 状态未知
     */
    UNKNOWN(5000, "状态未知", Level.WARNING);

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态描述
     */
    private final String message;

    /**
     * 消息级别
     */
    private final Level level;

    ApiStatusCode(int code, String message, Level level) {
        this.code = code;
        this.message = message;
        this.level = level;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Level getLevel() {
        return level;
    }

    /**
     * 根据状态码获取枚举
     */
    public static ApiStatusCode fromCode(int code) {
        for (ApiStatusCode status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return UNKNOWN;
    }

    /**
     * 判断是否为成功状态
     */
    public boolean isSuccess() {
        return code >= 2000 && code < 3000;
    }

    /**
     * 判断是否为处理中状态
     */
    public boolean isProcessing() {
        return code >= 3000 && code < 4000;
    }

    /**
     * 判断是否为失败状态
     */
    public boolean isError() {
        return code >= 4000 && code < 5000;
    }

    /**
     * 消息级别枚举
     * 参考 UserNotification.level
     */
    public enum Level {
        /**
         * 普通信息
         */
        INFO("普通"),
        /**
         * 重要警告
         */
        WARNING("重要"),
        /**
         * 紧急错误
         */
        URGENT("紧急");

        private final String description;

        Level(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
