package com.market.aspect;

import com.market.annotation.DistributedLock;
import com.market.exception.DistributedLockException;
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
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 分布式锁切面
 *
 * 拦截带有 @DistributedLock 注解的方法，实现锁机制
 * 使用本地锁实现（生产环境建议替换为 Redis 分布式锁）
 */
@Slf4j
@Aspect
@Component
public class DistributedLockAspect {

    private static final Logger log = LoggerFactory.getLogger(DistributedLockAspect.class);

    /**
     * SpEL 表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 本地锁存储
     */
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final Map<String, ReadWriteLock> readWriteLocks = new ConcurrentHashMap<>();

    /**
     * 围绕带有 @DistributedLock 注解的方法执行
     */
    @Around("@annotation(com.market.annotation.DistributedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // 生成锁 key
        String lockKey = generateLockKey(joinPoint, distributedLock);

        // 获取锁类型
        DistributedLock.LockType lockType = distributedLock.lockType();

        // 尝试获取锁
        if (!acquireLock(lockKey, distributedLock)) {
            String message = distributedLock.message() + " - key: " + lockKey;
            log.warn("[分布式锁] 获取锁失败 - {}", message);

            if (distributedLock.throwException()) {
                throw new DistributedLockException(message, lockKey);
            }
            return null;
        }

        try {
            log.debug("[分布式锁] 获取锁成功 - key: {}", lockKey);

            // 执行方法
            return joinPoint.proceed();

        } finally {
            // 释放锁
            releaseLock(lockKey, lockType);
        }
    }

    /**
     * 生成锁 key
     */
    private String generateLockKey(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        String keyExpression = distributedLock.key();

        // 如果没有指定 key，使用方法签名和参数
        if (keyExpression == null || keyExpression.trim().isEmpty()) {
            String methodKey = joinPoint.getSignature().toShortString();
            String argsKey = generateArgsKey(joinPoint.getArgs());
            return "lock:" + methodKey + ":" + argsKey;
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
            return "lock:" + (keyValue != null ? keyValue.toString() : "null");
        } catch (Exception e) {
            log.warn("SpEL 表达式解析失败：{}, 使用默认 key", keyExpression, e);
            return "lock:default:" + System.currentTimeMillis();
        }
    }

    /**
     * 获取锁
     */
    private boolean acquireLock(String key, DistributedLock distributedLock) throws InterruptedException {
        DistributedLock.LockType lockType = distributedLock.lockType();
        long waitTime = distributedLock.timeUnit().toMillis(distributedLock.waitTime());
        int retryCount = distributedLock.retryCount();
        long retryInterval = distributedLock.timeUnit().toMillis(distributedLock.retryInterval());

        switch (lockType) {
            case READ:
            case WRITE:
                return acquireReadWriteLock(key, lockType, waitTime, retryCount, retryInterval);
            default:
                return acquireReentrantLock(key, waitTime, retryCount, retryInterval);
        }
    }

    /**
     * 获取可重入锁
     */
    private boolean acquireReentrantLock(String key, long waitTime, int retryCount, long retryInterval)
            throws InterruptedException {

        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());

        // 尝试获取锁
        if (lock.tryLock(waitTime, TimeUnit.MILLISECONDS)) {
            return true;
        }

        // 重试
        for (int i = 0; i < retryCount; i++) {
            Thread.sleep(retryInterval);
            if (lock.tryLock(waitTime, TimeUnit.MILLISECONDS)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取读写锁
     */
    private boolean acquireReadWriteLock(String key, DistributedLock.LockType lockType,
                                          long waitTime, int retryCount, long retryInterval)
            throws InterruptedException {

        ReadWriteLock readWriteLock = readWriteLocks.computeIfAbsent(key, k -> new ReentrantReadWriteLock());

        java.util.concurrent.locks.Lock lock =
            (lockType == DistributedLock.LockType.WRITE)
                ? readWriteLock.writeLock()
                : readWriteLock.readLock();

        // 尝试获取锁
        if (lock.tryLock(waitTime, TimeUnit.MILLISECONDS)) {
            return true;
        }

        // 重试
        for (int i = 0; i < retryCount; i++) {
            Thread.sleep(retryInterval);
            if (lock.tryLock(waitTime, TimeUnit.MILLISECONDS)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 释放锁
     */
    private void releaseLock(String key, DistributedLock.LockType lockType) {
        try {
            switch (lockType) {
                case READ:
                case WRITE:
                    ReadWriteLock readWriteLock = readWriteLocks.get(key);
                    if (readWriteLock != null) {
                        java.util.concurrent.locks.Lock lock =
                            (lockType == DistributedLock.LockType.WRITE)
                                ? readWriteLock.writeLock()
                                : readWriteLock.readLock();
                        lock.unlock();
                    }
                    break;
                default:
                    ReentrantLock lock = locks.get(key);
                    if (lock != null && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                    break;
            }
            log.debug("[分布式锁] 锁已释放 - key: {}", key);
        } catch (Exception e) {
            log.error("[分布式锁] 释放锁失败 - key: {}", key, e);
        }
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
     * 清除锁（谨慎使用）
     */
    public void clearLock(String key) {
        locks.remove(key);
        readWriteLocks.remove(key);
        log.info("[分布式锁] 锁已清除 - key: {}", key);
    }

    /**
     * 获取统计信息
     */
    public LockStats getStats() {
        return new LockStats(locks.size(), readWriteLocks.size());
    }

    /**
     * 锁统计信息
     */
    public static class LockStats {
        private final int reentrantLockCount;
        private final int readWriteLockCount;

        LockStats(int reentrantLockCount, int readWriteLockCount) {
            this.reentrantLockCount = reentrantLockCount;
            this.readWriteLockCount = readWriteLockCount;
        }

        public int getReentrantLockCount() {
            return reentrantLockCount;
        }

        public int getReadWriteLockCount() {
            return readWriteLockCount;
        }
    }
}
