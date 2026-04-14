package com.market.mq;

import com.market.common.MQConstants;
import com.market.dto.mq.CreditMessage;
import com.market.dto.mq.EmailMessage;
import com.market.dto.mq.NotificationMessage;
import com.market.dto.mq.OrderDelayMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 消息队列生产者
 */
@Service
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class MQProducer {

    private static final Logger log = LoggerFactory.getLogger(MQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${market.mq.exchange:market.exchange}")
    private String marketExchange;

    @Value("${market.mq.order-delay-exchange:market.order.delay.exchange}")
    private String orderDelayExchange;

    public MQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送验证码邮件消息
     */
    public void sendVerificationEmail(String email, String code, int expireMinutes) {
        EmailMessage message = EmailMessage.verification(email, code, expireMinutes);
        rabbitTemplate.convertAndSend(
                marketExchange,
                MQConstants.ROUTING_KEY_EMAIL_VERIFICATION,
                message
        );
        log.info("发送验证码邮件消息到队列: {}", email);
    }

    /**
     * 发送欢迎邮件消息
     */
    public void sendWelcomeEmail(String email, String username) {
        EmailMessage message = EmailMessage.welcome(email, username);
        rabbitTemplate.convertAndSend(
                marketExchange,
                MQConstants.ROUTING_KEY_EMAIL_WELCOME,
                message
        );
        log.info("发送欢迎邮件消息到队列: {}", email);
    }

    /**
     * 发送通用邮件消息
     */
    public void sendEmail(EmailMessage message) {
        rabbitTemplate.convertAndSend(
                marketExchange,
                MQConstants.ROUTING_KEY_EMAIL_VERIFICATION,
                message
        );
        log.info("发送邮件消息到队列: {}", message.to());
    }

    /**
     * 发送站内通知消息
     */
    public void sendNotification(NotificationMessage message) {
        rabbitTemplate.convertAndSend(
                marketExchange,
                MQConstants.ROUTING_KEY_NOTIFICATION,
                message
        );
        log.info("发送通知消息到队列: userId={}, title={}", message.userId(), message.title());
    }

    /**
     * 发送订单通知消息
     */
    public void sendOrderNotification(NotificationMessage message) {
        rabbitTemplate.convertAndSend(
                marketExchange,
                MQConstants.ROUTING_KEY_ORDER_NOTIFICATION,
                message
        );
        log.info("发送订单通知消息到队列: userId={}", message.userId());
    }

    /**
     * 发送积分消息
     */
    public void sendCredit(CreditMessage message) {
        rabbitTemplate.convertAndSend(
                marketExchange,
                MQConstants.ROUTING_KEY_CREDIT,
                message
        );
        log.info("发送积分消息到队列: userId={}, credit={}", message.userId(), message.credit());
    }

    /**
     * 发送订单延迟取消消息 (延迟队列)
     * @param message 订单消息
     * @param delayMs 延迟时间 (毫秒)
     */
    public void sendOrderDelay(OrderDelayMessage message, long delayMs) {
        rabbitTemplate.convertAndSend(
                orderDelayExchange,
                MQConstants.ROUTING_KEY_ORDER_DELAY,
                message,
                msg -> {
                    msg.getMessageProperties().setHeader("x-delay", (int) Math.min(delayMs, Integer.MAX_VALUE));
                    return msg;
                }
        );
        log.info("发送订单延迟取消消息到队列: orderId={}, delay={}ms", message.orderId(), delayMs);
    }

    /**
     * 发送订单延迟取消消息 (使用默认 30 分钟)
     */
    public void sendOrderDelay(OrderDelayMessage message) {
        sendOrderDelay(message, MQConstants.ORDER_TIMEOUT_MS);
    }

    /**
     * 取消订单延迟消息 (通过发送取消消息到死信队列)
     */
    public void cancelOrderDelay(Long orderId) {
        rabbitTemplate.convertAndSend(
                marketExchange,
                MQConstants.ROUTING_KEY_ORDER_CANCEL,
                new OrderDelayMessage(orderId, null, null, null)
        );
        log.info("发送订单取消消息到队列: orderId={}", orderId);
    }
}
