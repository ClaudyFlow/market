package com.market.dto;

import java.time.LocalDateTime;

/**
 * 关注响应 DTO
 */
public class FollowResponse {
    private Long id;
    private Long userId;
    private Long shopId;
    private String shopName;
    private String shopAvatar;
    private LocalDateTime createdAt;

    public FollowResponse() {}

    public FollowResponse(Long id, Long userId, Long shopId, String shopName,
                          String shopAvatar, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopAvatar = shopAvatar;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getShopAvatar() { return shopAvatar; }
    public void setShopAvatar(String shopAvatar) { this.shopAvatar = shopAvatar; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static FollowResponseBuilder builder() {
        return new FollowResponseBuilder();
    }

    public static class FollowResponseBuilder {
        private Long id, userId, shopId;
        private String shopName, shopAvatar;
        private LocalDateTime createdAt;

        public FollowResponseBuilder id(Long id) { this.id = id; return this; }
        public FollowResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public FollowResponseBuilder shopId(Long shopId) { this.shopId = shopId; return this; }
        public FollowResponseBuilder shopName(String shopName) { this.shopName = shopName; return this; }
        public FollowResponseBuilder shopAvatar(String shopAvatar) { this.shopAvatar = shopAvatar; return this; }
        public FollowResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public FollowResponse build() {
            return new FollowResponse(id, userId, shopId, shopName, shopAvatar, createdAt);
        }
    }
}
