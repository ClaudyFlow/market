package com.market.controller;

import com.market.annotation.*;
import com.market.common.Result;
import com.market.dto.UserNotificationResponse;
import com.market.entity.User;
import com.market.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 * 提供用户通知的查询、已读/未读管理、删除、分类通知列表、通知偏好设置等功能。
 * 权限要求：需要登录
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/notification
 */
@RestController
@RequestMapping("/api/notification")
@CrossOrigin(origins = "*")
public class UserNotificationController {

    @Autowired
    private UserNotificationService notificationService;

    /**
     * 获取通知列表
     * API路径：GET /api/notification
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param type 通知类型（可选）
     * @return 分页的通知列表
     */
    @GetMapping
    @Cacheable(key = "'notifications_' + #user.id + '_' + #page + '_' + #type",
               cacheName = "notifications", expire = 300)
    @AuditLog(module = "通知管理", action = "查询通知列表")
    public Result<Page<UserNotificationResponse>> getNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserNotificationResponse> notifications = notificationService.getNotificationsByType(user.getId(), type, pageable);
        return Result.success(notifications);
    }

    /**
     * 获取通知详情
     * API路径：GET /api/notification/{id}
     * 权限：需要登录
     *
     * @param id 通知ID
     * @param user 当前登录用户
     * @return 通知详情
     */
    @GetMapping("/{id}")
    @Cacheable(key = "'notification_detail_' + #id", cacheName = "notifications", expire = 600)
    @AuditLog(module = "通知管理", action = "查询通知详情")
    public Result<UserNotificationResponse> getNotificationDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            UserNotificationResponse notification = notificationService.getNotificationDetail(user.getId(), id);
            return Result.success(notification);
        } catch (RuntimeException e) {
            return Result.error(404, e.getMessage());
        }
    }

    /**
     * 标记通知为已读
     * API路径：PUT /api/notification/{id}/read
     * 权限：需要登录
     *
     * @param id 通知ID
     * @param user 当前登录用户
     * @return 标记数量
     */
    @PutMapping("/{id}/read")
    @Idempotent(key = "'mark_read_' + #id + '_' + #user.id", expire = 600)
    @AuditLog(module = "通知管理", action = "标记通知已读")
    public Result<Integer> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        int count = notificationService.markAsRead(user.getId(), List.of(id));
        return Result.success(count);
    }

    /**
     * 批量标记通知为已读
     * API路径：PUT /api/notification/batch-read
     * 权限：需要登录
     *
     * @param ids 通知ID列表
     * @param user 当前登录用户
     * @return 标记数量
     */
    @PutMapping("/batch-read")
    @Idempotent(key = "'batch_mark_read_' + #user.id", expire = 600)
    @AuditLog(module = "通知管理", action = "批量标记通知已读")
    public Result<Integer> batchMarkAsRead(
            @RequestBody List<Long> ids,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        int count = notificationService.markAsRead(user.getId(), ids);
        return Result.success(count);
    }

    /**
     * 全部标记为已读
     * API路径：PUT /api/notification/all-read
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 标记数量
     */
    @PutMapping("/all-read")
    @Idempotent(key = "'mark_all_read_' + #user.id", expire = 600)
    @AuditLog(module = "通知管理", action = "全部标记已读")
    public Result<Integer> markAllAsRead(@AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        int count = notificationService.markAllAsRead(user.getId());
        return Result.success(count);
    }

    /**
     * 删除通知
     * API路径：DELETE /api/notification/{id}
     * 权限：需要登录
     *
     * @param id 通知ID
     * @param user 当前登录用户
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Idempotent(key = "'delete_notification_' + #id", expire = 600)
    @AuditLog(module = "通知管理", action = "删除通知")
    public Result<Void> deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            notificationService.deleteNotification(user.getId(), id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(404, e.getMessage());
        }
    }

    /**
     * 批量删除通知
     * API路径：DELETE /api/notification/batch-delete
     * 权限：需要登录
     *
     * @param ids 通知ID列表
     * @param user 当前登录用户
     * @return 删除数量
     */
    @DeleteMapping("/batch-delete")
    @Idempotent(key = "'batch_delete_' + #user.id", expire = 600)
    @AuditLog(module = "通知管理", action = "批量删除通知")
    public Result<Integer> batchDeleteNotifications(
            @RequestBody List<Long> ids,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        int count = notificationService.batchDeleteNotifications(user.getId(), ids);
        return Result.success(count);
    }

    /**
     * 清空通知
     * API路径：DELETE /api/notification/clear
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 清空数量
     */
    @DeleteMapping("/clear")
    @Idempotent(key = "'clear_notifications_' + #user.id", expire = 600)
    @AuditLog(module = "通知管理", action = "清空通知", logLevel = AuditLog.LogLevel.WARNING)
    public Result<Integer> clearNotifications(@AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        int count = notificationService.clearAllNotifications(user.getId());
        return Result.success(count);
    }

    /**
     * 获取未读通知数量
     * API路径：GET /api/notification/unread-count
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 未读通知数量
     */
    @GetMapping("/unread-count")
    @Cacheable(key = "'unread_count_' + #user.id", cacheName = "notifications", expire = 60)
    @AuditLog(module = "通知管理", action = "查询未读数量")
    public Result<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        long count = notificationService.getUnreadCount(user.getId());
        return Result.success(Map.of("count", count));
    }

    /**
     * 获取通知统计
     * API路径：GET /api/notification/stats
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 通知统计数据
     */
    @GetMapping("/stats")
    @Cacheable(key = "'notification_stats_' + #user.id", cacheName = "notifications", expire = 300)
    @AuditLog(module = "通知管理", action = "查询通知统计")
    public Result<Map<String, Object>> getNotificationStats(@AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Map<String, Object> stats = notificationService.getNotificationStats(user.getId());
        return Result.success(stats);
    }

    /**
     * 获取系统通知列表
     * API路径：GET /api/notification/system
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 分页的系统通知列表
     */
    @GetMapping("/system")
    @Cacheable(key = "'system_notifications_' + #user.id + '_' + #page",
               cacheName = "notifications", expire = 300)
    @AuditLog(module = "通知管理", action = "查询系统通知")
    public Result<Page<UserNotificationResponse>> getSystemNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserNotificationResponse> notifications = notificationService.getNotificationsByType(user.getId(), "SYSTEM", pageable);
        return Result.success(notifications);
    }

    /**
     * 获取活动通知列表
     * API路径：GET /api/notification/activity
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 分页的活动通知列表
     */
    @GetMapping("/activity")
    @Cacheable(key = "'activity_notifications_' + #user.id + '_' + #page",
               cacheName = "notifications", expire = 300)
    @AuditLog(module = "通知管理", action = "查询活动通知")
    public Result<Page<UserNotificationResponse>> getActivityNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserNotificationResponse> notifications = notificationService.getNotificationsByType(user.getId(), "ACTIVITY", pageable);
        return Result.success(notifications);
    }

    /**
     * 获取订单通知列表
     * API路径：GET /api/notification/order
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 分页的订单通知列表
     */
    @GetMapping("/order")
    @Cacheable(key = "'order_notifications_' + #user.id + '_' + #page",
               cacheName = "notifications", expire = 300)
    @AuditLog(module = "通知管理", action = "查询订单通知")
    public Result<Page<UserNotificationResponse>> getOrderNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserNotificationResponse> notifications = notificationService.getNotificationsByType(user.getId(), "ORDER", pageable);
        return Result.success(notifications);
    }

    /**
     * 获取最新通知
     * API路径：GET /api/notification/latest
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param limit 数量限制，默认5
     * @return 最新通知列表
     */
    @GetMapping("/latest")
    @Cacheable(key = "'latest_notifications_' + #user.id + '_' + #limit",
               cacheName = "notifications", expire = 60)
    @AuditLog(module = "通知管理", action = "查询最新通知")
    public Result<List<UserNotificationResponse>> getLatestNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "5") Integer limit) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        List<UserNotificationResponse> notifications = notificationService.getLatestNotifications(user.getId(), limit);
        return Result.success(notifications);
    }

    /**
     * 设置通知偏好
     * API路径：PUT /api/notification/preference
     * 权限：需要登录
     *
     * @param preference 通知偏好配置
     * @param user 当前登录用户
     * @return 操作结果
     */
    @PutMapping("/preference")
    @Idempotent(key = "'set_notification_preference_' + #user.id", expire = 600)
    @AuditLog(module = "通知管理", action = "设置通知偏好")
    public Result<Void> setNotificationPreference(
            @RequestBody Map<String, Boolean> preference,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        return Result.success(null);
    }

    /**
     * 获取通知偏好
     * API路径：GET /api/notification/preference
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 通知偏好配置
     */
    @GetMapping("/preference")
    @Cacheable(key = "'notification_preference_' + #user.id", cacheName = "notifications", expire = 3600)
    @AuditLog(module = "通知管理", action = "查询通知偏好")
    public Result<Map<String, Boolean>> getNotificationPreference(@AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Map<String, Boolean> preference = Map.of(
            "systemNotify", true,
            "activityNotify", true,
            "orderNotify", true,
            "promoNotify", true
        );
        return Result.success(preference);
    }
}
