package com.market.exception;

import com.market.common.ApiStatusCode;

/**
 * API 可用性异常
 * 
 * 当 API 可用性检测失败时抛出此异常
 * 
 * @author Market Team
 * @since 1.0.0
 */
public class ApiAvailabilityException extends RuntimeException {

    /**
     * 状态码
     */
    private final ApiStatusCode statusCode;

    /**
     * 服务名称
     */
    private final String serviceName;

    /**
     * 依赖服务
     */
    private final String dependency;

    /**
     * 响应时间（毫秒）
     */
    private final Long responseTimeMs;

    /**
     * 构造异常（使用默认状态码）
     */
    public ApiAvailabilityException(String message) {
        super(message);
        this.statusCode = ApiStatusCode.UNAVAILABLE;
        this.serviceName = null;
        this.dependency = null;
        this.responseTimeMs = null;
    }

    /**
     * 构造异常（带服务名称）
     */
    public ApiAvailabilityException(String message, String serviceName) {
        super(message);
        this.statusCode = ApiStatusCode.UNAVAILABLE;
        this.serviceName = serviceName;
        this.dependency = null;
        this.responseTimeMs = null;
    }

    /**
     * 构造异常（带依赖服务）
     */
    public ApiAvailabilityException(String message, String serviceName, String dependency) {
        super(message);
        this.statusCode = ApiStatusCode.DEPENDENCY_UNAVAILABLE;
        this.serviceName = serviceName;
        this.dependency = dependency;
        this.responseTimeMs = null;
    }

    /**
     * 构造异常（带状态码）
     */
    public ApiAvailabilityException(ApiStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.serviceName = null;
        this.dependency = null;
        this.responseTimeMs = null;
    }

    /**
     * 构造异常（完整参数）
     */
    public ApiAvailabilityException(ApiStatusCode statusCode, String message, String serviceName, 
                                     String dependency, Long responseTimeMs) {
        super(message);
        this.statusCode = statusCode;
        this.serviceName = serviceName;
        this.dependency = dependency;
        this.responseTimeMs = responseTimeMs;
    }

    /**
     * 构造异常（带原因）
     */
    public ApiAvailabilityException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = ApiStatusCode.SERVICE_ERROR;
        this.serviceName = null;
        this.dependency = null;
        this.responseTimeMs = null;
    }

    /**
     * 构造异常（带服务名称和原因）
     */
    public ApiAvailabilityException(String message, String serviceName, Throwable cause) {
        super(message, cause);
        this.statusCode = ApiStatusCode.SERVICE_ERROR;
        this.serviceName = serviceName;
        this.dependency = null;
        this.responseTimeMs = null;
    }

    /**
     * 构造异常（完整参数带原因）
     */
    public ApiAvailabilityException(ApiStatusCode statusCode, String message, String serviceName,
                                     String dependency, Long responseTimeMs, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.serviceName = serviceName;
        this.dependency = dependency;
        this.responseTimeMs = responseTimeMs;
    }

    // Getters
    public ApiStatusCode getStatusCode() {
        return statusCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDependency() {
        return dependency;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    /**
     * 获取状态码
     */
    public int getCode() {
        return statusCode.getCode();
    }

    /**
     * 获取状态级别
     */
    public ApiStatusCode.Level getLevel() {
        return statusCode.getLevel();
    }
}
