package com.market.mq;

import com.market.common.MQConstants;
import com.market.dto.mq.CreditMessage;
import com.market.dto.mq.EmailMessage;
import com.market.dto.mq.NotificationMessage;
import com.market.dto.mq.OrderDelayMessage;
import com.market.entity.Order;
import com.market.repository.OrderRepository;
import com.market.service.CreditService;
import com.market.service.EmailService;
import com.market.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 消息队列消费者
 */
@Component
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class MQConsumer {

    private static final Logger log = LoggerFactory.getLogger(MQConsumer.class);

    private final EmailService emailService;
    private final NotificationService notificationService;
    private final CreditService creditService;
    private final OrderRepository orderRepository;

    @Value("${verification.code.expire.minutes:5}")
    private int codeExpireMinutes;

    public MQConsumer(EmailService emailService,
                      NotificationService notificationService,
                      CreditService creditService,
                      OrderRepository orderRepository) {
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.creditService = creditService;
        this.orderRepository = orderRepository;
    }

    /**
     * 处理邮件队列消息
     */
    @RabbitListener(queues = MQConstants.EMAIL_QUEUE)
    public void handleEmailMessage(EmailMessage message) {
        try {
            log.info("开始处理邮件消息: to={}, type={}", message.to(), message.type());

            switch (message.type()) {
                case VERIFICATION, WELCOME -> {
                    boolean success = emailService.sendVerificationCode(message.to(), message.subject());
                    if (!success) {
                        log.error("发送邮件失败: {}", message.to());
                    }
                }
                case ORDER, PROMOTION -> {
                    boolean success = emailService.sendHtmlEmail(
                            message.to(),
                            message.subject(),
                            message.content()
                    );
                    if (!success) {
                        log.error("发送HTML邮件失败: {}", message.to());
                    }
                }
            }

            log.info("邮件消息处理完成: to={}", message.to());
        } catch (Exception e) {
            log.error("处理邮件消息异常: {}", e.getMessage(), e);
            // 可根据需求决定是否重新入队
            throw e;
        }
    }

    /**
     * 处理通知队列消息
     */
    @RabbitListener(queues = MQConstants.NOTIFICATION_QUEUE)
    public void handleNotificationMessage(NotificationMessage message) {
        try {
            log.info("开始处理通知消息: userId={}, title={}", message.userId(), message.title());

            notificationService.sendNotification(
                    message.userId(),
                    message.title(),
                    message.content(),
                    message.type()
            );

            log.info("通知消息处理完成: userId={}", message.userId());
        } catch (Exception e) {
            log.error("处理通知消息异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 处理积分队列消息
     */
    @RabbitListener(queues = MQConstants.CREDIT_QUEUE)
    public void handleCreditMessage(CreditMessage message) {
        try {
            log.info("开始处理积分消息: userId={}, credit={}, reason={}",
                    message.userId(), message.credit(), message.reason());

            creditService.addCredit(
                    message.userId(),
                    message.credit(),
                    message.reason()
            );

            log.info("积分消息处理完成: userId={}", message.userId());
        } catch (Exception e) {
            log.error("处理积分消息异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 处理订单取消队列消息 (来自死信队列)
     */
    @RabbitListener(queues = MQConstants.ORDER_CANCEL_QUEUE)
    public void handleOrderCancelMessage(OrderDelayMessage message) {
        try {
            if (message.orderId() == null) {
                log.warn("收到无效的订单取消消息: {}", message);
                return;
            }

            log.info("开始处理订单取消: orderId={}, orderNo={}", message.orderId(), message.orderNo());

            orderRepository.findById(message.orderId()).ifPresent(order -> {
                // 仅取消待支付状态的订单
                if ("PENDING".equals(order.getStatus())) {
                    cancelOrder(order);
                    log.info("订单已自动取消 (超时): orderNo={}", order.getOrderNo());
                } else {
                    log.info("订单状态已变更，无需取消: orderNo={}, status={}",
                            order.getOrderNo(), order.getStatus());
                }
            });

            log.info("订单取消消息处理完成: orderId={}", message.orderId());
        } catch (Exception e) {
            log.error("处理订单取消消息异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 取消订单
     */
    private void cancelOrder(Order order) {
        order.setStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelReason("支付超时自动取消");

        // 恢复库存
        order.getItem().forEach(item -> {
            var product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        });

        orderRepository.save(order);
    }
}
