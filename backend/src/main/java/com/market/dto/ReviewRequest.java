package com.market.dto;

import java.util.List;

/**
 * 评价请求 DTO
 */
public class ReviewRequest {
    private Long productId;
    private Integer rating;
    private String content;
    private List<String> images;

    public ReviewRequest() {}

    public ReviewRequest(Long productId, Integer rating, String content) {
        this.productId = productId;
        this.rating = rating;
        this.content = content;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
