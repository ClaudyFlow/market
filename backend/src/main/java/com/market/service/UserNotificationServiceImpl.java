package com.market.service;

import com.market.dto.UserNotificationResponse;
import com.market.entity.UserNotification;
import com.market.repository.UserNotificationRepository;
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
 *
 * @author Market Team
 * @since 1.0.0
 */
@Service
public class UserNotificationServiceImpl implements UserNotificationService {

    @Autowired
    private UserNotificationRepository notificationRepository;

    @Override
    @Transactional
    public Long sendNotification(Long userId, String title, String content, String type,
                                  String level, String bizType, Long bizId, String jumpUrl) {
        UserNotification notification = new UserNotification(userId, title, content, type);
        notification.setLevel(level != null ? level : "INFO");
        notification.setBizType(bizType);
        notification.setBizId(bizId);
        notification.setJumpUrl(jumpUrl);
        
        UserNotification saved = notificationRepository.save(notification);
        return saved.getId();
    }

    @Override
    public Page<UserNotificationResponse> getNotifications(Long userId, Pageable pageable) {
        Page<UserNotification> notifications = notificationRepository.findByUserId(userId, pageable);
        return notifications.map(this::convertToResponse);
    }

    @Override
    public List<UserNotificationResponse> getNotificationsList(Long userId) {
        List<UserNotification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public UserNotificationResponse getNotificationDetail(Long userId, Long notificationId) {
        UserNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该通知");
        }
        
        // 自动标记为已读
        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
        
        return convertToResponse(notification);
    }

    @Override
    @Transactional
    public int markAsRead(Long userId, List<Long> notificationIds) {
        // 验证通知是否属于该用户
        for (Long id : notificationIds) {
            UserNotification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("通知不存在: " + id));
            if (!notification.getUserId().equals(userId)) {
                throw new RuntimeException("无权操作该通知");
            }
        }
        
        return notificationRepository.markAsRead(notificationIds);
    }

    @Override
    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public boolean deleteNotification(Long userId, Long notificationId) {
        UserNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该通知");
        }
        
        notificationRepository.delete(notification);
        return true;
    }

    @Override
    @Transactional
    public int cleanOldNotifications(Long userId, int days) {
        LocalDateTime before = LocalDateTime.now().minusDays(days);
        return notificationRepository.deleteOldNotifications(userId, before);
    }

    @Override
    public Page<UserNotificationResponse> getNotificationsByType(Long userId, String type, Pageable pageable) {
        Page<UserNotification> notifications = notificationRepository.findByUserIdAndType(userId, type, pageable);
        return notifications.map(this::convertToResponse);
    }

    @Override
    @Transactional
    public int batchDeleteNotifications(Long userId, List<Long> notificationIds) {
        int count = 0;
        for (Long id : notificationIds) {
            try {
                deleteNotification(userId, id);
                count++;
            } catch (Exception e) {
                // 继续删除其他
            }
        }
        return count;
    }

    @Override
    @Transactional
    public int clearAllNotifications(Long userId) {
        int count = (int) notificationRepository.countByUserId(userId);
        notificationRepository.deleteByUserId(userId);
        return count;
    }

    @Override
    public Map<String, Object> getNotificationStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        long total = notificationRepository.countByUserId(userId);
        long unread = notificationRepository.countByUserIdAndIsReadFalse(userId);
        long systemUnread = notificationRepository.countByUserIdAndTypeAndIsReadFalse(userId, "SYSTEM", false);
        long orderUnread = notificationRepository.countByUserIdAndTypeAndIsReadFalse(userId, "ORDER", false);
        long promotionUnread = notificationRepository.countByUserIdAndTypeAndIsReadFalse(userId, "PROMOTION", false);
        
        stats.put("total", total);
        stats.put("unread", unread);
        stats.put("systemUnread", systemUnread);
        stats.put("orderUnread", orderUnread);
        stats.put("promotionUnread", promotionUnread);
        
        return stats;
    }

    @Override
    public List<UserNotificationResponse> getLatestNotifications(Long userId, int limit) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit, 
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        
        Page<UserNotification> notifications = notificationRepository.findByUserId(userId, pageable);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 转换为响应 DTO
     */
    private UserNotificationResponse convertToResponse(UserNotification notification) {
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
}
