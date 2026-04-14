package com.market.dto.mq;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单延迟取消消息 DTO
 */
public record OrderDelayMessage(
        Long orderId,
        String orderNo,
        Long userId,
        LocalDateTime createdAt
) implements Serializable {

    public static OrderDelayMessage of(Long orderId, String orderNo, Long userId) {
        return new OrderDelayMessage(orderId, orderNo, userId, LocalDateTime.now());
    }
}
