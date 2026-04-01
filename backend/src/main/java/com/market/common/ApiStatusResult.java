package com.market.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API 可用性状态响应
 * 
 * 参考系统消息实体设计：
 * - SystemMessage: 标题、内容、类型、优先级
 * - UserNotification: 级别、业务类型、业务 ID
 * - ChatMessage: 状态码
 */
@Data
@NoArgsConstructor
public class ApiStatusResult implements Serializable {

    /**
     * 状态码（参考 ChatMessage.status）
     */
    @JsonProperty("status_code")
    private Integer statusCode;

    /**
     * 状态描述
     */
    @JsonProperty("status_message")
    private String statusMessage;

    /**
     * 消息级别（参考 UserNotification.level）
     */
    @JsonProperty("level")
    private ApiStatusCode.Level level;

    /**
     * 服务名称
     */
    @JsonProperty("service_name")
    private String serviceName;

    /**
     * 接口路径
     */
    @JsonProperty("endpoint")
    private String endpoint;

    /**
     * 依赖服务状态
     */
    @JsonProperty("dependencies")
    private DependencyStatus[] dependencies;

    /**
     * 响应时间（毫秒）
     */
    @JsonProperty("response_time_ms")
    private Long responseTimeMs;

    /**
     * 检测时间
     */
    @JsonProperty("check_time")
    private LocalDateTime checkTime;

    /**
     * 检测时间戳（毫秒）
     */
    @JsonProperty("check_time_ms")
    private Long checkTimeMs;

    /**
     * 设置检测时间戳
     */
    public void setCheckTimeMs(Long checkTimeMs) {
        this.checkTimeMs = checkTimeMs;
    }

    /**
     * 详细信息
     */
    @JsonProperty("details")
    private Object details;

    /**
     * 建议操作
     */
    @JsonProperty("suggestion")
    private String suggestion;

    /**
     * 构造函数
     */
    public ApiStatusResult(ApiStatusCode statusCode, String serviceName) {
        this.statusCode = statusCode.getCode();
        this.statusMessage = statusCode.getMessage();
        this.level = statusCode.getLevel();
        this.serviceName = serviceName;
        this.checkTime = LocalDateTime.now();
    }

    /**
     * 创建成功状态
     */
    public static ApiStatusResult success(String serviceName) {
        return new ApiStatusResult(ApiStatusCode.AVAILABLE, serviceName);
    }

    /**
     * 创建成功状态（带数据）
     */
    public static ApiStatusResult success(String serviceName, Object details) {
        ApiStatusResult result = new ApiStatusResult(ApiStatusCode.AVAILABLE, serviceName);
        result.details = details;
        return result;
    }

    /**
     * 创建降级状态
     */
    public static ApiStatusResult degraded(String serviceName, String message) {
        ApiStatusResult result = new ApiStatusResult(ApiStatusCode.AVAILABLE_DEGRADED, serviceName);
        result.statusMessage = message;
        return result;
    }

    /**
     * 创建超时状态
     */
    public static ApiStatusResult timeout(String serviceName) {
        return new ApiStatusResult(ApiStatusCode.TIMEOUT, serviceName);
    }

    /**
     * 创建依赖不可用状态
     */
    public static ApiStatusResult dependencyUnavailable(String serviceName, String dependency) {
        ApiStatusResult result = new ApiStatusResult(ApiStatusCode.DEPENDENCY_UNAVAILABLE, serviceName);
        result.statusMessage = "依赖服务 [" + dependency + "] 不可用";
        return result;
    }

    /**
     * 创建数据库不可用状态
     */
    public static ApiStatusResult databaseUnavailable(String serviceName) {
        return new ApiStatusResult(ApiStatusCode.DATABASE_UNAVAILABLE, serviceName);
    }

    /**
     * 创建 Redis 不可用状态
     */
    public static ApiStatusResult redisUnavailable(String serviceName) {
        return new ApiStatusResult(ApiStatusCode.REDIS_UNAVAILABLE, serviceName);
    }

    /**
     * 创建服务错误状态
     */
    public static ApiStatusResult serviceError(String serviceName, String message) {
        ApiStatusResult result = new ApiStatusResult(ApiStatusCode.SERVICE_ERROR, serviceName);
        result.statusMessage = message;
        return result;
    }

    /**
     * 依赖服务状态
     */
    @Data
    @NoArgsConstructor
    public static class DependencyStatus implements Serializable {
        
        /**
         * 依赖服务名称
         */
        @JsonProperty("name")
        private String name;
        
        /**
         * 是否可用
         */
        @JsonProperty("available")
        private boolean available;
        
        /**
         * 状态码
         */
        @JsonProperty("status_code")
        private Integer statusCode;
        
        /**
         * 状态消息
         */
        @JsonProperty("status_message")
        private String statusMessage;
        
        /**
         * 响应时间（毫秒）
         */
        @JsonProperty("response_time_ms")
        private Long responseTimeMs;

        public DependencyStatus(String name, boolean available) {
            this.name = name;
            this.available = available;
            this.statusCode = available ? ApiStatusCode.AVAILABLE.getCode() : ApiStatusCode.UNAVAILABLE.getCode();
            this.statusMessage = available ? "可用" : "不可用";
        }
    }
}
