package com.market.service;

import com.market.dto.ChatMessageRequest;
import com.market.dto.ChatMessageResponse;
import com.market.entity.ChatMessage;
import com.market.entity.User;
import com.market.repository.ChatMessageRepository;
import com.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 聊天服务类
 */
@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    /**
     * 发送消息
     */
    @Transactional
    public ChatMessageResponse sendMessage(Long senderId, ChatMessageRequest request) {
        ChatMessage message = new ChatMessage(
            senderId,
            request.getReceiverId(),
            request.getContent(),
            ChatMessage.MessageType.valueOf(request.getType())
        );
        message.setStatus(2000); // 已发送
        message = chatMessageRepository.save(message);

        // 通过 WebSocket 推送消息给接收者
        messagingTemplate.convertAndSendToUser(
            String.valueOf(request.getReceiverId()),
            "/queue/messages",
            convertToResponse(message)
        );

        return convertToResponse(message);
    }

    /**
     * 获取聊天记录（分页）
     */
    public List<ChatMessageResponse> getConversation(Long userId, Long otherUserId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<ChatMessage> messages = chatMessageRepository.findConversation(userId, otherUserId, pageRequest);
        return messages.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /**
     * 获取未读消息
     */
    public List<ChatMessageResponse> getUnreadMessages(Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findByReceiverIdAndIsRead(userId, false);
        return messages.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /**
     * 获取未读消息数量
     */
    public long getUnreadCount(Long userId) {
        return chatMessageRepository.countByReceiverIdAndIsRead(userId, false);
    }

    /**
     * 标记消息为已读
     */
    @Transactional
    public void markAsRead(Long userId, Long senderId) {
        chatMessageRepository.markAllAsRead(userId, senderId);
    }

    /**
     * 获取用户会话列表
     */
    public List<Map<String, Object>> getUserSessions(Long userId) {
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatMessage> sentMessages = chatMessageRepository.findBySenderIdOrderByCreatedAtDesc(userId, pageRequest);
        Page<ChatMessage> receivedMessages = chatMessageRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageRequest);

        Map<Long, ChatMessage> latestMessageMap = new LinkedHashMap<>();

        for (ChatMessage msg : sentMessages.getContent()) {
            Long otherId = msg.getReceiverId();
            latestMessageMap.putIfAbsent(otherId, msg);
        }

        for (ChatMessage msg : receivedMessages.getContent()) {
            Long otherId = msg.getSenderId();
            if (!latestMessageMap.containsKey(otherId)) {
                latestMessageMap.put(otherId, msg);
            }
        }

        List<Map<String, Object>> sessions = new ArrayList<>();
        for (Map.Entry<Long, ChatMessage> entry : latestMessageMap.entrySet()) {
            Long otherUserId = entry.getKey();
            ChatMessage lastMsg = entry.getValue();

            Map<String, Object> session = new HashMap<>();
            session.put("id", otherUserId);

            Optional<User> otherUser = userRepository.findById(otherUserId);
            if (otherUser.isPresent()) {
                session.put("name", otherUser.get().getUsername());
                session.put("avatar", otherUser.get().getAvatarUrl());
            } else {
                session.put("name", "用户" + otherUserId);
                session.put("avatar", null);
            }

            session.put("lastMessage", lastMsg.getContent());
            session.put("lastTime", lastMsg.getCreatedAt());

            long unreadCount = chatMessageRepository.countByReceiverIdAndIsRead(userId, false);
            session.put("unreadCount", unreadCount);
            session.put("isOnline", false);

            sessions.add(session);
        }

        sessions.sort((a, b) -> {
            Object timeA = a.get("lastTime");
            Object timeB = b.get("lastTime");
            if (timeA == null) return 1;
            if (timeB == null) return -1;
            return ((java.time.LocalDateTime) timeB).compareTo((java.time.LocalDateTime) timeA);
        });

        return sessions;
    }

    /**
     * 将实体转换为响应 DTO
     */
    private ChatMessageResponse convertToResponse(ChatMessage message) {
        return new ChatMessageResponse(
            message.getId(),
            message.getSenderId(),
            message.getReceiverId(),
            message.getContent(),
            message.getType().name(),
            message.getStatus(),
            message.getIsRead(),
            message.getCreatedAt()
        );
    }
}
