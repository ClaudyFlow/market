package com.market.common;

/**
 * RabbitMQ 队列、交换机、路由键常量
 */
public final class MQConstants {

    private MQConstants() {
    }

    // ==================== 交换机 ====================

    /**
     * 主交换机 (Topic Exchange)
     */
    public static final String MARKET_EXCHANGE = "market.exchange";

    /**
     * 订单延迟交换机 (Delayed Exchange)
     */
    public static final String ORDER_DELAY_EXCHANGE = "market.order.delay.exchange";

    // ==================== 队列 ====================

    /**
     * 邮件发送队列
     */
    public static final String EMAIL_QUEUE = "market.email.queue";

    /**
     * 站内通知队列
     */
    public static final String NOTIFICATION_QUEUE = "market.notification.queue";

    /**
     * 积分发放队列
     */
    public static final String CREDIT_QUEUE = "market.credit.queue";

    /**
     * 审计日志队列
     */
    public static final String AUDIT_LOG_QUEUE = "market.audit.log.queue";

    /**
     * 订单延迟队列 (30分钟后进入死信队列)
     */
    public static final String ORDER_DELAY_QUEUE = "market.order.delay.queue";

    /**
     * 订单取消队列 (死信队列)
     */
    public static final String ORDER_CANCEL_QUEUE = "market.order.cancel.queue";

    // ==================== 路由键 ====================

    /**
     * 验证码邮件路由键
     */
    public static final String ROUTING_KEY_EMAIL_VERIFICATION = "email.verification";

    /**
     * 欢迎邮件路由键
     */
    public static final String ROUTING_KEY_EMAIL_WELCOME = "email.welcome";

    /**
     * 通知路由键
     */
    public static final String ROUTING_KEY_NOTIFICATION = "notification.send";

    /**
     * 订单通知路由键
     */
    public static final String ROUTING_KEY_ORDER_NOTIFICATION = "notification.order";

    /**
     * 积分发放路由键
     */
    public static final String ROUTING_KEY_CREDIT = "credit.add";

    /**
     * 审计日志路由键
     */
    public static final String ROUTING_KEY_AUDIT_LOG = "audit.log";

    /**
     * 订单延迟路由键
     */
    public static final String ROUTING_KEY_ORDER_DELAY = "order.delay";

    /**
     * 订单取消路由键
     */
    public static final String ROUTING_KEY_ORDER_CANCEL = "order.cancel";

    // ==================== 消息头 ====================

    /**
     * 消息延迟时间 (毫秒)
     */
    public static final String HEADER_DELAY = "x-delay";

    /**
     * 订单超时时间 (30分钟)
     */
    public static final long ORDER_TIMEOUT_MS = 30 * 60 * 1000L;
}
