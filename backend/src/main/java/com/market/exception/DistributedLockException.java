package com.market.exception;

/**
 * 分布式锁异常
 *
 * 当获取锁失败时被抛出
 */
public class DistributedLockException extends RuntimeException {

    /**
     * 锁 key
     */
    private final String lockKey;

    public DistributedLockException(String message) {
        super(message);
        this.lockKey = null;
    }

    public DistributedLockException(String message, String lockKey) {
        super(message);
        this.lockKey = lockKey;
    }

    public DistributedLockException(String message, Throwable cause) {
        super(message, cause);
        this.lockKey = null;
    }

    public String getLockKey() {
        return lockKey;
    }
}
