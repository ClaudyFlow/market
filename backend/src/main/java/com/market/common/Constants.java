package com.market.common;

/**
 * 系统常量类
 */
public final class Constants {

    private Constants() {
        // 私有构造函数，防止实例化
    }

    // ==================== 状态常量 ====================
    
    /**
     * 订单状态
     */
    public static final class OrderStatus {
        public static final String PENDING = "PENDING";         // 待支付
        public static final String PAID = "PAID";               // 已支付
        public static final String SHIPPED = "SHIPPED";         // 已发货
        public static final String COMPLETED = "COMPLETED";     // 已完成
        public static final String CANCELLED = "CANCELLED";     // 已取消
        public static final String REFUNDING = "REFUNDING";     // 退款中
        public static final String REFUNDED = "REFUNDED";       // 已退款
    }

    /**
     * 用户状态
     */
    public static final class UserStatus {
        public static final String ACTIVE = "ACTIVE";           // 正常
        public static final String BANNED = "BANNED";           // 禁用
        public static final String INACTIVE = "INACTIVE";       // 未激活
    }

    /**
     * 商品状态
     */
    public static final class ProductStatus {
        public static final Integer ON_SALE = 1;                // 上架
        public static final Integer OFF_SALE = 0;               // 下架
        public static final Integer PENDING = 2;                // 待审核
    }

    /**
     * 审核状态
     */
    public static final class AuditStatus {
        public static final Integer PENDING = 0;                // 待审核
        public static final Integer APPROVED = 1;               // 审核通过
        public static final Integer REJECTED = 2;               // 审核拒绝
    }

    /**
     * 优惠券状态
     */
    public static final class CouponStatus {
        public static final String ACTIVE = "ACTIVE";           // 有效
        public static final String INACTIVE = "INACTIVE";       // 无效
        public static final String EXPIRED = "EXPIRED";         // 已过期
        public static final String USED_UP = "USED_UP";         // 已领完
    }

    /**
     * 优惠券使用状态
     */
    public static final class UserCouponStatus {
        public static final String UNUSED = "UNUSED";           // 未使用
        public static final String USED = "USED";               // 已使用
        public static final String EXPIRED = "EXPIRED";         // 已过期
    }

    // ==================== 类型常量 ====================
    
    /**
     * 优惠券类型
     */
    public static final class CouponType {
        public static final String PERCENT = "PERCENT";         // 折扣券
        public static final String FIXED = "FIXED";             // 满减券
    }

    /**
     * 优惠券适用范围
     */
    public static final class CouponScope {
        public static final String ALL = "ALL";                 // 全场通用
        public static final String CATEGORY = "CATEGORY";       // 品类券
        public static final String PRODUCT = "PRODUCT";         // 商品券
        public static final String SHOP = "SHOP";               // 店铺券
    }

    /**
     * 支付方式
     */
    public static final class PaymentMethod {
        public static final String ALIPAY = "ALIPAY";           // 支付宝
        public static final String WECHAT = "WECHAT";           // 微信
        public static final String CARD = "CARD";               // 银行卡
        public static final String BALANCE = "BALANCE";         // 余额
    }

    /**
     * 配送方式
     */
    public static final class DeliveryType {
        public static final String EXPRESS = "EXPRESS";         // 快递
        public static final String PICKUP = "PICKUP";           // 自提
        public static final String VIRTUAL = "VIRTUAL";         // 虚拟商品
    }

    // ==================== 角色常量 ====================
    
    /**
     * 用户角色
     */
    public static final class UserRole {
        public static final String USER = "USER";               // 普通用户
        public static final String MERCHANT = "MERCHANT";       // 商家
        public static final String ADMIN = "ADMIN";             // 管理员
    }

    /**
     * 商家状态
     */
    public static final class MerchantStatus {
        public static final String INACTIVE = "INACTIVE";       // 未激活
        public static final String ACTIVE = "ACTIVE";           // 已激活
        public static final String BANNED = "BANNED";           // 已封禁
    }

    // ==================== 通知常量 ====================
    
    /**
     * 通知类型
     */
    public static final class NotificationType {
        public static final String SYSTEM = "SYSTEM";           // 系统通知
        public static final String ORDER = "ORDER";             // 订单通知
        public static final String PROMOTION = "PROMOTION";     // 促销通知
        public static final String REMINDER = "REMINDER";       // 提醒通知
        public static final String ACTIVITY = "ACTIVITY";       // 活动通知
    }

    /**
     * 通知级别
     */
    public static final class NotificationLevel {
        public static final String INFO = "INFO";               // 普通
        public static final String WARNING = "WARNING";         // 重要
        public static final String URGENT = "URGENT";           // 紧急
    }

    // ==================== 缓存常量 ====================
    
    /**
     * 缓存名称
     */
    public static final class CacheNames {
        public static final String USERS = "users";
        public static final String PRODUCTS = "products";
        public static final String SHOPS = "shops";
        public static final String ORDERS = "orders";
        public static final String COUPONS = "coupons";
        public static final String CATEGORIES = "categories";
        public static final String NOTIFICATIONS = "notifications";
    }

    // ==================== Redis Key 常量 ====================
    
    /**
     * Redis Key 前缀
     */
    public static final class RedisKeys {
        public static final String USER_PREFIX = "user:";
        public static final String PRODUCT_PREFIX = "product:";
        public static final String SHOP_PREFIX = "shop:";
        public static final String ORDER_PREFIX = "order:";
        public static final String CART_PREFIX = "cart:";
        public static final String TOKEN_PREFIX = "token:";
        public static final String CAPTCHA_PREFIX = "captcha:";
        public static final String LOCK_PREFIX = "lock:";
        public static final String RATE_LIMIT_PREFIX = "rate:";
    }

    // ==================== 默认值常量 ====================
    
    /**
     * 默认分页参数
     */
    public static final class Pagination {
        public static final int DEFAULT_PAGE = 1;
        public static final int DEFAULT_SIZE = 10;
        public static final int MAX_SIZE = 100;
    }

    /**
     * 默认数值
     */
    public static final class Defaults {
        public static final int ZERO = 0;
        public static final int ONE = 1;
        public static final int TEN = 10;
        public static final int HUNDRED = 100;
    }

    // ==================== 正则表达式常量 ====================
    
    /**
     * 正则表达式
     */
    public static final class Regex {
        public static final String PHONE = "^1[3-9]\\d{9}$";
        public static final String EMAIL = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        public static final String ID_CARD = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
        public static final String USERNAME = "^[a-zA-Z][a-zA-Z0-9_-]{3,19}$";
    }

    // ==================== 时间常量 ====================
    
    /**
     * 时间常量（秒）
     */
    public static final class Time {
        public static final long ONE_MINUTE = 60L;
        public static final long ONE_HOUR = 3600L;
        public static final long ONE_DAY = 86400L;
        public static final long ONE_WEEK = 604800L;
        public static final long ONE_MONTH = 2592000L;
    }

    // ==================== JWT 常量 ====================
    
    /**
     * JWT 相关常量
     */
    public static final class Jwt {
        public static final String TOKEN_HEADER = "Authorization";
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String CLAIM_USER_ID = "userId";
        public static final String CLAIM_USERNAME = "username";
        public static final String CLAIM_ROLE = "role";
    }
}
