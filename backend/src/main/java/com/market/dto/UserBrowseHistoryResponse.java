package com.market.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户浏览历史响应 DTO
 */
public class UserBrowseHistoryResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private Long shopId;
    private String shopName;
    private LocalDateTime browseTime;
    
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
    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public LocalDateTime getBrowseTime() { return browseTime; }
    public void setBrowseTime(LocalDateTime browseTime) { this.browseTime = browseTime; }
}