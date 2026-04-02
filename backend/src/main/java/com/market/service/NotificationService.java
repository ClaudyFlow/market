package com.market.service;

import com.market.entity.SystemMessage;
import com.market.entity.User;
import com.market.entity.UserNotification;
import com.market.repository.SystemMessageRepository;
import com.market.repository.UserNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知服务类（统一站内信 + WebSocket 推送）
 */
@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserNotificationRepository notificationRepository;

    @Autowired
    private SystemMessageRepository systemMessageRepository;

    /**
     * 发送站内通知给用户
     */
    @Async
    public void sendNotification(Long userId, String title, String content, String type) {
        UserNotification notification = new UserNotification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setUserId(userId);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);

        // 通过 WebSocket 推送实时通知
        sendWebSocketNotification(userId, title, content, type);
    }

    /**
     * 发送订单状态变更通知
     */
    @Async
    public void sendOrderNotification(Long userId, String orderNo, String status, String message) {
        String title = "订单状态更新";
        String content = String.format("您的订单 %s 状态已更新为：%s", orderNo, message);
        sendNotification(userId, title, content, "ORDER");
    }

    /**
     * 发送支付成功通知
     */
    @Async
    public void sendPaymentSuccessNotification(Long userId, String orderNo, Double amount) {
        String title = "支付成功";
        String content = String.format("您的订单 %s 已支付成功，支付金额：%.2f 元", orderNo, amount);
        sendNotification(userId, title, content, "PAYMENT");
    }

    /**
     * 发送物流状态变更通知
     */
    @Async
    public void sendLogisticsNotification(Long userId, String trackingNo, String status) {
        String title = "物流状态更新";
        String content = String.format("您的包裹 %s 状态已更新：%s", trackingNo, status);
        sendNotification(userId, title, content, "LOGISTICS");
    }

    /**
     * 发送优惠券通知
     */
    @Async
    public void sendCouponNotification(Long userId, String couponName, String message) {
        String title = "优惠券通知";
        String content = String.format("您%s：%s", message, couponName);
        sendNotification(userId, title, content, "COUPON");
    }

    /**
     * 发送系统公告（广播）
     */
    @Async
    public void sendBroadcastMessage(String title, String content, String type) {
        SystemMessage message = new SystemMessage();
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setPriority(1);
        message.setIsBroadcast(true);
        message.setSendTime(LocalDateTime.now());
        
        systemMessageRepository.save(message);

        // 通过 WebSocket 广播
        messagingTemplate.convertAndSend("/topic/system-message", mapMessage(message));
    }

    /**
     * 发送 WebSocket 实时通知
     */
    private void sendWebSocketNotification(Long userId, String title, String content, String type) {
        try {
            // 发送给用户私有队列
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notification",
                mapNotification(title, content, type)
            );
        } catch (Exception e) {
            // WebSocket 发送失败不影响站内信保存
        }
    }

    /**
     * 转换 SystemMessage 为 Map
     */
    private java.util.Map<String, Object> mapMessage(SystemMessage message) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", message.getId());
        map.put("title", message.getTitle());
        map.put("content", message.getContent());
        map.put("type", message.getType());
        map.put("sendTime", message.getSendTime());
        return map;
    }

    /**
     * 转换通知为 Map
     */
    private java.util.Map<String, Object> mapNotification(String title, String content, String type) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("type", type);
        map.put("time", LocalDateTime.now());
        return map;
    }

    /**
     * 获取用户未读通知数量
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 获取用户通知列表
     */
    public List<UserNotification> getUserNotifications(Long userId, int limit) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }
}
