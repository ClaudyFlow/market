package com.market.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息队列配置
 */
@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${market.mq.email-queue:market.email.queue}")
    private String emailQueue;

    @Value("${market.mq.notification-queue:market.notification.queue}")
    private String notificationQueue;

    @Value("${market.mq.order-cancel-queue:market.order.cancel.queue}")
    private String orderCancelQueue;

    @Value("${market.mq.credit-queue:market.credit.queue}")
    private String creditQueue;

    @Value("${market.mq.audit-log-queue:market.audit.log.queue}")
    private String auditLogQueue;

    @Value("${market.mq.exchange:market.exchange}")
    private String marketExchange;

    @Value("${market.mq.order-delay-exchange:market.order.delay.exchange}")
    private String orderDelayExchange;

    @Value("${market.mq.order-delay-queue:market.order.delay.queue}")
    private String orderDelayQueue;

    @Value("${market.mq.order-cancel-dlq:market.order.cancel.dlq}")
    private String orderCancelDLQ;

    /**
     * 消息转换器 (JSON)
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate 配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    /**
     * 主交换机 (Topic Exchange)
     */
    @Bean
    public TopicExchange marketExchange() {
        return new TopicExchange(marketExchange, true, false);
    }

    /**
     * 邮件队列
     */
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueue).build();
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(marketExchange())
                .with("email.#");
    }

    /**
     * 通知队列
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(notificationQueue).build();
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(marketExchange())
                .with("notification.#");
    }

    /**
     * 积分队列
     */
    @Bean
    public Queue creditQueue() {
        return QueueBuilder.durable(creditQueue).build();
    }

    @Bean
    public Binding creditBinding() {
        return BindingBuilder.bind(creditQueue())
                .to(marketExchange())
                .with("credit.#");
    }

    /**
     * 审计日志队列
     */
    @Bean
    public Queue auditLogQueue() {
        return QueueBuilder.durable(auditLogQueue).build();
    }

    @Bean
    public Binding auditLogBinding() {
        return BindingBuilder.bind(auditLogQueue())
                .to(marketExchange())
                .with("audit.#");
    }

    /**
     * 订单延迟交换机 (用于订单超时取消)
     */
    @Bean
    public DirectExchange orderDelayExchange() {
        return ExchangeBuilder.directExchange(orderDelayExchange)
                .durable(true)
                .delayed()
                .build();
    }

    /**
     * 订单延迟队列 (绑定死信队列)
     */
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(orderDelayQueue)
                .withArgument("x-dead-letter-exchange", marketExchange)
                .withArgument("x-dead-letter-routing-key", "order.cancel")
                .build();
    }

    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue())
                .to(orderDelayExchange())
                .with("order.delay");
    }

    /**
     * 订单取消队列 (死信队列)
     */
    @Bean
    public Queue orderCancelQueue() {
        return QueueBuilder.durable(orderCancelQueue).build();
    }

    @Bean
    public Binding orderCancelBinding() {
        return BindingBuilder.bind(orderCancelQueue())
                .to(marketExchange())
                .with("order.cancel");
    }

    /**
     * 统一绑定所有队列和交换机
     */
    @Bean
    public Declarables bindings(
            Binding emailBinding,
            Binding notificationBinding,
            Binding creditBinding,
            Binding auditLogBinding,
            Binding orderDelayBinding,
            Binding orderCancelBinding) {
        return new Declarables(
                emailBinding,
                notificationBinding,
                creditBinding,
                auditLogBinding,
                orderDelayBinding,
                orderCancelBinding
        );
    }
}
