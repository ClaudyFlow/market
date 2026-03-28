package com.market.controller;

import com.market.dto.*;
import com.market.entity.ProductReview;
import com.market.entity.User;
import com.market.entity.UserFavorite;
import com.market.entity.UserFollow;
import com.market.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户账户控制器
 * <p>
 * 提供用户资料管理、关注/粉丝、收藏、评价等功能的 API 接口。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "*")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    // ==================== 用户资料管理 ====================

    /**
     * 获取用户完整资料
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        Long currentUserId = user != null ? user.getId() : null;
        UserProfileResponse response = userAccountService.getUserProfileWithRelation(userId, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前登录用户资料
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(
            @AuthenticationPrincipal User user) {
        UserProfileResponse response = userAccountService.getUserProfileWithRelation(
                user.getId(), user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @Valid @RequestBody UserProfileUpdateRequest request,
            @AuthenticationPrincipal User user) {
        UserProfileResponse response = userAccountService.updateUserProfile(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * 修改密码
     */
    @PostMapping("/password/change")
    public ResponseEntity<Map<String, Object>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = userAccountService.changePassword(user.getId(), request);
            result.put("success", success);
            result.put("message", "密码修改成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 更新头像
     */
    @PostMapping("/avatar")
    public ResponseEntity<Map<String, Object>> updateAvatar(
            @RequestParam String avatarUrl,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userAccountService.updateAvatar(user.getId(), avatarUrl);
        result.put("success", success);
        result.put("avatarUrl", avatarUrl);
        result.put("message", "头像更新成功");
        return ResponseEntity.ok(result);
    }

    // ==================== 用户关注/粉丝 ====================

    /**
     * 关注用户
     */
    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<Map<String, Object>> followUser(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = userAccountService.followUser(user.getId(), targetUserId);
            result.put("success", success);
            result.put("message", "关注成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 取消关注用户
     */
    @PostMapping("/unfollow/{targetUserId}")
    public ResponseEntity<Map<String, Object>> unfollowUser(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userAccountService.unfollowUser(user.getId(), targetUserId);
        result.put("success", success);
        result.put("message", "取消关注成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 检查是否已关注
     */
    @GetMapping("/follow/check/{targetUserId}")
    public ResponseEntity<Map<String, Boolean>> checkFollowing(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal User user) {
        Map<String, Boolean> result = new HashMap<>();
        boolean isFollowing = userAccountService.isFollowing(user.getId(), targetUserId);
        result.put("isFollowing", isFollowing);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取关注列表
     */
    @GetMapping("/followings/{userId}")
    public ResponseEntity<Page<UserFollow>> getFollowingList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserFollow> followingList = userAccountService.getFollowingList(userId, pageable);
        return ResponseEntity.ok(followingList);
    }

    /**
     * 获取粉丝列表
     */
    @GetMapping("/followers/{userId}")
    public ResponseEntity<Page<UserFollow>> getFollowerList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserFollow> followerList = userAccountService.getFollowerList(userId, pageable);
        return ResponseEntity.ok(followerList);
    }

    /**
     * 获取关注数
     */
    @GetMapping("/followings/count/{userId}")
    public ResponseEntity<Map<String, Long>> getFollowingCount(@PathVariable Long userId) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", userAccountService.getFollowingCount(userId));
        return ResponseEntity.ok(result);
    }

    /**
     * 获取粉丝数
     */
    @GetMapping("/followers/count/{userId}")
    public ResponseEntity<Map<String, Long>> getFollowerCount(@PathVariable Long userId) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", userAccountService.getFollowerCount(userId));
        return ResponseEntity.ok(result);
    }

    // ==================== 用户收藏 ====================

    /**
     * 添加收藏
     */
    @PostMapping("/favorite")
    public ResponseEntity<UserFavorite> addFavorite(
            @RequestParam Long productId,
            @RequestParam String productName,
            @RequestParam(required = false) String productImage,
            @RequestParam(required = false) java.math.BigDecimal productPrice,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) String shopName,
            @AuthenticationPrincipal User user) {
        UserFavorite favorite = userAccountService.addFavorite(
                user.getId(), productId, productName, productImage, productPrice, shopId, shopName);
        return ResponseEntity.ok(favorite);
    }

    /**
     * 取消收藏
     */
    @PostMapping("/favorite/{productId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userAccountService.removeFavorite(user.getId(), productId);
        result.put("success", success);
        result.put("message", "取消收藏成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/favorite/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal User user) {
        Map<String, Boolean> result = new HashMap<>();
        boolean isFavorite = userAccountService.isFavorite(user.getId(), productId);
        result.put("isFavorite", isFavorite);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取收藏列表
     */
    @GetMapping("/favorites")
    public ResponseEntity<Page<UserFavorite>> getFavoriteList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserFavorite> favoriteList = userAccountService.getFavoriteList(user.getId(), pageable);
        return ResponseEntity.ok(favoriteList);
    }

    /**
     * 搜索收藏列表
     */
    @GetMapping("/favorites/search")
    public ResponseEntity<Page<UserFavorite>> searchFavorites(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserFavorite> favoriteList = userAccountService.searchFavorites(
                user.getId(), keyword, pageable);
        return ResponseEntity.ok(favoriteList);
    }

    /**
     * 获取收藏数
     */
    @GetMapping("/favorites/count")
    public ResponseEntity<Map<String, Long>> getFavoriteCount(
            @AuthenticationPrincipal User user) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", userAccountService.getFavoriteCount(user.getId()));
        return ResponseEntity.ok(result);
    }

    // ==================== 用户评价 ====================

    /**
     * 创建评价
     */
    @PostMapping("/review")
    public ResponseEntity<ProductReview> createReview(
            @RequestParam Long productId,
            @RequestParam Long orderId,
            @RequestParam Integer rating,
            @RequestParam String content,
            @RequestParam(required = false) String images,
            @AuthenticationPrincipal User user) {
        ProductReview review = userAccountService.createReview(
                user.getId(), productId, orderId, rating, content, images);
        return ResponseEntity.ok(review);
    }

    /**
     * 更新评价
     */
    @PutMapping("/review/{reviewId}")
    public ResponseEntity<ProductReview> updateReview(
            @PathVariable Long reviewId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String images,
            @AuthenticationPrincipal User user) {
        ProductReview review = userAccountService.updateReview(reviewId, rating, content, images);
        return ResponseEntity.ok(review);
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userAccountService.deleteReview(reviewId);
        result.put("success", success);
        result.put("message", "评价删除成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 获取商品评价列表
     */
    @GetMapping("/reviews/product/{productId}")
    public ResponseEntity<Page<ProductReview>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductReview> reviews = userAccountService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 获取用户评价列表
     */
    @GetMapping("/reviews/user/{userId}")
    public ResponseEntity<Page<ProductReview>> getUserReviews(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductReview> reviews = userAccountService.getUserReviews(userId, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 获取商品平均评分
     */
    @GetMapping("/reviews/average/{productId}")
    public ResponseEntity<Map<String, Double>> getProductAverageRating(
            @PathVariable Long productId) {
        Map<String, Double> result = new HashMap<>();
        Double averageRating = userAccountService.getProductAverageRating(productId);
        result.put("averageRating", averageRating != null ? averageRating : 0.0);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取商品评分分布
     */
    @GetMapping("/reviews/distribution/{productId}")
    public ResponseEntity<Map<Integer, Long>> getProductRatingDistribution(
            @PathVariable Long productId) {
        Map<Integer, Long> distribution = userAccountService.getProductRatingDistribution(productId);
        return ResponseEntity.ok(distribution);
    }

    /**
     * 商家回复评价
     */
    @PostMapping("/review/reply/{reviewId}")
    public ResponseEntity<Map<String, Object>> replyToReview(
            @PathVariable Long reviewId,
            @RequestParam String reply,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userAccountService.replyToReview(reviewId, reply);
        result.put("success", success);
        result.put("message", "回复成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 审核评价（管理员）
     */
    @PostMapping("/review/audit/{reviewId}")
    public ResponseEntity<Map<String, Object>> auditReview(
            @PathVariable Long reviewId,
            @RequestParam boolean approved,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        if (!"ADMIN".equals(user.getRole())) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return ResponseEntity.status(403).body(result);
        }
        boolean success = userAccountService.auditReview(reviewId, approved);
        result.put("success", success);
        result.put("message", "审核成功");
        return ResponseEntity.ok(result);
    }
}
