package com.market.dto;

import java.time.LocalDateTime;

/**
 * 聊天消息响应 DTO
 */
public class ChatMessageResponse {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private String type;
    private Integer status;  // 消息状态码：1000-9999
    private Boolean isRead;
    private LocalDateTime createdAt;

    public ChatMessageResponse() {}

    public ChatMessageResponse(Long id, Long senderId, Long receiverId, String content,
                               String type, Integer status, Boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.type = type;
        this.status = status;
        this.isRead = isRead;
        this.createdAt = createdAt;
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

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
