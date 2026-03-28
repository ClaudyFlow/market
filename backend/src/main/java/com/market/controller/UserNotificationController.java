package com.market.controller;

import com.market.dto.UserNotificationResponse;
import com.market.entity.User;
import com.market.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户通知控制器
 *
 * @author Market Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/notification")
@CrossOrigin(origins = "*")
public class UserNotificationController {

    @Autowired
    private UserNotificationService userNotificationService;

    /**
     * 获取通知列表（分页）
     */
    @GetMapping("/list")
    public ResponseEntity<Page<UserNotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserNotificationResponse> notifications = userNotificationService.getNotifications(
                user.getId(), pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 获取通知列表
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserNotificationResponse>> getAllNotifications(
            @AuthenticationPrincipal User user) {
        List<UserNotificationResponse> notifications = userNotificationService
                .getNotificationsList(user.getId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal User user) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", userNotificationService.getUnreadCount(user.getId()));
        return ResponseEntity.ok(result);
    }

    /**
     * 获取通知详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserNotificationResponse> getNotificationDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        UserNotificationResponse notification = userNotificationService
                .getNotificationDetail(user.getId(), id);
        return ResponseEntity.ok(notification);
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @RequestBody List<Long> notificationIds,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        int count = userNotificationService.markAsRead(user.getId(), notificationIds);
        result.put("success", true);
        result.put("count", count);
        result.put("message", "已标记 " + count + " 条通知为已读");
        return ResponseEntity.ok(result);
    }

    /**
     * 标记所有通知为已读
     */
    @PostMapping("/read/all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        int count = userNotificationService.markAllAsRead(user.getId());
        result.put("success", true);
        result.put("count", count);
        result.put("message", "已标记所有通知为已读");
        return ResponseEntity.ok(result);
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userNotificationService.deleteNotification(user.getId(), id);
        result.put("success", success);
        result.put("message", "通知删除成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 清理旧通知
     */
    @PostMapping("/clean")
    public ResponseEntity<Map<String, Object>> cleanOldNotifications(
            @RequestParam(defaultValue = "30") int days,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        int count = userNotificationService.cleanOldNotifications(user.getId(), days);
        result.put("success", true);
        result.put("count", count);
        result.put("message", "已清理 " + count + " 条旧通知");
        return ResponseEntity.ok(result);
    }
}
