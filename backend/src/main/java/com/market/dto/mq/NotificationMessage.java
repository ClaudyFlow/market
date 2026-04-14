package com.market.dto.mq;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知消息 DTO
 */
public record NotificationMessage(
        Long userId,
        String title,
        String content,
        String type,
        LocalDateTime timestamp
) implements Serializable {

    /**
     * 创建订单通知消息
     */
    public static NotificationMessage order(Long userId, String orderNo, String status, String message) {
        return new NotificationMessage(
                userId,
                "订单状态更新",
                String.format("您的订单 %s 状态已更新为：%s", orderNo, message),
                "ORDER",
                LocalDateTime.now()
        );
    }

    /**
     * 创建支付成功通知
     */
    public static NotificationMessage paymentSuccess(Long userId, String orderNo, Double amount) {
        return new NotificationMessage(
                userId,
                "支付成功",
                String.format("您的订单 %s 已支付成功，支付金额：%.2f 元", orderNo, amount),
                "PAYMENT",
                LocalDateTime.now()
        );
    }

    /**
     * 创建物流通知
     */
    public static NotificationMessage logistics(Long userId, String trackingNo, String status) {
        return new NotificationMessage(
                userId,
                "物流状态更新",
                String.format("您的包裹 %s 状态已更新：%s", trackingNo, status),
                "LOGISTICS",
                LocalDateTime.now()
        );
    }

    /**
     * 创建通用通知
     */
    public static NotificationMessage general(Long userId, String title, String content, String type) {
        return new NotificationMessage(userId, title, content, type, LocalDateTime.now());
    }
}
