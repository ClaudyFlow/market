package com.market.dto.mq;

import java.io.Serializable;
import java.util.Map;

/**
 * 邮件消息 DTO
 */
public record EmailMessage(
        String to,
        String subject,
        String content,
        EmailType type,
        Map<String, Object> extraData
) implements Serializable {

    public enum EmailType {
        VERIFICATION,  // 验证码
        WELCOME,       // 欢迎邮件
        ORDER,         // 订单通知
        PROMOTION      // 营销推广
    }

    /**
     * 创建验证码邮件消息
     */
    public static EmailMessage verification(String to, String code, int expireMinutes) {
        return new EmailMessage(
                to,
                "Market 验证码",
                "您的验证码是：" + code + "，有效期为" + expireMinutes + "分钟。\n\n如果这不是您本人操作，请忽略此邮件。",
                EmailType.VERIFICATION,
                Map.of("code", code, "expireMinutes", expireMinutes)
        );
    }

    /**
     * 创建欢迎邮件消息
     */
    public static EmailMessage welcome(String to, String username) {
        return new EmailMessage(
                to,
                "欢迎注册 Market！",
                "亲爱的 " + username + "：\n\n欢迎加入 Market！\n您的账号已成功注册。\n\n如有任何问题，请联系我们的客服团队。\n\n祝您购物愉快！\nMarket 团队",
                EmailType.WELCOME,
                Map.of("username", username)
        );
    }
}
