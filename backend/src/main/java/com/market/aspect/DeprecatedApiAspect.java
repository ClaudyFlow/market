package com.market.aspect;

import com.market.annotation.DeprecatedApi;
import com.market.common.DeprecationStatus;
import com.market.exception.DeprecatedApiException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * API 弃用检测切面
 *
 * 拦截带有 @DeprecatedApi 注解的方法，根据日期判断弃用状态
 * - since 之前：显示"尚未弃用"
 * - since 和 until 之间：显示"即将弃用"
 * - until 之后：显示"已弃用"
 */
@Slf4j
@Aspect
@Component
public class DeprecatedApiAspect {

    private static final Logger log = LoggerFactory.getLogger(DeprecatedApiAspect.class);

    /**
     * 日期格式化器
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 围绕带有 @DeprecatedApi 注解的方法执行
     */
    @Around("@annotation(com.market.annotation.DeprecatedApi)")
    public Object checkDeprecation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解（优先获取方法上的注解，如果没有则获取类上的）
        DeprecatedApi deprecatedApi = method.getAnnotation(DeprecatedApi.class);
        if (deprecatedApi == null) {
            Class<?> declaringType = signature.getDeclaringType();
            if (declaringType != null) {
                deprecatedApi = declaringType.getAnnotation(DeprecatedApi.class);
            }
        }

        if (deprecatedApi == null) {
            return joinPoint.proceed();
        }

        // 解析日期
        LocalDate sinceDate = parseDate(deprecatedApi.since());
        LocalDate untilDate = parseDate(deprecatedApi.until());
        LocalDate currentDate = LocalDate.now();

        // 判断弃用状态
        DeprecationStatus status = determineDeprecationStatus(currentDate, sinceDate, untilDate);

        // 获取方法签名
        String methodSignature = signature.getDeclaringTypeName() + "." + signature.getName();

        // 根据状态处理
        switch (status) {
            case NOT_DEPRECATED:
                // 尚未弃用，仅记录日志
                log.info("[API 弃用] 方法 {} 尚未弃用 (since: {})",
                    methodSignature,
                    formatDate(sinceDate));
                return joinPoint.proceed();

            case PENDING_DEPRECATED:
                // 即将弃用，记录警告日志
                long daysUntilDeprecated = ChronoUnit.DAYS.between(currentDate, untilDate);
                String warningMsg = buildWarningMessage(deprecatedApi, methodSignature, daysUntilDeprecated);
                log.warn("[API 弃用] {}", warningMsg);
                return joinPoint.proceed();

            case DEPRECATED:
                // 已弃用
                String errorMsg = buildErrorMessage(deprecatedApi, methodSignature);
                log.error("[API 弃用] {}", errorMsg);

                if (deprecatedApi.throwException()) {
                    throw new DeprecatedApiException(
                        status,
                        deprecatedApi.message(),
                        sinceDate,
                        untilDate,
                        deprecatedApi.replacement(),
                        deprecatedApi.reason(),
                        methodSignature
                    );
                }
                return joinPoint.proceed();

            default:
                return joinPoint.proceed();
        }
    }

    /**
     * 解析日期字符串
     */
    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            log.error("日期解析失败：{}", dateString, e);
            return null;
        }
    }

    /**
     * 格式化日期
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "未设置";
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * 判断弃用状态
     */
    private DeprecationStatus determineDeprecationStatus(LocalDate currentDate,
                                                          LocalDate sinceDate,
                                                          LocalDate untilDate) {
        // 如果未设置 since 日期，默认已经弃用
        if (sinceDate == null) {
            return DeprecationStatus.DEPRECATED;
        }

        // 在 since 之前：尚未弃用
        if (currentDate.isBefore(sinceDate)) {
            return DeprecationStatus.NOT_DEPRECATED;
        }

        // 如果未设置 until 日期，在 since 之后即为已弃用
        if (untilDate == null) {
            return DeprecationStatus.DEPRECATED;
        }

        // 在 since 和 until 之间：即将弃用
        if (!currentDate.isAfter(untilDate)) {
            return DeprecationStatus.PENDING_DEPRECATED;
        }

        // 在 until 之后：已弃用
        return DeprecationStatus.DEPRECATED;
    }

    /**
     * 构建警告消息（即将弃用）
     */
    private String buildWarningMessage(DeprecatedApi deprecatedApi,
                                        String methodSignature,
                                        long daysUntilDeprecated) {
        StringBuilder sb = new StringBuilder();
        sb.append("方法 ").append(methodSignature).append(" 即将弃用");
        sb.append(" [").append(DeprecationStatus.PENDING_DEPRECATED.getDescription()).append("]");
        sb.append("，剩余 ").append(daysUntilDeprecated).append(" 天");

        if (deprecatedApi.since() != null && !deprecatedApi.since().isEmpty()) {
            sb.append(" (since: ").append(deprecatedApi.since()).append(")");
        }
        if (deprecatedApi.until() != null && !deprecatedApi.until().isEmpty()) {
            sb.append(" (until: ").append(deprecatedApi.until()).append(")");
        }
        if (deprecatedApi.reason() != null && !deprecatedApi.reason().isEmpty()) {
            sb.append("，原因：").append(deprecatedApi.reason());
        }
        if (deprecatedApi.replacement() != null && deprecatedApi.replacement().length > 0) {
            sb.append("，请使用：").append(String.join(", ", deprecatedApi.replacement()));
        }

        return sb.toString();
    }

    /**
     * 构建错误消息（已弃用）
     */
    private String buildErrorMessage(DeprecatedApi deprecatedApi, String methodSignature) {
        StringBuilder sb = new StringBuilder();
        sb.append("方法 ").append(methodSignature).append(" 已弃用");
        sb.append(" [").append(DeprecationStatus.DEPRECATED.getDescription()).append("]");

        if (deprecatedApi.until() != null && !deprecatedApi.until().isEmpty()) {
            sb.append(" (until: ").append(deprecatedApi.until()).append(")");
        }
        if (deprecatedApi.reason() != null && !deprecatedApi.reason().isEmpty()) {
            sb.append("，原因：").append(deprecatedApi.reason());
        }
        if (deprecatedApi.replacement() != null && deprecatedApi.replacement().length > 0) {
            sb.append("，替代方案：").append(String.join(", ", deprecatedApi.replacement()));
        }

        return sb.toString();
    }

    /**
     * 获取 API 弃用状态信息（用于外部查询）
     */
    public DeprecationInfo getDeprecationInfo(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName);
            DeprecatedApi deprecatedApi = method.getAnnotation(DeprecatedApi.class);

            if (deprecatedApi == null) {
                return null;
            }

            LocalDate sinceDate = parseDate(deprecatedApi.since());
            LocalDate untilDate = parseDate(deprecatedApi.until());
            LocalDate currentDate = LocalDate.now();
            DeprecationStatus status = determineDeprecationStatus(currentDate, sinceDate, untilDate);

            return new DeprecationInfo(
                status,
                sinceDate,
                untilDate,
                deprecatedApi.replacement(),
                deprecatedApi.reason(),
                deprecatedApi.message()
            );
        } catch (Exception e) {
            log.error("获取弃用信息失败：{}.{}", className, methodName, e);
            return null;
        }
    }

    /**
     * 弃用信息 DTO
     */
    @lombok.Data
    public static class DeprecationInfo {
        private DeprecationStatus status;
        private LocalDate sinceDate;
        private LocalDate untilDate;
        private String[] replacement;
        private String reason;
        private String message;

        /**
         * 构造函数
         */
        public DeprecationInfo(DeprecationStatus status,
                               LocalDate sinceDate,
                               LocalDate untilDate,
                               String[] replacement,
                               String reason,
                               String message) {
            this.status = status;
            this.sinceDate = sinceDate;
            this.untilDate = untilDate;
            this.replacement = replacement;
            this.reason = reason;
            this.message = message;
        }

        /**
         * 获取剩余天数（即将弃用时）
         */
        public Long getDaysUntilDeprecated() {
            if (status != DeprecationStatus.PENDING_DEPRECATED || untilDate == null) {
                return null;
            }
            return ChronoUnit.DAYS.between(LocalDate.now(), untilDate);
        }

        /**
         * 获取已弃用天数（已弃用时）
         */
        public Long getDaysSinceDeprecated() {
            if (status != DeprecationStatus.DEPRECATED || untilDate == null) {
                return null;
            }
            return ChronoUnit.DAYS.between(untilDate, LocalDate.now());
        }
    }
}
