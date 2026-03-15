package com.market.dto;

import java.time.LocalDateTime;

/**
 * 收藏响应 DTO
 */
public class FavoriteResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImage;
    private String productPrice;
    private LocalDateTime createdAt;

    public FavoriteResponse() {}

    public FavoriteResponse(Long id, Long userId, Long productId, String productName,
                            String productImage, String productPrice, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public String getProductPrice() { return productPrice; }
    public void setProductPrice(String productPrice) { this.productPrice = productPrice; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static FavoriteResponseBuilder builder() {
        return new FavoriteResponseBuilder();
    }

    public static class FavoriteResponseBuilder {
        private Long id, userId, productId;
        private String productName, productImage, productPrice;
        private LocalDateTime createdAt;

        public FavoriteResponseBuilder id(Long id) { this.id = id; return this; }
        public FavoriteResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public FavoriteResponseBuilder productId(Long productId) { this.productId = productId; return this; }
        public FavoriteResponseBuilder productName(String productName) { this.productName = productName; return this; }
        public FavoriteResponseBuilder productImage(String productImage) { this.productImage = productImage; return this; }
        public FavoriteResponseBuilder productPrice(String productPrice) { this.productPrice = productPrice; return this; }
        public FavoriteResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public FavoriteResponse build() {
            return new FavoriteResponse(id, userId, productId, productName, productImage, productPrice, createdAt);
        }
    }
}
