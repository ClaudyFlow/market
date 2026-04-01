package com.market.exception;

/**
 * 幂等性异常
 *
 * 当重复提交或重复执行时被抛出
 */
public class IdempotentException extends RuntimeException {

    /**
     * 幂等 key
     */
    private final String idempotentKey;

    public IdempotentException(String message) {
        super(message);
        this.idempotentKey = null;
    }

    public IdempotentException(String message, String idempotentKey) {
        super(message);
        this.idempotentKey = idempotentKey;
    }

    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
        this.idempotentKey = null;
    }

    public String getIdempotentKey() {
        return idempotentKey;
    }
}
