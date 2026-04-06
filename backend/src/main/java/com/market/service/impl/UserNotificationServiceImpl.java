package com.market.service.impl;

import com.market.dto.UserNotificationResponse;
import com.market.entity.UserNotification;
import com.market.repository.UserNotificationRepository;
import com.market.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户通知服务实现类
 */
@Service
public class UserNotificationServiceImpl implements UserNotificationService {

    @Autowired
    private UserNotificationRepository notificationRepository;

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
        notification.setIsRead(false);

        UserNotification saved = notificationRepository.save(notification);
        return saved.getId();
    }

    @Override
    public Page<UserNotificationResponse> getNotifications(Long userId, Pageable pageable) {
        Page<UserNotification> page = notificationRepository.findByUserId(userId, pageable);
        return new PageImpl<>(page.getContent().stream()
            .map(this::toResponse)
            .collect(Collectors.toList()), pageable, page.getTotalElements());
    }

    @Override
    public List<UserNotificationResponse> getNotificationsList(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public UserNotificationResponse getNotificationDetail(Long userId, Long notificationId) {
        UserNotification notification = notificationRepository.findById(notificationId)
            .filter(n -> n.getUserId().equals(userId))
            .orElseThrow(() -> new RuntimeException("通知不存在"));
        
        // 标记为已读
        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
        
        return toResponse(notification);
    }

    @Override
    @Transactional
    public int markAsRead(Long userId, List<Long> notificationIds) {
        List<UserNotification> notifications = notificationRepository.findAllById(notificationIds);
        int count = 0;
        for (UserNotification notification : notifications) {
            if (notification.getUserId().equals(userId) && !notification.getIsRead()) {
                notification.setIsRead(true);
                notification.setReadAt(LocalDateTime.now());
                notificationRepository.save(notification);
                count++;
            }
        }
        return count;
    }

    @Override
    @Transactional
    public int markAllAsRead(Long userId) {
        List<UserNotification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        for (UserNotification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(unreadNotifications);
        return unreadNotifications.size();
    }

    @Override
    @Transactional
    public boolean deleteNotification(Long userId, Long notificationId) {
        UserNotification notification = notificationRepository.findById(notificationId)
            .filter(n -> n.getUserId().equals(userId))
            .orElseThrow(() -> new RuntimeException("通知不存在"));
        
        notificationRepository.delete(notification);
        return true;
    }

    @Override
    @Transactional
    public int cleanOldNotifications(Long userId, int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        // 使用分页查询获取所有旧通知
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 1000);
        List<UserNotification> oldNotifications = notificationRepository.findByUserId(userId, pageable).getContent().stream()
            .filter(n -> n.getCreatedAt().isBefore(cutoffDate))
            .collect(java.util.stream.Collectors.toList());
        notificationRepository.deleteAll(oldNotifications);
        return oldNotifications.size();
    }

    /**
     * 转换为响应对象
     */
    private UserNotificationResponse toResponse(UserNotification notification) {
        return UserNotificationResponse.builder()
            .id(notification.getId())
            .userId(notification.getUserId())
            .title(notification.getTitle())
            .content(notification.getContent())
            .type(notification.getType())
            .level(notification.getLevel())
            .isRead(notification.getIsRead())
            .bizType(notification.getBizType())
            .bizId(notification.getBizId())
            .jumpUrl(notification.getJumpUrl())
            .createdAt(notification.getCreatedAt())
            .readAt(notification.getReadAt())
            .build();
    }

    @Override
    public Page<UserNotificationResponse> getNotificationsByType(Long userId, String type, Pageable pageable) {
        Page<UserNotification> page;
        if (type != null && !type.isEmpty()) {
            page = notificationRepository.findByUserIdAndType(userId, type, pageable);
        } else {
            page = notificationRepository.findByUserId(userId, pageable);
        }
        return new PageImpl<>(page.getContent().stream()
            .map(this::toResponse)
            .collect(Collectors.toList()), pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public int batchDeleteNotifications(Long userId, List<Long> notificationIds) {
        List<UserNotification> notifications = notificationRepository.findAllById(notificationIds);
        int count = 0;
        for (UserNotification notification : notifications) {
            if (notification.getUserId().equals(userId)) {
                notificationRepository.delete(notification);
                count++;
            }
        }
        return count;
    }

    @Override
    @Transactional
    public int clearAllNotifications(Long userId) {
        List<UserNotification> allNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        notificationRepository.deleteAll(allNotifications);
        return allNotifications.size();
    }

    @Override
    public Map<String, Object> getNotificationStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        long total = notificationRepository.countByUserId(userId);
        long unread = notificationRepository.countByUserIdAndIsReadFalse(userId);
        long systemUnread = notificationRepository.countByUserIdAndTypeAndIsReadFalse(userId, "SYSTEM", false);
        long activityUnread = notificationRepository.countByUserIdAndTypeAndIsReadFalse(userId, "ACTIVITY", false);
        long orderUnread = notificationRepository.countByUserIdAndTypeAndIsReadFalse(userId, "ORDER", false);
        long promotionUnread = notificationRepository.countByUserIdAndTypeAndIsReadFalse(userId, "PROMOTION", false);

        stats.put("total", total);
        stats.put("unread", unread);
        stats.put("systemUnread", systemUnread);
        stats.put("activityUnread", activityUnread);
        stats.put("orderUnread", orderUnread);
        stats.put("promotionUnread", promotionUnread);
        return stats;
    }

    @Override
    public List<UserNotificationResponse> getLatestNotifications(Long userId, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return notificationRepository.findByUserId(userId, pageable).getContent().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
