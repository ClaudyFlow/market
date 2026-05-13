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
 * 提供用户消息接收、已读/未读管理、消息详情等功能，以及管理员发送系统消息的接口。
 * 权限要求：用户端需要登录，管理员端需要 ADMIN 角色
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/message
 */
@RestController
@RequestMapping("/api/message")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取我的消息列表
     * API路径：GET /api/message/list
     * 权限：需要登录
     *
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param user 当前登录用户
     * @return 分页的消息列表
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
     * API路径：GET /api/message/unread
     * 权限：需要登录
     *
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param user 当前登录用户
     * @return 分页的未读消息列表
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
     * API路径：GET /api/message/unread/count
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 未读消息数量
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", messageService.getUnreadCount(user));
        return ResponseEntity.ok(result);
    }

    /**
     * 标记消息为已读
     * API路径：POST /api/message/read
     * 权限：需要登录
     *
     * @param receiveIds 消息接收记录ID列表
     * @param user 当前登录用户
     * @return 标记结果
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
     * API路径：POST /api/message/read/all
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 标记结果
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
     * API路径：GET /api/message/{id}
     * 权限：需要登录
     *
     * @param id 消息ID
     * @param user 当前登录用户
     * @return 消息详情
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
     * API路径：GET /api/message/stats
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 消息统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMessageStats(@AuthenticationPrincipal User user) {
        Map<String, Object> stats = messageService.getMessageStats(user);
        return ResponseEntity.ok(stats);
    }

    // ==================== 管理员接口 ====================

    /**
     * 发送系统消息（管理员）
     * API路径：POST /api/message/send
     * 权限：需要 ADMIN 角色
     *
     * @param title 消息标题
     * @param content 消息内容
     * @param type 消息类型，默认SYSTEM
     * @param priority 优先级（可选）
     * @param jumpUrl 跳转链接（可选）
     * @param imageUrl 图片链接（可选）
     * @param user 当前登录管理员
     * @return 发送结果
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
     * API路径：POST /api/message/send/user
     * 权限：需要 ADMIN 角色
     *
     * @param userId 目标用户ID
     * @param title 消息标题
     * @param content 消息内容
     * @param type 消息类型，默认SYSTEM
     * @param priority 优先级（可选）
     * @param jumpUrl 跳转链接（可选）
     * @param user 当前登录管理员
     * @return 发送结果
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
     * API路径：POST /api/message/send/users
     * 权限：需要 ADMIN 角色
     *
     * @param userIds 目标用户ID列表
     * @param title 消息标题
     * @param content 消息内容
     * @param type 消息类型，默认SYSTEM
     * @param priority 优先级（可选）
     * @param jumpUrl 跳转链接（可选）
     * @param user 当前登录管理员
     * @return 发送结果
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
