package com.market.dto;

/**
 * 关注请求 DTO
 */
public class FollowRequest {
    private Long shopId;
    private String shopName;
    private String shopAvatar;

    public FollowRequest() {}

    public FollowRequest(Long shopId, String shopName, String shopAvatar) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopAvatar = shopAvatar;
    }

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getShopAvatar() { return shopAvatar; }
    public void setShopAvatar(String shopAvatar) { this.shopAvatar = shopAvatar; }
}
