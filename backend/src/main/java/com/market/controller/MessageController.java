package com.market.controller;

import com.market.entity.MessageReceive;
import com.market.entity.SystemMessage;
import com.market.entity.User;
import com.market.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息通知控制器
 */
@RestController
@RequestMapping("/api/message")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取我的消息列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getMessages(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal User user) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<MessageReceive> messagePage = messageService.getUserMessages(user, pageable);

        List<Map<String, Object>> messageList = messagePage.getContent().stream()
            .map(receive -> {
                SystemMessage message = messageService.getMessageDetail(receive.getMessageId());
                Map<String, Object> map = new HashMap<>();
                map.put("id", receive.getId());
                map.put("messageId", message.getId());
                map.put("title", message.getTitle());
                map.put("content", message.getContent());
                map.put("type", message.getType());
                map.put("priority", message.getPriority());
                map.put("imageUrl", message.getImageUrl());
                map.put("jumpUrl", message.getJumpUrl());
                map.put("sendTime", message.getSendTime());
                map.put("isRead", receive.getIsRead());
                map.put("createdAt", receive.getCreatedAt());
                return map;
            })
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", messageList);
        response.put("total", messagePage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取未读消息
     */
    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadMessages(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal User user) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<MessageReceive> messagePage = messageService.getUnreadMessages(user, pageable);

        List<Map<String, Object>> messageList = messagePage.getContent().stream()
            .map(receive -> {
                SystemMessage message = messageService.getMessageDetail(receive.getMessageId());
                Map<String, Object> map = new HashMap<>();
                map.put("id", receive.getId());
                map.put("messageId", message.getId());
                map.put("title", message.getTitle());
                map.put("content", message.getContent());
                map.put("type", message.getType());
                map.put("priority", message.getPriority());
                map.put("imageUrl", message.getImageUrl());
                map.put("jumpUrl", message.getJumpUrl());
                map.put("sendTime", message.getSendTime());
                map.put("isRead", false);
                map.put("createdAt", receive.getCreatedAt());
                return map;
            })
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", messageList);
        response.put("total", messagePage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", messageService.getUnreadCount(user));
        return ResponseEntity.ok(result);
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @RequestBody List<Long> receiveIds,
            @AuthenticationPrincipal User user) {

        int count = messageService.markAsRead(user, receiveIds);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", count);
        result.put("message", "已标记 " + count + " 条消息为已读");

        return ResponseEntity.ok(result);
    }

    /**
     * 标记所有消息为已读
     */
    @PostMapping("/read/all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@AuthenticationPrincipal User user) {
        int count = messageService.markAllAsRead(user);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", count);
        result.put("message", "已标记所有消息为已读");

        return ResponseEntity.ok(result);
    }

    /**
     * 获取消息详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMessageDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        SystemMessage message = messageService.getMessageDetail(id);

        Map<String, Object> result = new HashMap<>();
        result.put("id", message.getId());
        result.put("title", message.getTitle());
        result.put("content", message.getContent());
        result.put("type", message.getType());
        result.put("priority", message.getPriority());
        result.put("imageUrl", message.getImageUrl());
        result.put("jumpUrl", message.getJumpUrl());
        result.put("sendTime", message.getSendTime());

        return ResponseEntity.ok(result);
    }

    /**
     * 获取消息统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMessageStats(@AuthenticationPrincipal User user) {
        Map<String, Object> stats = messageService.getMessageStats(user);
        return ResponseEntity.ok(stats);
    }

    // ==================== 管理员接口 ====================

    /**
     * 发送系统消息（管理员）
     */
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "SYSTEM") String type,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) String jumpUrl,
            @RequestParam(required = false) String imageUrl,
            @AuthenticationPrincipal User user) {

        SystemMessage message = messageService.sendMessage(title, content, type, priority, jumpUrl, imageUrl);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("messageId", message.getId());
        result.put("message", "消息发送成功");

        return ResponseEntity.ok(result);
    }

    /**
     * 发送消息给指定用户（管理员）
     */
    @PostMapping("/send/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> sendMessageToUser(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "SYSTEM") String type,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) String jumpUrl,
            @AuthenticationPrincipal User user) {

        SystemMessage message = messageService.sendMessageToUser(title, content, type, userId, priority, jumpUrl);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("messageId", message.getId());
        result.put("message", "消息发送成功");

        return ResponseEntity.ok(result);
    }

    /**
     * 发送消息给多个用户（管理员）
     */
    @PostMapping("/send/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> sendMessageToUsers(
            @RequestBody List<Long> userIds,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "SYSTEM") String type,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) String jumpUrl,
            @AuthenticationPrincipal User user) {

        SystemMessage message = messageService.sendMessageToUsers(title, content, type, userIds, priority, jumpUrl);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("messageId", message.getId());
        result.put("message", "消息发送成功");

        return ResponseEntity.ok(result);
    }
}
