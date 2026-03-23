package com.market.dto;

import java.time.LocalDateTime;

/**
 * 聊天消息请求 DTO
 */
public class ChatMessageRequest {

    private Long receiverId;
    private String content;
    private String type;  // TEXT, IMAGE, SYSTEM, FILE

    public ChatMessageRequest() {}

    public ChatMessageRequest(Long receiverId, String content, String type) {
        this.receiverId = receiverId;
        this.content = content;
        this.type = type;
    }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
