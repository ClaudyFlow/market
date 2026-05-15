package com.market.dto;

/**
 * 收藏请求 DTO
 */
public class FavoriteRequest {
    private Long productId;

    public FavoriteRequest() {}

    public FavoriteRequest(Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
