package com.market.common;

import lombok.Getter;

/**
 * 统一错误码枚举
 */
@Getter
public enum ErrorCode {

    // ==================== 通用错误 (1xxx) ====================
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    INVALID_PARAM(400, "参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // ==================== 业务错误 (2xxx) ====================
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_ALREADY_EXISTS(2002, "用户已存在"),
    PASSWORD_ERROR(2003, "密码错误"),
    TOKEN_INVALID(2004, "Token 无效"),
    TOKEN_EXPIRED(2005, "Token 已过期"),
    CAPTCHA_ERROR(2006, "验证码错误"),
    CAPTCHA_EXPIRED(2007, "验证码已过期"),

    PRODUCT_NOT_FOUND(2101, "商品不存在"),
    PRODUCT_OUT_OF_STOCK(2102, "商品库存不足"),
    PRODUCT_STATUS_ERROR(2103, "商品状态错误"),

    SHOP_NOT_FOUND(2201, "店铺不存在"),
    SHOP_ALREADY_EXISTS(2202, "店铺已存在"),
    SHOP_CERTIFIED(2203, "店铺已认证"),

    ORDER_NOT_FOUND(2301, "订单不存在"),
    ORDER_STATUS_ERROR(2302, "订单状态错误"),
    ORDER_CANCELLED(2303, "订单已取消"),
    ORDER_PAID(2304, "订单已支付"),

    CART_EMPTY(2401, "购物车为空"),
    CART_ITEM_NOT_FOUND(2402, "购物车商品不存在"),

    COUPON_NOT_FOUND(2501, "优惠券不存在"),
    COUPON_EXPIRED(2502, "优惠券已过期"),
    COUPON_USED_UP(2503, "优惠券已领完"),
    COUPON_ALREADY_RECEIVED(2504, "优惠券已领取"),
    COUPON_CONDITION_NOT_MET(2505, "不满足优惠券使用条件"),

    REVIEW_NOT_FOUND(2601, "评价不存在"),
    REVIEW_ALREADY_EXISTS(2602, "评价已存在"),

    ADDRESS_NOT_FOUND(2701, "地址不存在"),
    ADDRESS_LIMIT_EXCEEDED(2702, "地址数量已达上限"),

    // ==================== 系统错误 (3xxx) ====================
    DATABASE_ERROR(3001, "数据库错误"),
    REDIS_ERROR(3002, "缓存服务错误"),
    FILE_UPLOAD_ERROR(3003, "文件上传失败"),
    FILE_NOT_FOUND(3004, "文件不存在"),
    FILE_TYPE_ERROR(3005, "文件类型错误"),
    FILE_SIZE_EXCEEDED(3006, "文件大小超出限制"),

    // ==================== 限流错误 (4xxx) ====================
    RATE_LIMIT_EXCEEDED(4001, "请求过于频繁，请稍后再试"),
    IP_BLACKLISTED(4002, "IP 已被列入黑名单"),

    // ==================== 锁相关错误 (5xxx) ====================
    LOCK_FAILED(5001, "获取锁失败"),
    LOCK_TIMEOUT(5002, "锁超时"),
    IDEMPOTENT_ERROR(5003, "请勿重复提交"),

    // ==================== 第三方服务错误 (6xxx) ====================
    SMS_SEND_ERROR(6001, "短信发送失败"),
    EMAIL_SEND_ERROR(6002, "邮件发送失败"),
    PAYMENT_ERROR(6003, "支付失败"),
    LOGISTICS_ERROR(6004, "物流查询失败");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取 ErrorCode
     */
    public static ErrorCode fromCode(Integer code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return ERROR;
    }
}
