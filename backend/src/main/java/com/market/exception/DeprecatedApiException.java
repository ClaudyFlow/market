package com.market.exception;

import com.market.common.DeprecationStatus;
import lombok.Getter;

import java.time.LocalDate;

/**
 * API 弃用异常
 *
 * 当调用已弃用的 API 时抛出（如果配置了 throwException = true）
 */
@Getter
public class DeprecatedApiException extends RuntimeException {

    /**
     * 弃用状态
     */
    private final DeprecationStatus status;

    /**
     * 弃用开始日期
     */
    private final LocalDate sinceDate;

    /**
     * 弃用结束日期
     */
    private final LocalDate untilDate;

    /**
     * 替代方案索引
     */
    private final String[] replacement;

    /**
     * 弃用原因
     */
    private final String reason;

    /**
     * 方法签名
     */
    private final String methodSignature;

    public DeprecatedApiException(DeprecationStatus status,
                                   String message,
                                   LocalDate sinceDate,
                                   LocalDate untilDate,
                                   String[] replacement,
                                   String reason,
                                   String methodSignature) {
        super(message);
        this.status = status;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.replacement = replacement;
        this.reason = reason;
        this.methodSignature = methodSignature;
    }

    public DeprecatedApiException(DeprecationStatus status,
                                   String message,
                                   LocalDate sinceDate,
                                   LocalDate untilDate,
                                   String[] replacement,
                                   String reason,
                                   String methodSignature,
                                   Throwable cause) {
        super(message, cause);
        this.status = status;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.replacement = replacement;
        this.reason = reason;
        this.methodSignature = methodSignature;
    }

    /**
     * 获取替代方案列表（格式化后）
     */
    public String getReplacementInfo() {
        if (replacement == null || replacement.length == 0) {
            return "无替代方案";
        }
        StringBuilder sb = new StringBuilder("替代方案：");
        for (int i = 0; i < replacement.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(replacement[i]);
        }
        return sb.toString();
    }
}
