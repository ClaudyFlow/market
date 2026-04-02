package com.market.dto;

import java.time.LocalDateTime;

/**
 * 用户通知响应 DTO
 */
public class UserNotificationResponse {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;
    private String level;
    private Boolean isRead;
    private String bizType;
    private Long bizId;
    private String jumpUrl;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public UserNotificationResponse() {
    }

    public UserNotificationResponse(Long id, Long userId, String title, String content, String type, 
                                     String level, Boolean isRead, String bizType, Long bizId, 
                                     String jumpUrl, LocalDateTime createdAt, LocalDateTime readAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.level = level;
        this.isRead = isRead;
        this.bizType = bizType;
        this.bizId = bizId;
        this.jumpUrl = jumpUrl;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }

    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }

    public String getJumpUrl() { return jumpUrl; }
    public void setJumpUrl(String jumpUrl) { this.jumpUrl = jumpUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    /**
     * 构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private String title;
        private String content;
        private String type;
        private String level;
        private Boolean isRead;
        private String bizType;
        private Long bizId;
        private String jumpUrl;
        private LocalDateTime createdAt;
        private LocalDateTime readAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder level(String level) { this.level = level; return this; }
        public Builder isRead(Boolean isRead) { this.isRead = isRead; return this; }
        public Builder bizType(String bizType) { this.bizType = bizType; return this; }
        public Builder bizId(Long bizId) { this.bizId = bizId; return this; }
        public Builder jumpUrl(String jumpUrl) { this.jumpUrl = jumpUrl; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder readAt(LocalDateTime readAt) { this.readAt = readAt; return this; }

        public UserNotificationResponse build() {
            return new UserNotificationResponse(id, userId, title, content, type, level, isRead, bizType, bizId, jumpUrl, createdAt, readAt);
        }
    }
}
