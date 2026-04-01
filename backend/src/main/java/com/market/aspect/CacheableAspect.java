package com.market.aspect;

import com.market.annotation.Cacheable;
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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 缓存切面
 *
 * 拦截带有 @Cacheable 注解的方法，实现缓存功能
 * 优先从缓存获取数据，缓存未命中时执行方法并缓存结果
 */
@Slf4j
@Aspect
@Component
public class CacheableAspect {

    private static final Logger log = LoggerFactory.getLogger(CacheableAspect.class);

    /**
     * 本地缓存存储：cacheName:key -> CacheEntry
     */
    private final Map<String, CacheEntry> localCache = new ConcurrentHashMap<>();

    /**
     * SpEL 表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 围绕带有 @Cacheable 注解的方法执行
     */
    @Around("@annotation(com.market.annotation.Cacheable)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        // 生成缓存 key
        String cacheKey = generateCacheKey(joinPoint, cacheable);

        // 尝试从缓存获取
        Object cachedValue = getFromCache(cacheKey, cacheable);
        if (cachedValue != null) {
            log.debug("[缓存命中] {} - key: {}", cacheable.cacheName(), cacheKey);
            return cachedValue;
        }

        // 缓存未命中，执行方法
        log.debug("[缓存未命中] {} - key: {}", cacheable.cacheName(), cacheKey);
        Object result = joinPoint.proceed();

        // 检查是否需要缓存
        if (shouldCache(result, cacheable, joinPoint)) {
            putToCache(cacheKey, result, cacheable);
        }

        return result;
    }

    /**
     * 生成缓存 key
     */
    private String generateCacheKey(ProceedingJoinPoint joinPoint, Cacheable cacheable) {
        String cacheName = cacheable.cacheName();
        String keyExpression = cacheable.key();

        // 如果没有指定 key，使用方法签名和参数
        if (keyExpression == null || keyExpression.trim().isEmpty()) {
            String methodKey = joinPoint.getSignature().toShortString();
            String argsKey = generateArgsKey(joinPoint.getArgs());
            return cacheName + ":" + methodKey + ":" + argsKey;
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

            // 添加常用变量
            context.setVariable("method", joinPoint.getSignature().getName());
            context.setVariable("target", joinPoint.getTarget());

            Object keyValue = expression.getValue(context);
            return cacheName + ":" + (keyValue != null ? keyValue.toString() : "null");
        } catch (Exception e) {
            log.warn("SpEL 表达式解析失败：{}, 使用默认 key", keyExpression, e);
            return cacheName + ":default:" + System.currentTimeMillis();
        }
    }

    /**
     * 从缓存获取数据
     */
    private Object getFromCache(String key, Cacheable cacheable) {
        CacheEntry entry = localCache.get(key);
        if (entry == null) {
            return null;
        }

        // 检查是否过期
        if (entry.isExpired()) {
            localCache.remove(key);
            log.debug("[缓存过期] key: {}", key);
            return null;
        }

        return entry.getValue();
    }

    /**
     * 放入缓存
     */
    private void putToCache(String key, Object value, Cacheable cacheable) {
        if (value == null && cacheable.ignoreNull()) {
            log.debug("[忽略空值] 不缓存 null 值：{}", key);
            return;
        }

        long expireMillis = cacheable.timeUnit().toMillis(cacheable.expire());
        localCache.put(key, new CacheEntry(value, System.currentTimeMillis() + expireMillis));
        log.debug("[缓存写入] {} - key: {}, expire: {} {}",
            cacheable.cacheName(), key, cacheable.expire(), cacheable.timeUnit());
    }

    /**
     * 判断是否应该缓存
     */
    private boolean shouldCache(Object result, Cacheable cacheable, ProceedingJoinPoint joinPoint) {
        // 检查 unless 条件
        String unless = cacheable.unless();
        if (unless != null && !unless.trim().isEmpty()) {
            try {
                Expression expression = parser.parseExpression(unless);
                StandardEvaluationContext context = new StandardEvaluationContext();
                context.setVariable("result", result);

                Object unlessResult = expression.getValue(context, Boolean.class);
                if (Boolean.TRUE.equals(unlessResult)) {
                    log.debug("[除非条件满足] 不缓存：{}", unless);
                    return false;
                }
            } catch (Exception e) {
                log.warn("unless 表达式解析失败：{}", unless, e);
            }
        }

        // 检查 condition 条件
        String condition = cacheable.condition();
        if (condition != null && !condition.trim().isEmpty()) {
            try {
                Expression expression = parser.parseExpression(condition);
                StandardEvaluationContext context = new StandardEvaluationContext();
                context.setVariable("result", result);

                Object conditionResult = expression.getValue(context, Boolean.class);
                if (!Boolean.TRUE.equals(conditionResult)) {
                    log.debug("[条件不满足] 不缓存：{}", condition);
                    return false;
                }
            } catch (Exception e) {
                log.warn("condition 表达式解析失败：{}", condition, e);
            }
        }

        return true;
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
     * 清除缓存
     */
    public void evictCache(String cacheName, String key) {
        String fullKey = cacheName + ":" + key;
        localCache.remove(fullKey);
        log.info("[缓存清除] {}", fullKey);
    }

    /**
     * 清除指定缓存名称的所有缓存
     */
    public void evictCache(String cacheName) {
        String prefix = cacheName + ":";
        localCache.keySet().removeIf(key -> key.startsWith(prefix));
        log.info("[缓存清除] 清除所有 {} 缓存", cacheName);
    }

    /**
     * 清除所有缓存
     */
    public void evictAllCache() {
        localCache.clear();
        log.info("[缓存清除] 清除所有缓存");
    }

    /**
     * 获取缓存统计信息
     */
    public CacheStats getCacheStats() {
        return new CacheStats(localCache.size());
    }

    /**
     * 缓存条目
     */
    private static class CacheEntry {
        private final Object value;
        private final long expireTime;

        CacheEntry(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }

        Object getValue() {
            return value;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    /**
     * 缓存统计信息
     */
    public static class CacheStats {
        private final long size;

        CacheStats(long size) {
            this.size = size;
        }

        public long getSize() {
            return size;
        }
    }
}
