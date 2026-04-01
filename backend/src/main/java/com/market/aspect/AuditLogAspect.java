package com.market.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.annotation.AuditLog;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 审计日志切面
 *
 * 拦截带有 @AuditLog 注解的方法，记录操作审计日志
 * 支持异步记录、SpEL 表达式、参数/结果记录
 */
@Slf4j
@Aspect
@Component
public class AuditLogAspect {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT_LOG");
    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    /**
     * JSON 序列化器
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * SpEL 表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 异步执行线程池
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     * 审计日志存储（实际项目中应该存入数据库）
     */
    private final Map<String, AuditLogEntry> logStore = new ConcurrentHashMap<>();

    /**
     * 围绕带有 @AuditLog 注解的方法执行
     */
    @Around("@annotation(com.market.annotation.AuditLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        AuditLog auditLogConfig = method.getAnnotation(AuditLog.class);

        // 获取请求信息
        HttpServletRequest request = getRequest();

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 构建审计日志条目
        AuditLogEntry logEntry = buildLogEntry(joinPoint, auditLogConfig, request);

        Object result = null;
        Throwable exception = null;

        try {
            // 执行方法
            result = joinPoint.proceed();
            logEntry.status = AuditLog.OperationStatus.SUCCESS;
            return result;

        } catch (Throwable e) {
            exception = e;
            logEntry.status = AuditLog.OperationStatus.FAILURE;
            logEntry.errorMessage = e.getMessage();

            // 检查是否忽略该异常
            if (shouldIgnoreException(e, auditLogConfig)) {
                throw e;
            }
            throw e;

        } finally {
            // 记录响应时间
            long responseTime = System.currentTimeMillis() - startTime;
            logEntry.responseTimeMs = responseTime;

            // 填充额外信息
            fillLogEntry(logEntry, result, exception, auditLogConfig, joinPoint);

            // 记录日志
            recordLog(logEntry, auditLogConfig);
        }
    }

    /**
     * 构建日志条目基础信息
     */
    private AuditLogEntry buildLogEntry(ProceedingJoinPoint joinPoint,
                                         AuditLog auditLogConfig,
                                         HttpServletRequest request) {
        AuditLogEntry entry = new AuditLogEntry();
        entry.id = generateLogId();
        entry.timestamp = LocalDateTime.now();

        // 基本信息
        entry.module = auditLogConfig.module();
        entry.action = auditLogConfig.action();
        entry.description = evaluateDescription(joinPoint, auditLogConfig);

        // 请求信息
        if (request != null) {
            entry.ipAddress = request.getRemoteAddr();
            entry.userAgent = request.getHeader("User-Agent");
            entry.requestUri = request.getRequestURI();
            entry.requestMethod = request.getMethod();
        }

        // 方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        entry.className = signature.getDeclaringTypeName();
        entry.methodName = signature.getName();

        // 操作人信息
        entry.operatorId = evaluateExpression(auditLogConfig.operatorId(), joinPoint, null);
        entry.operatorName = evaluateExpression(auditLogConfig.operatorName(), joinPoint, null);

        // 业务信息
        entry.businessId = evaluateExpression(auditLogConfig.businessId(), joinPoint, null);
        entry.businessType = auditLogConfig.businessType();

        // 配置信息
        entry.logLevel = auditLogConfig.logLevel();
        entry.recordParams = auditLogConfig.recordParams();
        entry.recordResult = auditLogConfig.recordResult();

        return entry;
    }

    /**
     * 填充日志条目额外信息
     */
    private void fillLogEntry(AuditLogEntry entry, Object result, Throwable exception,
                               AuditLog auditLogConfig, ProceedingJoinPoint joinPoint) {
        // 记录参数
        if (entry.recordParams) {
            entry.params = paramsToJson(joinPoint.getArgs());
        }

        // 记录结果
        if (entry.recordResult && result != null) {
            entry.result = objectToJson(result);
        }

        // 记录错误信息
        if (exception != null) {
            entry.errorType = exception.getClass().getName();
        }
    }

    /**
     * 记录日志
     */
    private void recordLog(AuditLogEntry logEntry, AuditLog auditLogConfig) {
        Runnable logTask = () -> {
            // 存储到内存
            logStore.put(logEntry.id, logEntry);

            // 根据日志级别记录
            String logMessage = formatLogMessage(logEntry);

            switch (auditLogConfig.logLevel()) {
                case DEBUG:
                    auditLog.debug(logMessage);
                    break;
                case INFO:
                    auditLog.info(logMessage);
                    break;
                case WARNING:
                    auditLog.warn(logMessage);
                    break;
                case ERROR:
                    auditLog.error(logMessage);
                    break;
                default:
                    auditLog.info(logMessage);
            }
        };

        // 异步或同步执行
        if (auditLogConfig.async()) {
            executorService.submit(logTask);
        } else {
            logTask.run();
        }
    }

    /**
     * 格式化日志消息
     */
    private String formatLogMessage(AuditLogEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("[审计日志] ");
        sb.append("模块：").append(entry.module).append(" | ");
        sb.append("操作：").append(entry.action).append(" | ");
        sb.append("状态：").append(entry.status).append(" | ");
        sb.append("操作人：").append(entry.operatorId).append(" | ");
        sb.append("IP: ").append(entry.ipAddress).append(" | ");
        sb.append("耗时：").append(entry.responseTimeMs).append("ms");

        if (entry.description != null && !entry.description.isEmpty()) {
            sb.append(" | 描述：").append(entry.description);
        }

        if (entry.errorMessage != null) {
            sb.append(" | 错误：").append(entry.errorMessage);
        }

        return sb.toString();
    }

    /**
     * 评估描述表达式
     */
    private String evaluateDescription(ProceedingJoinPoint joinPoint, AuditLog auditLogConfig) {
        String description = auditLogConfig.description();
        if (description == null || description.trim().isEmpty()) {
            return auditLogConfig.module() + " - " + auditLogConfig.action();
        }
        return evaluateExpression(description, joinPoint, null);
    }

    /**
     * 评估 SpEL 表达式
     */
    private String evaluateExpression(String expressionStr, ProceedingJoinPoint joinPoint, Object result) {
        if (expressionStr == null || expressionStr.trim().isEmpty()) {
            return null;
        }

        try {
            Expression expression = parser.parseExpression(expressionStr);
            StandardEvaluationContext context = new StandardEvaluationContext();

            // 添加参数到上下文
            if (joinPoint != null) {
                String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
                Object[] args = joinPoint.getArgs();
                for (int i = 0; i < paramNames.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                    context.setVariable("param" + i, args[i]);
                }
            }

            // 添加结果
            if (result != null) {
                context.setVariable("result", result);
            }

            Object value = expression.getValue(context);
            return value != null ? value.toString() : null;

        } catch (Exception e) {
            log.warn("SpEL 表达式解析失败：{}", expressionStr, e);
            return expressionStr;
        }
    }

    /**
     * 检查是否忽略异常
     */
    private boolean shouldIgnoreException(Throwable throwable, AuditLog auditLogConfig) {
        for (Class<? extends Throwable> ignoreClass : auditLogConfig.ignoreExceptions()) {
            if (ignoreClass.isInstance(throwable)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 参数转 JSON
     */
    private String paramsToJson(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        return objectToJson(args);
    }

    /**
     * 对象转 JSON
     */
    private String objectToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }

    /**
     * 生成日志 ID
     */
    private String generateLogId() {
        return "AUDIT_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
    }

    /**
     * 获取请求
     */
    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取存储的日志
     */
    public AuditLogEntry getLogEntry(String id) {
        return logStore.get(id);
    }

    /**
     * 清除日志
     */
    public void clearLogs() {
        logStore.clear();
    }

    /**
     * 获取日志数量
     */
    public int getLogCount() {
        return logStore.size();
    }

    /**
     * 审计日志条目
     */
    public static class AuditLogEntry {
        /**
         * 日志 ID
         */
        public String id;

        /**
         * 时间戳
         */
        public LocalDateTime timestamp;

        /**
         * 模块
         */
        public String module;

        /**
         * 操作
         */
        public String action;

        /**
         * 描述
         */
        public String description;

        /**
         * 操作人 ID
         */
        public String operatorId;

        /**
         * 操作人名称
         */
        public String operatorName;

        /**
         * IP 地址
         */
        public String ipAddress;

        /**
         * User-Agent
         */
        public String userAgent;

        /**
         * 请求 URI
         */
        public String requestUri;

        /**
         * 请求方法
         */
        public String requestMethod;

        /**
         * 类名
         */
        public String className;

        /**
         * 方法名
         */
        public String methodName;

        /**
         * 业务 ID
         */
        public String businessId;

        /**
         * 业务类型
         */
        public String businessType;

        /**
         * 状态
         */
        public AuditLog.OperationStatus status;

        /**
         * 日志级别
         */
        public AuditLog.LogLevel logLevel;

        /**
         * 请求参数 (JSON)
         */
        public String params;

        /**
         * 返回结果 (JSON)
         */
        public String result;

        /**
         * 错误类型
         */
        public String errorType;

        /**
         * 错误消息
         */
        public String errorMessage;

        /**
         * 响应时间 (ms)
         */
        public Long responseTimeMs;

        /**
         * 是否记录参数
         */
        public boolean recordParams;

        /**
         * 是否记录结果
         */
        public boolean recordResult;
    }
}
