package com.market.aspect;

import com.market.annotation.Retryable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 重试切面
 */
@Aspect
@Component
public class RetryableAspect {
    
    private static final Logger log = LoggerFactory.getLogger(RetryableAspect.class);

    /**
     * 围绕带有 @Retryable 注解的方法执行
     */
    @Around("@annotation(com.market.annotation.Retryable)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        Retryable retryable = method.getAnnotation(Retryable.class);

        // 执行重试逻辑
        return executeWithRetry(joinPoint, retryable);
    }

    /**
     * 执行重试逻辑
     */
    private Object executeWithRetry(ProceedingJoinPoint joinPoint, Retryable retryable) throws Throwable {
        int maxAttempts = retryable.maxAttempts();
        long delay = retryable.timeUnit().toMillis(retryable.delay());
        double multiplier = retryable.multiplier();
        long maxDelay = retryable.timeUnit().toMillis(retryable.maxDelay());
        String methodName = joinPoint.getSignature().toShortString();

        Throwable lastException = null;
        long currentDelay = delay;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                // 执行方法
                Object result = joinPoint.proceed();

                // 成功则返回
                if (attempt > 1 && retryable.logEnabled()) {
                    log.info("[重试成功] {} - 第 {} 次尝试成功", methodName, attempt);
                }
                return result;

            } catch (Throwable throwable) {
                lastException = throwable;

                // 检查是否应该重试
                if (!shouldRetry(throwable, retryable)) {
                    if (retryable.logEnabled()) {
                        log.warn("[不重试] {} - 异常类型不在重试范围内：{}",
                            methodName, throwable.getClass().getName());
                    }
                    throw throwable;
                }

                // 检查是否达到最大尝试次数
                if (attempt >= maxAttempts) {
                    if (retryable.logEnabled()) {
                        log.error("[重试耗尽] {} - 已达到最大重试次数 {}", methodName, maxAttempts);
                    }
                    break;
                }

                // 检查自定义条件
                if (!checkCondition(throwable, attempt, retryable)) {
                    if (retryable.logEnabled()) {
                        log.warn("[条件不满足] {} - 不满足重试条件", methodName);
                    }
                    throw throwable;
                }

                // 记录重试日志
                if (retryable.logEnabled()) {
                    log.warn("[重试执行] {} - 第 {} 次失败，将在 {}ms 后重试。错误：{}",
                        methodName, attempt, currentDelay, throwable.getMessage());
                }

                // 等待延迟时间
                try {
                    Thread.sleep(currentDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw throwable;
                }

                // 计算下一次延迟（指数退避）
                currentDelay = (long) Math.min(currentDelay * multiplier, maxDelay);
            }
        }

        // 所有重试失败，根据失败处理器处理
        return handleFailure(joinPoint, retryable, lastException);
    }

    /**
     * 判断是否应该重试
     */
    private boolean shouldRetry(Throwable throwable, Retryable retryable) {
        Class<? extends Throwable>[] includes = retryable.include();
        Class<? extends Throwable>[] excludes = retryable.exclude();

        // 检查排除的异常
        for (Class<? extends Throwable> excludeClass : excludes) {
            if (excludeClass.isInstance(throwable)) {
                return false;
            }
        }

        // 检查包含的异常
        for (Class<? extends Throwable> includeClass : includes) {
            if (includeClass.isInstance(throwable)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查自定义条件
     */
    private boolean checkCondition(Throwable throwable, int attempt, Retryable retryable) {
        String condition = retryable.condition();
        if (condition == null || condition.trim().isEmpty()) {
            return true;
        }

        // 简单的条件检查（支持 #attempt, #exception 变量）
        if (condition.contains("#attempt")) {
            condition = condition.replace("#attempt", String.valueOf(attempt));
        }
        if (condition.contains("#exception")) {
            // 简单处理，实际可以使用 SpEL
            condition = condition.replace("#exception", throwable.getClass().getSimpleName());
        }

        try {
            // 支持简单的比较表达式
            if (condition.contains("<=") || condition.contains(">=") ||
                condition.contains("<") || condition.contains(">") ||
                condition.contains("==") || condition.contains("!=")) {
                return evaluateSimpleCondition(condition);
            }
            return Boolean.parseBoolean(condition);
        } catch (Exception e) {
            log.warn("条件表达式解析失败：{}", condition, e);
            return true;
        }
    }

    /**
     * 评估简单条件
     */
    private boolean evaluateSimpleCondition(String condition) {
        try {
            if (condition.contains("<=")) {
                String[] parts = condition.split("<=");
                int left = Integer.parseInt(parts[0].trim());
                int right = Integer.parseInt(parts[1].trim());
                return left <= right;
            } else if (condition.contains(">=")) {
                String[] parts = condition.split(">=");
                int left = Integer.parseInt(parts[0].trim());
                int right = Integer.parseInt(parts[1].trim());
                return left >= right;
            } else if (condition.contains("<")) {
                String[] parts = condition.split("<");
                int left = Integer.parseInt(parts[0].trim());
                int right = Integer.parseInt(parts[1].trim());
                return left < right;
            } else if (condition.contains(">")) {
                String[] parts = condition.split(">");
                int left = Integer.parseInt(parts[0].trim());
                int right = Integer.parseInt(parts[1].trim());
                return left > right;
            } else if (condition.contains("==")) {
                String[] parts = condition.split("==");
                return parts[0].trim().equals(parts[1].trim());
            } else if (condition.contains("!=")) {
                String[] parts = condition.split("!=");
                return !parts[0].trim().equals(parts[1].trim());
            }
        } catch (Exception e) {
            log.warn("简单条件评估失败：{}", condition, e);
        }
        return true;
    }

    /**
     * 处理失败
     */
    private Object handleFailure(ProceedingJoinPoint joinPoint, Retryable retryable, Throwable lastException) throws Throwable {
        Retryable.FailureHandler handler = retryable.failureHandler();

        switch (handler) {
            case THROW:
                if (lastException != null) {
                    throw lastException;
                }
                return null;

            case RETURN_NULL:
                log.info("[失败处理] 返回 null");
                return null;

            case RETURN_DEFAULT:
                return invokeDefaultMethod(joinPoint, retryable);

            default:
                if (lastException != null) {
                    throw lastException;
                }
                return null;
        }
    }

    /**
     * 调用默认值方法
     */
    private Object invokeDefaultMethod(ProceedingJoinPoint joinPoint, Retryable retryable) {
        String defaultMethod = retryable.defaultMethod();
        if (defaultMethod == null || defaultMethod.trim().isEmpty()) {
            log.warn("[默认方法] 未指定 defaultMethod，返回 null");
            return null;
        }

        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Class<?> targetClass = joinPoint.getTarget().getClass();
            Method method = targetClass.getMethod(defaultMethod, signature.getParameterTypes());

            Object result = method.invoke(joinPoint.getTarget(), joinPoint.getArgs());
            log.info("[默认方法] 调用 {} 成功", defaultMethod);
            return result;

        } catch (Exception e) {
            log.error("[默认方法] 调用失败：{}", defaultMethod, e);
            return null;
        }
    }

    /**
     * 获取重试统计信息
     */
    public RetryStats getRetryStats() {
        return new RetryStats();
    }

    /**
     * 重试统计信息
     */
    public static class RetryStats {
        private final long totalRetries = 0;
        private final long successfulRetries = 0;

        public long getTotalRetries() {
            return totalRetries;
        }

        public long getSuccessfulRetries() {
            return successfulRetries;
        }
    }
}
