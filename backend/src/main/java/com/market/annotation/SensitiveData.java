package com.market.annotation;

import java.lang.annotation.*;

/**
 * 敏感数据脱敏注解
 *
 * 用于标记需要脱敏的字段或方法返回值
 * 支持多种脱敏规则
 *
 * @example
 * {@code
 * @SensitiveData(type = SensitiveType.PHONE)
 * private String phone;
 *
 * @SensitiveData(type = SensitiveType.ID_CARD)
 * public String getIdCard() { ... }
 * }
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveData {

    /**
     * 脱敏类型
     */
    SensitiveType type() default SensitiveType.DEFAULT;

    /**
     * 脱敏字符（默认用*）
     */
    char maskChar() default '*';

    /**
     * 保留前缀长度
     */
    int prefixLength() default 0;

    /**
     * 保留后缀长度
     */
    int suffixLength() default 0;

    /**
     * 脱敏类型枚举
     */
    enum SensitiveType {
        /**
         * 默认（不脱敏）
         */
        DEFAULT,
        /**
         * 手机号
         */
        PHONE,
        /**
         * 身份证号
         */
        ID_CARD,
        /**
         * 银行卡号
         */
        BANK_CARD,
        /**
         * 邮箱
         */
        EMAIL,
        /**
         * 地址
         */
        ADDRESS,
        /**
         * 姓名
         */
        NAME,
        /**
         * 密码
         */
        PASSWORD,
        /**
         * 自定义
         */
        CUSTOM
    }
}
