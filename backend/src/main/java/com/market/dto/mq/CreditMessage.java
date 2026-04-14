package com.market.dto.mq;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 积分消息 DTO
 */
public record CreditMessage(
        Long userId,
        Integer credit,
        String reason,
        CreditType type
) implements Serializable {

    public enum CreditType {
        EARN,    // 获得积分
        CONSUME, // 消费积分
        EXPIRE,  // 积分过期
        REFUND   // 退款退还
    }

    /**
     * 创建积分奖励消息
     */
    public static CreditMessage reward(Long userId, Integer credit, String reason) {
        return new CreditMessage(userId, credit, reason, CreditType.EARN);
    }

    /**
     * 创建订单积分奖励
     */
    public static CreditMessage orderReward(Long userId, BigDecimal orderAmount, String orderNo) {
        int credit = orderAmount.intValue() / 10;
        return new CreditMessage(
                userId,
                credit,
                "订单奖励：" + orderNo,
                CreditType.EARN
        );
    }
}
