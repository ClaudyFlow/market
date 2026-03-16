package com.market.controller;

import com.market.dto.ReviewRequest;
import com.market.dto.ReviewResponse;
import com.market.entity.Review;
import com.market.entity.User;
import com.market.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/api/review")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 获取商品的所有评价
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        List<ReviewResponse> response = reviews.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的所有评价
     */
    @GetMapping("/user")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(@AuthenticationPrincipal User user) {
        List<Review> reviews = reviewService.getUserReviews(user.getId());
        List<ReviewResponse> response = reviews.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 添加评价
     */
    @PostMapping
    public ResponseEntity<?> addReview(
            @AuthenticationPrincipal User user,
            @RequestBody ReviewRequest request) {
        try {
            Review review = reviewService.addReview(
                user.getId(),
                request.getProductId(),
                request.getRating(),
                request.getContent()
            );
            return ResponseEntity.ok(convertToResponse(review));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 更新评价
     */
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId,
            @RequestBody ReviewRequest request) {
        try {
            Review review = reviewService.updateReview(
                user.getId(),
                productId,
                request.getRating(),
                request.getContent()
            );
            return ResponseEntity.ok(convertToResponse(review));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        reviewService.deleteReview(user.getId(), productId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取商品平均评分
     */
    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<Map<String, Object>> getProductRating(@PathVariable Long productId) {
        Double averageRating = reviewService.getProductAverageRating(productId);
        int reviewCount = reviewService.getProductReviewCount(productId);

        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", averageRating);
        response.put("reviewCount", reviewCount);

        // 获取各评分等级的数量
        Map<Integer, Integer> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingDistribution.put(i, reviewService.getRatingCount(productId, i));
        }
        response.put("ratingDistribution", ratingDistribution);

        return ResponseEntity.ok(response);
    }

    /**
     * 检查用户是否已评价
     */
    @GetMapping("/product/{productId}/check")
    public ResponseEntity<Map<String, Object>> checkReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        boolean hasReviewed = reviewService.hasReviewed(user.getId(), productId);
        Map<String, Object> response = new HashMap<>();
        response.put("hasReviewed", hasReviewed);

        if (hasReviewed) {
            Review review = reviewService.getUserReview(user.getId(), productId);
            response.put("rating", review.getRating());
            response.put("content", review.getContent());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 将 Review 实体转换为 ReviewResponse DTO
     */
    private ReviewResponse convertToResponse(Review review) {
        return ReviewResponse.builder()
            .id(review.getId())
            .userId(review.getUserId())
            .productId(review.getProductId())
            .rating(review.getRating())
            .content(review.getContent())
            .userName(review.getUserName())
            .userAvatar(review.getUserAvatar())
            .productName(review.getProductName())
            .productImage(review.getProductImage())
            .productPrice(review.getProductPrice())
            .createdAt(review.getCreatedAt())
            .updatedAt(review.getUpdatedAt())
            .build();
    }
}
