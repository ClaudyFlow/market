package com.market.service;

import com.market.dto.UserNotificationResponse;
import com.market.entity.UserNotification;
import com.market.repository.UserNotificationRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户通知服务实现类
 *
 * @author Market Team
 * @since 1.0.0
 */
@Service
public class UserNotificationServiceImpl implements UserNotificationService {

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Override
    @Transactional
    public Long sendNotification(Long userId, String title, String content, String type,
                                  String level, String bizType, Long bizId, String jumpUrl) {
        UserNotification notification = new UserNotification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setLevel(level != null ? level : "INFO");
        notification.setBizType(bizType);
        notification.setBizId(bizId);
        notification.setJumpUrl(jumpUrl);

        UserNotification saved = userNotificationRepository.save(notification);
        return saved.getId();
    }

    @Override
    public Page<UserNotificationResponse> getNotifications(Long userId, Pageable pageable) {
        return userNotificationRepository.findByUserId(userId, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<UserNotificationResponse> getNotificationsList(Long userId) {
        return userNotificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(Long userId) {
        return userNotificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public UserNotificationResponse getNotificationDetail(Long userId, Long notificationId) {
        UserNotification notification = userNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该通知");
        }

        return convertToResponse(notification);
    }

    @Override
    @Transactional
    public int markAsRead(Long userId, List<Long> notificationIds) {
        // 验证通知是否属于该用户
        for (Long notificationId : notificationIds) {
            UserNotification notification = userNotificationRepository.findById(notificationId)
                    .orElseThrow(() -> new RuntimeException("通知不存在: " + notificationId));
            if (!notification.getUserId().equals(userId)) {
                throw new RuntimeException("无权修改该通知");
            }
        }

        return userNotificationRepository.markAsRead(notificationIds);
    }

    @Override
    @Transactional
    public int markAllAsRead(Long userId) {
        return userNotificationRepository.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public boolean deleteNotification(Long userId, Long notificationId) {
        UserNotification notification = userNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该通知");
        }

        userNotificationRepository.delete(notification);
        return true;
    }

    @Override
    @Transactional
    public int cleanOldNotifications(Long userId, int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return userNotificationRepository.deleteOldNotifications(userId, cutoffDate);
    }

    /**
     * 将 UserNotification 转换为 UserNotificationResponse
     */
    private UserNotificationResponse convertToResponse(UserNotification notification) {
        UserNotificationResponse response = new UserNotificationResponse();
        response.setId(notification.getId());
        response.setUserId(notification.getUserId());
        response.setTitle(notification.getTitle());
        response.setContent(notification.getContent());
        response.setType(notification.getType());
        response.setLevel(notification.getLevel());
        response.setIsRead(notification.getIsRead());
        response.setBizType(notification.getBizType());
        response.setBizId(notification.getBizId());
        response.setJumpUrl(notification.getJumpUrl());
        response.setCreatedAt(notification.getCreatedAt());
        response.setReadAt(notification.getReadAt());
        return response;
    }
}
