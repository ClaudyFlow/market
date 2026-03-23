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

/**
 * 聊天控制器
 * 处理 WebSocket 和 HTTP 请求
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * WebSocket 处理发送消息
     */
    @MessageMapping("/chat.send")
    public void sendWsMessage(@Payload ChatMessageRequest request,
                              @AuthenticationPrincipal User user) {
        if (user != null) {
            chatService.sendMessage(user.getId(), request);
        }
    }

    /**
     * WebSocket 处理加入聊天
     */
    @MessageMapping("/chat.join")
    @SendTo("/topic/chat")
    public ChatMessageRequest joinChat(@Payload ChatMessageRequest request) {
        return request;
    }

    /**
     * HTTP 获取聊天记录
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
     * HTTP 获取未读消息
     */
    @GetMapping("/unread")
    public ResponseEntity<List<ChatMessageResponse>> getUnreadMessages(
            @AuthenticationPrincipal User user) {
        List<ChatMessageResponse> messages = chatService.getUnreadMessages(user.getId());
        return ResponseEntity.ok(messages);
    }

    /**
     * HTTP 获取未读消息数量
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getUnreadCount(user.getId()));
    }

    /**
     * HTTP 标记消息为已读
     */
    @PostMapping("/mark-read/{senderId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long senderId,
                                           @AuthenticationPrincipal User user) {
        chatService.markAsRead(user.getId(), senderId);
        return ResponseEntity.ok().build();
    }

    /**
     * HTTP 发送消息（备用接口）
     */
    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal User user) {
        ChatMessageResponse response = chatService.sendMessage(user.getId(), request);
        return ResponseEntity.ok(response);
    }
}
