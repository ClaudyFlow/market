package com.market.aspect;

import com.market.annotation.ApiAvailable;
import com.market.annotation.ApiAvailabilityDetector;
import com.market.annotation.ApiHealthCheck;
import com.market.annotation.DatabaseAvailabilityDetector;
import com.market.annotation.RedisAvailabilityDetector;
import com.market.common.ApiStatusResult;
import com.market.common.ApiStatusCode;
import com.market.common.Result;
import com.market.exception.ApiAvailabilityException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * API 可用性检测切面
 *
 * 拦截带有 @ApiAvailable 注解的方法，执行可用性检测
 * 整合系统消息状态库，提供统一的状态响应
 */
@Slf4j
@Aspect
@Component
public class ApiAvailabilityAspect {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private DatabaseAvailabilityDetector databaseDetector;

    @Autowired(required = false)
    private RedisAvailabilityDetector redisDetector;

    /**
     * 服务健康状态缓存
     */
    private final Map<String, ServiceHealthStatus> healthStatusCache = new ConcurrentHashMap<>();

    /**
     * 围绕带有 @ApiAvailable 注解的方法执行
     */
    @Around("@annotation(com.market.annotation.ApiAvailable)")
    public Object checkAvailability(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String serviceName = getServiceName(joinPoint);
        long startTime = System.currentTimeMillis();

        // 获取注解
        ApiAvailable apiAvailable = method.getAnnotation(ApiAvailable.class);

        // 检查是否启用
        if (!apiAvailable.enabled()) {
            return joinPoint.proceed();
        }

        // 执行可用性检测
        DetectionContext context = new DetectionContext(apiAvailable, serviceName);

        try {
            if (!performDetection(context)) {
                return handleFailure(joinPoint, apiAvailable, context, startTime);
            }

            // 执行目标方法（带超时控制）
            Object result = executeWithTimeout(joinPoint, apiAvailable.timeout(), serviceName);
            
            // 记录成功日志
            long responseTime = System.currentTimeMillis() - startTime;
            log.info("API 可用性检测通过：{} - 响应时间：{}ms", serviceName, responseTime);
            
            // 更新健康状态
            updateHealthStatus(serviceName, true, responseTime);
            
            return result;
            
        } catch (ApiAvailabilityException e) {
            throw e;
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.error("API 执行异常：{} - 错误：{}", serviceName, e.getMessage(), e);
            updateHealthStatus(serviceName, false, responseTime);
            
            if (apiAvailable.onFailure() == ApiAvailable.FailureAction.THROW) {
                throw new ApiAvailabilityException(ApiStatusCode.SERVICE_ERROR, e.getMessage(), 
                    serviceName, null, responseTime, e);
            }
            return createErrorResponse(apiAvailable, ApiStatusCode.SERVICE_ERROR, e.getMessage());
        }
    }

    /**
     * 执行可用性检测
     */
    private boolean performDetection(DetectionContext context) {
        ApiAvailable config = context.config;

        // 检查依赖服务
        for (String dependency : config.dependencies()) {
            long depStartTime = System.currentTimeMillis();
            boolean available = checkDependency(dependency);
            long depResponseTime = System.currentTimeMillis() - depStartTime;
            
            context.dependencyResults.put(dependency, available);
            context.dependencyResponseTimes.put(dependency, depResponseTime);

            if (!available) {
                context.failedDependency = dependency;
                if (isCritical(dependency)) {
                    log.warn("依赖服务 [{}] 不可用：{}", dependency, context.serviceName);
                    return false;
                }
            }
        }

        // 执行自定义检测器
        if (config.detector() != Void.class) {
            try {
                ApiAvailabilityDetector detector =
                    (ApiAvailabilityDetector) applicationContext.getBean(config.detector());
                ApiAvailabilityDetector.DetectionResult result = detector.detect();
                if (!result.isAvailable()) {
                    context.errorMessage = result.getMessage();
                    context.statusCode = ApiStatusCode.DETECTOR_ERROR;
                    return false;
                }
            } catch (Exception e) {
                log.error("自定义检测器执行失败：{}", config.detector().getName(), e);
                context.errorMessage = "检测器执行失败：" + e.getMessage();
                context.statusCode = ApiStatusCode.DETECTOR_ERROR;
                return false;
            }
        }

        return true;
    }

    /**
     * 检查依赖服务
     */
    private boolean checkDependency(String dependency) {
        return switch (dependency.toLowerCase()) {
            case "database" -> databaseDetector != null &&
                              databaseDetector.detect().isAvailable();
            case "redis" -> redisDetector != null &&
                           redisDetector.detect().isAvailable();
            default -> {
                // 尝试从 Spring 容器中获取自定义检测器
                try {
                    ApiAvailabilityDetector detector =
                        applicationContext.getBean(dependency + "AvailabilityDetector",
                                                   ApiAvailabilityDetector.class);
                    yield detector.detect().isAvailable();
                } catch (Exception e) {
                    log.warn("未找到依赖服务 [{}] 的检测器", dependency);
                    yield true; // 默认认为可用
                }
            }
        };
    }

    /**
     * 判断依赖是否为关键依赖
     */
    private boolean isCritical(String dependency) {
        // 检查是否有 @ApiHealthCheck 配置
        // 默认所有依赖都是关键的
        return true;
    }

    /**
     * 处理检测失败
     */
    private Object handleFailure(ProceedingJoinPoint joinPoint,
                                  ApiAvailable config,
                                  DetectionContext context,
                                  long startTime) throws Throwable {
        ApiAvailable.FailureAction action = config.onFailure();
        long responseTime = System.currentTimeMillis() - startTime;

        // 确定状态码
        ApiStatusCode statusCode = determineStatusCode(context);

        // 构建错误消息
        String errorMsg = buildErrorMessage(context, statusCode);

        log.error("API 可用性检测失败：{} - 状态码：{} - {}", 
            context.serviceName, statusCode.getCode(), errorMsg);

        // 更新健康状态
        updateHealthStatus(context.serviceName, false, responseTime);

        return switch (action) {
            case THROW -> {
                ApiAvailabilityException exception = new ApiAvailabilityException(
                    statusCode, errorMsg, context.serviceName, 
                    context.failedDependency, responseTime);
                throw exception;
            }
            case RETURN_ERROR -> createErrorResponse(config, statusCode, errorMsg);
            case CONTINUE -> {
                log.warn("继续执行方法（降级模式）：{}", context.serviceName);
                yield joinPoint.proceed();
            }
        };
    }

    /**
     * 确定状态码
     */
    private ApiStatusCode determineStatusCode(DetectionContext context) {
        if (context.failedDependency != null) {
            return switch (context.failedDependency.toLowerCase()) {
                case "database" -> ApiStatusCode.DATABASE_UNAVAILABLE;
                case "redis" -> ApiStatusCode.REDIS_UNAVAILABLE;
                default -> ApiStatusCode.DEPENDENCY_UNAVAILABLE;
            };
        }
        if (context.statusCode != null) {
            return context.statusCode;
        }
        if (context.errorMessage != null) {
            return ApiStatusCode.SERVICE_ERROR;
        }
        return ApiStatusCode.UNAVAILABLE;
    }

    /**
     * 构建错误消息
     */
    private String buildErrorMessage(DetectionContext context, ApiStatusCode statusCode) {
        if (context.failedDependency != null) {
            return "依赖服务 [" + context.failedDependency + "] 不可用";
        }
        if (context.errorMessage != null) {
            return context.errorMessage;
        }
        return statusCode.getMessage();
    }

    /**
     * 创建错误响应
     */
    private Result<Object> createErrorResponse(ApiAvailable config, ApiStatusCode statusCode, String errorMsg) {
        return Result.error(statusCode.getCode(), errorMsg);
    }

    /**
     * 带超时控制执行目标方法
     */
    private Object executeWithTimeout(ProceedingJoinPoint joinPoint, long timeoutMs, String serviceName) throws Throwable {
        final Object[] result = new Object[1];
        final Throwable[] error = new Throwable[1];

        Thread workerThread = new Thread(() -> {
            try {
                result[0] = joinPoint.proceed();
            } catch (Throwable e) {
                error[0] = e;
            }
        });

        workerThread.start();
        workerThread.join(timeoutMs);

        if (workerThread.isAlive()) {
            workerThread.interrupt();
            String msg = String.format("方法执行超时（超过 %dms）", timeoutMs);
            log.error(msg);
            throw new ApiAvailabilityException(ApiStatusCode.TIMEOUT, msg, serviceName, null, timeoutMs);
        }

        if (error[0] != null) {
            throw error[0];
        }

        return result[0];
    }

    /**
     * 获取服务名称
     */
    private String getServiceName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringTypeName() + "." + signature.getName();
    }

    /**
     * 更新健康状态
     */
    private void updateHealthStatus(String serviceName, boolean healthy, long responseTime) {
        ServiceHealthStatus status = healthStatusCache.computeIfAbsent(serviceName, k -> new ServiceHealthStatus());
        status.update(healthy, responseTime);
    }

    /**
     * 获取服务健康状态
     */
    public ApiStatusResult getServiceHealth(String serviceName) {
        ServiceHealthStatus status = healthStatusCache.get(serviceName);
        if (status == null) {
            return ApiStatusResult.success(serviceName);
        }

        ApiStatusResult result = new ApiStatusResult(
            status.healthy ? ApiStatusCode.AVAILABLE : ApiStatusCode.UNAVAILABLE,
            serviceName
        );
        result.setResponseTimeMs(status.lastResponseTime);
        result.setCheckTimeMs(status.lastCheckTime);

        return result;
    }

    /**
     * 检测上下文
     */
    private static class DetectionContext {
        final ApiAvailable config;
        final String serviceName;
        final Map<String, Boolean> dependencyResults = new HashMap<>();
        final Map<String, Long> dependencyResponseTimes = new HashMap<>();
        String failedDependency;
        String errorMessage;
        ApiStatusCode statusCode;

        DetectionContext(ApiAvailable config, String serviceName) {
            this.config = config;
            this.serviceName = serviceName;
        }
    }

    /**
     * 服务健康状态
     */
    private static class ServiceHealthStatus {
        boolean healthy;
        long lastCheckTime;
        long lastResponseTime;
        int consecutiveFailures;
        int consecutiveSuccesses;

        ServiceHealthStatus() {
            this.healthy = true;
            this.lastCheckTime = System.currentTimeMillis();
            this.consecutiveFailures = 0;
            this.consecutiveSuccesses = 0;
        }

        synchronized void update(boolean healthy, long responseTime) {
            this.healthy = healthy;
            this.lastCheckTime = System.currentTimeMillis();
            this.lastResponseTime = responseTime;

            if (healthy) {
                this.consecutiveSuccesses++;
                this.consecutiveFailures = 0;
            } else {
                this.consecutiveFailures++;
                this.consecutiveSuccesses = 0;
            }
        }
    }
}
