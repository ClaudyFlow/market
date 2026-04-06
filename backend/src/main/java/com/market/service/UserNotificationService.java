package com.market.service;

import com.market.dto.UserNotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 用户通知服务接口
 *
 * @author Market Team
 * @since 1.0.0
 */
public interface UserNotificationService {

    /**
     * 发送通知
     *
     * @param userId 用户 ID
     * @param title 标题
     * @param content 内容
     * @param type 类型
     * @param level 级别
     * @param bizType 业务类型
     * @param bizId 业务 ID
     * @param jumpUrl 跳转链接
     * @return 通知 ID
     */
    Long sendNotification(Long userId, String title, String content, String type,
                          String level, String bizType, Long bizId, String jumpUrl);

    /**
     * 获取用户通知列表（分页）
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 通知列表分页
     */
    Page<UserNotificationResponse> getNotifications(Long userId, Pageable pageable);

    /**
     * 获取用户通知列表
     *
     * @param userId 用户 ID
     * @return 通知列表
     */
    List<UserNotificationResponse> getNotificationsList(Long userId);

    /**
     * 获取未读通知数量
     *
     * @param userId 用户 ID
     * @return 未读通知数量
     */
    long getUnreadCount(Long userId);

    /**
     * 获取通知详情
     *
     * @param userId 用户 ID
     * @param notificationId 通知 ID
     * @return 通知详情
     */
    UserNotificationResponse getNotificationDetail(Long userId, Long notificationId);

    /**
     * 标记通知为已读
     *
     * @param userId 用户 ID
     * @param notificationIds 通知 ID 列表
     * @return 已读数量
     */
    int markAsRead(Long userId, List<Long> notificationIds);

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户 ID
     * @return 已读数量
     */
    int markAllAsRead(Long userId);

    /**
     * 删除通知
     *
     * @param userId 用户 ID
     * @param notificationId 通知 ID
     * @return 是否成功
     */
    boolean deleteNotification(Long userId, Long notificationId);

    /**
     * 清理旧通知
     *
     * @param userId 用户 ID
     * @param days 保留天数
     * @return 删除数量
     */
    int cleanOldNotifications(Long userId, int days);

    /**
     * 获取用户通知列表（带类型过滤，分页）
     *
     * @param userId 用户 ID
     * @param type 通知类型（可为空）
     * @param pageable 分页参数
     * @return 通知列表分页
     */
    Page<UserNotificationResponse> getNotificationsByType(Long userId, String type, Pageable pageable);

    /**
     * 批量删除通知
     *
     * @param userId 用户 ID
     * @param notificationIds 通知 ID 列表
     * @return 删除数量
     */
    int batchDeleteNotifications(Long userId, List<Long> notificationIds);

    /**
     * 清空用户所有通知
     *
     * @param userId 用户 ID
     * @return 删除数量
     */
    int clearAllNotifications(Long userId);

    /**
     * 获取通知统计
     *
     * @param userId 用户 ID
     * @return 统计数据 (total, unread, 各类型未读数)
     */
    java.util.Map<String, Object> getNotificationStats(Long userId);

    /**
     * 获取最新通知（限制条数）
     *
     * @param userId 用户 ID
     * @param limit 限制条数
     * @return 通知列表
     */
    List<UserNotificationResponse> getLatestNotifications(Long userId, int limit);
}
