package com.market.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评价响应 DTO
 */
public class ReviewResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer rating;
    private String content;
    private String userName;
    private String userAvatar;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewResponse() {}

    public ReviewResponse(Long id, Long userId, Long productId, Integer rating, String content,
                          String userName, String userAvatar, String productName,
                          String productImage, BigDecimal productPrice,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.content = content;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static ReviewResponseBuilder builder() {
        return new ReviewResponseBuilder();
    }

    public static class ReviewResponseBuilder {
        private Long id;
        private Long userId;
        private Long productId;
        private Integer rating;
        private String content;
        private String userName;
        private String userAvatar;
        private String productName;
        private String productImage;
        private BigDecimal productPrice;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ReviewResponseBuilder id(Long id) { this.id = id; return this; }
        public ReviewResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public ReviewResponseBuilder productId(Long productId) { this.productId = productId; return this; }
        public ReviewResponseBuilder rating(Integer rating) { this.rating = rating; return this; }
        public ReviewResponseBuilder content(String content) { this.content = content; return this; }
        public ReviewResponseBuilder userName(String userName) { this.userName = userName; return this; }
        public ReviewResponseBuilder userAvatar(String userAvatar) { this.userAvatar = userAvatar; return this; }
        public ReviewResponseBuilder productName(String productName) { this.productName = productName; return this; }
        public ReviewResponseBuilder productImage(String productImage) { this.productImage = productImage; return this; }
        public ReviewResponseBuilder productPrice(BigDecimal productPrice) { this.productPrice = productPrice; return this; }
        public ReviewResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ReviewResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ReviewResponse build() {
            return new ReviewResponse(id, userId, productId, rating, content, userName, userAvatar,
                                      productName, productImage, productPrice, createdAt, updatedAt);
        }
    }
}
