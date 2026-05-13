package com.market.controller;

import com.market.dto.ChatMessageRequest;
import com.market.dto.ChatMessageResponse;
import com.market.entity.ChatMessage;
import com.market.entity.User;
import com.market.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聊天控制器
 * 处理 WebSocket 实时消息和 HTTP 聊天记录查询接口。
 * 权限要求：需要登录（WebSocket 和 HTTP 接口均需认证）
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/chat
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * WebSocket 发送消息
     * API路径：WebSocket /chat.send
     * 权限：需要登录
     *
     * @param request 消息请求（包含接收者ID和内容）
     * @param user 当前登录用户
     */
    @MessageMapping("/chat.send")
    public void sendWsMessage(@Payload ChatMessageRequest request,
                              @AuthenticationPrincipal User user) {
        if (user != null) {
            chatService.sendMessage(user.getId(), request);
        }
    }

    /**
     * WebSocket 加入聊天
     * API路径：WebSocket /chat.join
     * 权限：公开
     *
     * @param request 加入聊天请求
     * @return 加入聊天的消息
     */
    @MessageMapping("/chat.join")
    @SendTo("/topic/chat")
    public ChatMessageRequest joinChat(@Payload ChatMessageRequest request) {
        return request;
    }

    /**
     * 获取聊天记录
     * API路径：GET /api/chat/conversation/{otherUserId}
     * 权限：需要登录
     *
     * @param otherUserId 对话方用户ID
     * @param user 当前登录用户
     * @param page 页码，默认0
     * @param size 每页大小，默认20
     * @return 聊天记录列表
     */
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<ChatMessageResponse>> getConversation(
            @PathVariable Long otherUserId,
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ChatMessageResponse> messages = chatService.getConversation(
            user.getId(), otherUserId, page, size);
        return ResponseEntity.ok(messages);
    }

    /**
     * 获取未读消息
     * API路径：GET /api/chat/unread
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 未读消息列表
     */
    @GetMapping("/unread")
    public ResponseEntity<List<ChatMessageResponse>> getUnreadMessages(
            @AuthenticationPrincipal User user) {
        List<ChatMessageResponse> messages = chatService.getUnreadMessages(user.getId());
        return ResponseEntity.ok(messages);
    }

    /**
     * 获取未读消息数量
     * API路径：GET /api/chat/unread/count
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 未读消息数量
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getUnreadCount(user.getId()));
    }

    /**
     * 标记消息为已读
     * API路径：POST /api/chat/mark-read/{senderId}
     * 权限：需要登录
     *
     * @param senderId 发送者用户ID
     * @param user 当前登录用户
     * @return 操作结果
     */
    @PostMapping("/mark-read/{senderId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long senderId,
                                           @AuthenticationPrincipal User user) {
        chatService.markAsRead(user.getId(), senderId);
        return ResponseEntity.ok().build();
    }

    /**
     * 发送消息（HTTP 备用接口）
     * API路径：POST /api/chat/send
     * 权限：需要登录
     *
     * @param request 消息请求（包含接收者ID和内容）
     * @param user 当前登录用户
     * @return 发送后的消息
     */
    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal User user) {
        ChatMessageResponse response = chatService.sendMessage(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户会话列表
     * API路径：GET /api/chat/sessions
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 会话列表（包含对方用户信息和最后一条消息）
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<Map<String, Object>>> getSessions(
            @AuthenticationPrincipal User user) {
        List<Map<String, Object>> sessions = chatService.getUserSessions(user.getId());
        return ResponseEntity.ok(sessions);
    }
}
