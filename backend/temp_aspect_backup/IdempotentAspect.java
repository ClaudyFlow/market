package com.market.aspect;

import com.market.annotation.Idempotent;
import com.market.exception.IdempotentException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性切面
 *
 * 拦截带有 @Idempotent 注解的方法，防止重复提交
 * 使用本地锁机制实现（生产环境建议使用 Redis 分布式锁）
 */
@Slf4j
@Aspect
@Component
public class IdempotentAspect {

    /**
     * SpEL 表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 正在执行的 key 集合
     */
    private final Map<String, LockEntry> executingKeys = new ConcurrentHashMap<>();

    /**
     * 缓存的结果
     */
    private final Map<String, ResultEntry> cachedResults = new ConcurrentHashMap<>();

    /**
     * 围绕带有 @Idempotent 注解的方法执行
     */
    @Around("@annotation(com.market.annotation.Idempotent)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        Idempotent idempotent = method.getAnnotation(Idempotent.class);

        // 生成幂等 key
        String idempotentKey = generateIdempotentKey(joinPoint, idempotent);

        // 检查是否有缓存结果
        if (idempotent.cacheResult()) {
            ResultEntry cachedEntry = getCachedResult(idempotentKey);
            if (cachedEntry != null && !cachedEntry.isExpired()) {
                log.info("[幂等性] 返回缓存结果 - key: {}", idempotentKey);
                return cachedEntry.getResult();
            }
        }

        // 尝试获取锁
        if (!tryAcquireLock(idempotentKey, idempotent)) {
            String message = idempotent.message() + " - key: " + idempotentKey;
            log.warn("[幂等性] 重复请求被拦截 - {}", message);

            if (idempotent.throwException()) {
                throw new IdempotentException(message, idempotentKey);
            }
            return null;
        }

        try {
            // 执行方法
            Object result = joinPoint.proceed();

            // 缓存结果
            if (idempotent.cacheResult()) {
                cacheResult(idempotentKey, result, idempotent);
            }

            return result;

        } finally {
            // 释放锁
            releaseLock(idempotentKey);
        }
    }

    /**
     * 生成幂等 key
     */
    private String generateIdempotentKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        String keyExpression = idempotent.key();

        // 如果没有指定 key，使用方法签名和参数
        if (keyExpression == null || keyExpression.trim().isEmpty()) {
            String methodKey = joinPoint.getSignature().toShortString();
            String argsKey = generateArgsKey(joinPoint.getArgs());
            return "idempotent:" + methodKey + ":" + argsKey;
        }

        // 解析 SpEL 表达式
        try {
            Expression expression = parser.parseExpression(keyExpression);
            StandardEvaluationContext context = new StandardEvaluationContext();

            // 添加参数到上下文
            String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
                context.setVariable("param" + i, args[i]);
            }

            Object keyValue = expression.getValue(context);
            return "idempotent:" + (keyValue != null ? keyValue.toString() : "null");
        } catch (Exception e) {
            log.warn("SpEL 表达式解析失败：{}, 使用默认 key", keyExpression, e);
            return "idempotent:default:" + System.currentTimeMillis();
        }
    }

    /**
     * 尝试获取锁
     */
    private synchronized boolean tryAcquireLock(String key, Idempotent idempotent) {
        LockEntry entry = executingKeys.get(key);

        if (entry == null) {
            // 没有锁，获取成功
            executingKeys.put(key, new LockEntry(System.currentTimeMillis()));
            return true;
        }

        // 检查锁是否过期
        long expireMillis = idempotent.timeUnit().toMillis(idempotent.expire());
        if (System.currentTimeMillis() - entry.lockTime > expireMillis) {
            // 锁已过期，重新获取
            executingKeys.put(key, new LockEntry(System.currentTimeMillis()));
            return true;
        }

        // 锁正在持有
        return false;
    }

    /**
     * 释放锁
     */
    private synchronized void releaseLock(String key) {
        executingKeys.remove(key);
        log.debug("[幂等性] 锁已释放 - key: {}", key);
    }

    /**
     * 获取缓存结果
     */
    private ResultEntry getCachedResult(String key) {
        return cachedResults.get(key);
    }

    /**
     * 缓存结果
     */
    private void cacheResult(String key, Object result, Idempotent idempotent) {
        long expireMillis = idempotent.timeUnit().toMillis(idempotent.expire());
        cachedResults.put(key, new ResultEntry(result, System.currentTimeMillis() + expireMillis));
        log.debug("[幂等性] 结果已缓存 - key: {}, expire: {} {}",
            key, idempotent.expire(), idempotent.timeUnit());
    }

    /**
     * 清除缓存
     */
    public void clearCache(String key) {
        cachedResults.remove(key);
        log.info("[幂等性] 缓存已清除 - key: {}", key);
    }

    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        cachedResults.clear();
        log.info("[幂等性] 所有缓存已清除");
    }

    /**
     * 获取统计信息
     */
    public IdempotentStats getStats() {
        return new IdempotentStats(executingKeys.size(), cachedResults.size());
    }

    /**
     * 生成参数 key
     */
    private String generateArgsKey(Object[] args) {
        if (args == null || args.length == 0) {
            return "empty";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append("_");
            sb.append(args[i] != null ? args[i].hashCode() : "null");
        }
        return sb.toString();
    }

    /**
     * 锁条目
     */
    private static class LockEntry {
        private final long lockTime;

        LockEntry(long lockTime) {
            this.lockTime = lockTime;
        }
    }

    /**
     * 结果条目
     */
    private static class ResultEntry {
        private final Object result;
        private final long expireTime;

        ResultEntry(Object result, long expireTime) {
            this.result = result;
            this.expireTime = expireTime;
        }

        Object getResult() {
            return result;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    /**
     * 幂等统计信息
     */
    public static class IdempotentStats {
        private final int executingCount;
        private final int cachedCount;

        IdempotentStats(int executingCount, int cachedCount) {
            this.executingCount = executingCount;
            this.cachedCount = cachedCount;
        }

        public int getExecutingCount() {
            return executingCount;
        }

        public int getCachedCount() {
            return cachedCount;
        }
    }
}
