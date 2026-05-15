package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 * 对应数据库表：chat_message
 * 用于存储用户与客服之间的聊天记录
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "chat_message", indexes = {
    @Index(name = "idx_sender_id", columnList = "sender_id"),
    @Index(name = "idx_receiver_id", columnList = "receiver_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class ChatMessage {

    /**
     * 聊天消息唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发送者ID
     */
    @Column(nullable = false)
    private Long senderId;

    /**
     * 接收者ID
     */
    @Column(nullable = false)
    private Long receiverId;

    /**
     * 消息内容
     */
    @Column(nullable = false, length = 2000)
    private String content;

    /**
     * 消息类型（TEXT文本、IMAGE图片、SYSTEM系统、FILE文件）
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageType type = MessageType.TEXT;

    /**
     * 消息状态码：1000-发送中，2000-已发送，3000-已送达，4000-已读，5000-失败
     */
    @Column(nullable = false)
    private Integer status = 1000;

    /**
     * 是否已读
     */
    @Column(nullable = false)
    private Boolean isRead = false;

    /**
     * 消息创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChatMessage() {}

    public ChatMessage(Long senderId, Long receiverId, String content, MessageType type) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.type = type;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum MessageType {
        TEXT,      // 文本消息
        IMAGE,     // 图片消息
        SYSTEM,    // 系统消息
        FILE       // 文件消息
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
