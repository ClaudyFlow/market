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
 * 提供用户资料管理、关注/粉丝、收藏、评价等功能的 API 接口。
 * 权限要求：需要登录（部分查询接口可公开）
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/account
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
     * API路径：GET /api/account/profile/{userId}
     * 权限：需要登录
     *
     * @param userId 目标用户ID
     * @param user 当前登录用户
     * @return 用户完整资料
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
     * API路径：GET /api/account/profile
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 当前用户完整资料
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
     * API路径：PUT /api/account/profile
     * 权限：需要登录
     *
     * @param request 更新的用户资料
     * @param user 当前登录用户
     * @return 更新后的用户资料
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
     * API路径：POST /api/account/password/change
     * 权限：需要登录
     *
     * @param request 密码修改请求（包含旧密码和新密码）
     * @param user 当前登录用户
     * @return 修改结果
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
     * API路径：POST /api/account/avatar
     * 权限：需要登录
     *
     * @param avatarUrl 新头像URL
     * @param user 当前登录用户
     * @return 更新结果
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
     * API路径：POST /api/account/follow/{targetUserId}
     * 权限：需要登录
     *
     * @param targetUserId 目标用户ID
     * @param user 当前登录用户
     * @return 关注结果
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
     * API路径：POST /api/account/unfollow/{targetUserId}
     * 权限：需要登录
     *
     * @param targetUserId 目标用户ID
     * @param user 当前登录用户
     * @return 取消关注结果
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
     * API路径：GET /api/account/follow/check/{targetUserId}
     * 权限：需要登录
     *
     * @param targetUserId 目标用户ID
     * @param user 当前登录用户
     * @return 是否已关注
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
     * API路径：GET /api/account/followings/{userId}
     * 权限：需要登录
     *
     * @param userId 用户ID
     * @param page 页码，默认0
     * @param size 每页大小，默认20
     * @return 分页的关注列表
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
     * API路径：GET /api/account/followers/{userId}
     * 权限：需要登录
     *
     * @param userId 用户ID
     * @param page 页码，默认0
     * @param size 每页大小，默认20
     * @return 分页的粉丝列表
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
     * API路径：GET /api/account/followings/count/{userId}
     * 权限：公开
     *
     * @param userId 用户ID
     * @return 关注数量
     */
    @GetMapping("/followings/count/{userId}")
    public ResponseEntity<Map<String, Long>> getFollowingCount(@PathVariable Long userId) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", userAccountService.getFollowingCount(userId));
        return ResponseEntity.ok(result);
    }

    /**
     * 获取粉丝数
     * API路径：GET /api/account/followers/count/{userId}
     * 权限：公开
     *
     * @param userId 用户ID
     * @return 粉丝数量
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
     * API路径：POST /api/account/favorite
     * 权限：需要登录
     *
     * @param productId 商品ID
     * @param productName 商品名称
     * @param productImage 商品图片（可选）
     * @param productPrice 商品价格（可选）
     * @param shopId 店铺ID（可选）
     * @param shopName 店铺名称（可选）
     * @param user 当前登录用户
     * @return 添加的收藏记录
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
     * API路径：POST /api/account/favorite/{productId}
     * 权限：需要登录
     *
     * @param productId 商品ID
     * @param user 当前登录用户
     * @return 取消收藏结果
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
     * API路径：GET /api/account/favorite/check/{productId}
     * 权限：需要登录
     *
     * @param productId 商品ID
     * @param user 当前登录用户
     * @return 是否已收藏
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
     * API路径：GET /api/account/favorites
     * 权限：需要登录
     *
     * @param page 页码，默认0
     * @param size 每页大小，默认20
     * @param user 当前登录用户
     * @return 分页的收藏列表
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
     * API路径：GET /api/account/favorites/search
     * 权限：需要登录
     *
     * @param keyword 搜索关键词
     * @param page 页码，默认0
     * @param size 每页大小，默认20
     * @param user 当前登录用户
     * @return 分页的收藏搜索结果
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
     * API路径：GET /api/account/favorites/count
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 收藏数量
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
     * API路径：POST /api/account/review
     * 权限：需要登录
     *
     * @param productId 商品ID
     * @param orderId 订单ID
     * @param rating 评分
     * @param content 评价内容
     * @param images 评价图片（可选）
     * @param user 当前登录用户
     * @return 创建的评价
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
     * API路径：PUT /api/account/review/{reviewId}
     * 权限：需要登录
     *
     * @param reviewId 评价ID
     * @param rating 新评分（可选）
     * @param content 新内容（可选）
     * @param images 新图片（可选）
     * @param user 当前登录用户
     * @return 更新后的评价
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
     * API路径：DELETE /api/account/review/{reviewId}
     * 权限：需要登录
     *
     * @param reviewId 评价ID
     * @param user 当前登录用户
     * @return 删除结果
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
     * API路径：GET /api/account/reviews/product/{productId}
     * 权限：公开
     *
     * @param productId 商品ID
     * @param page 页码，默认0
     * @param size 每页大小，默认20
     * @return 分页的商品评价列表
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
     * API路径：GET /api/account/reviews/user/{userId}
     * 权限：公开
     *
     * @param userId 用户ID
     * @param page 页码，默认0
     * @param size 每页大小，默认20
     * @return 分页的用户评价列表
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
     * API路径：GET /api/account/reviews/average/{productId}
     * 权限：公开
     *
     * @param productId 商品ID
     * @return 商品平均评分
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
     * API路径：GET /api/account/reviews/distribution/{productId}
     * 权限：公开
     *
     * @param productId 商品ID
     * @return 评分分布数据
     */
    @GetMapping("/reviews/distribution/{productId}")
    public ResponseEntity<Map<Integer, Long>> getProductRatingDistribution(
            @PathVariable Long productId) {
        Map<Integer, Long> distribution = userAccountService.getProductRatingDistribution(productId);
        return ResponseEntity.ok(distribution);
    }

    /**
     * 商家回复评价
     * API路径：POST /api/account/review/reply/{reviewId}
     * 权限：需要登录
     *
     * @param reviewId 评价ID
     * @param reply 回复内容
     * @param user 当前登录用户（商家）
     * @return 回复结果
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
     * API路径：POST /api/account/review/audit/{reviewId}
     * 权限：需要管理员角色
     *
     * @param reviewId 评价ID
     * @param approved 是否通过
     * @param user 当前登录管理员
     * @return 审核结果
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
