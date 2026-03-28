package com.market.service.impl;

import com.market.dto.*;
import com.market.entity.ProductReview;
import com.market.entity.User;
import com.market.entity.UserFavorite;
import com.market.entity.UserFollow;
import com.market.repository.ProductReviewRepository;
import com.market.repository.UserFavoriteRepository;
import com.market.repository.UserFollowRepository;
import com.market.repository.UserRepository;
import com.market.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户账户服务实现类
 *
 * @author Market Team
 * @since 1.0.0
 */
@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFollowRepository userFollowRepository;

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==================== 用户资料管理 ====================

    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return convertToUserProfileResponse(user);
    }

    @Override
    public UserProfileResponse getUserProfileWithRelation(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserProfileResponse response = convertToUserProfileResponse(user);

        // 设置是否已关注
        if (currentUserId != null && !currentUserId.equals(userId)) {
            response.setIsFollowing(userFollowRepository.existsByFollowerIdAndFollowingId(
                    currentUserId, userId));
        }

        // 设置关注数、粉丝数、收藏数
        response.setFollowingCount(userFollowRepository.countByFollowerId(userId));
        response.setFollowerCount(userFollowRepository.countByFollowingId(userId));
        response.setFavoriteCount(userFavoriteRepository.countByUserId(userId));

        return response;
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            // 检查邮箱是否已被其他用户使用
            if (!request.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return convertToUserProfileResponse(user);
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, PasswordChangeRequest request) {
        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的新密码不一致");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional
    public boolean updateAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return true;
    }

    // ==================== 用户关注/粉丝 ====================

    @Override
    @Transactional
    public boolean followUser(Long followerId, Long followingId) {
        // 不能关注自己
        if (followerId.equals(followingId)) {
            throw new RuntimeException("不能关注自己");
        }

        // 检查被关注用户是否存在
        if (!userRepository.existsById(followingId)) {
            throw new RuntimeException("被关注用户不存在");
        }

        // 检查是否已关注
        if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            return false; // 已关注，无需重复关注
        }

        UserFollow userFollow = new UserFollow(followerId, followingId);
        userFollowRepository.save(userFollow);

        return true;
    }

    @Override
    @Transactional
    public boolean unfollowUser(Long followerId, Long followingId) {
        UserFollow userFollow = userFollowRepository
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .orElse(null);

        if (userFollow == null) {
            return false; // 未关注，无需取消
        }

        userFollowRepository.delete(userFollow);
        return true;
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    public Page<UserFollow> getFollowingList(Long userId, Pageable pageable) {
        return userFollowRepository.findAll(
                org.springframework.data.domain.Example.of(new UserFollow(userId, null)),
                pageable);
    }

    @Override
    public Page<UserFollow> getFollowerList(Long userId, Pageable pageable) {
        // 使用自定义查询获取粉丝列表
        List<UserFollow> followers = userFollowRepository.findByFollowingId(userId);
        int start = (int) pageable.getOffset() * pageable.getPageSize();
        int end = Math.min(start + pageable.getPageSize(), followers.size());

        return new org.springframework.data.domain.PageImpl<>(
                followers.subList(start, end), pageable, followers.size());
    }

    @Override
    public long getFollowingCount(Long userId) {
        return userFollowRepository.countByFollowerId(userId);
    }

    @Override
    public long getFollowerCount(Long userId) {
        return userFollowRepository.countByFollowingId(userId);
    }

    // ==================== 用户收藏 ====================

    @Override
    @Transactional
    public UserFavorite addFavorite(Long userId, Long productId, String productName,
                                    String productImage, java.math.BigDecimal productPrice,
                                    Long shopId, String shopName) {
        // 检查是否已收藏
        if (userFavoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("已收藏该商品");
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setProductName(productName);
        favorite.setProductImage(productImage);
        favorite.setProductPrice(productPrice);
        favorite.setShopId(shopId);
        favorite.setShopName(shopName);

        return userFavoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public boolean removeFavorite(Long userId, Long productId) {
        UserFavorite favorite = userFavoriteRepository
                .findByUserIdAndProductId(userId, productId)
                .orElse(null);

        if (favorite == null) {
            return false; // 未收藏，无需取消
        }

        userFavoriteRepository.delete(favorite);
        return true;
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        return userFavoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public Page<UserFavorite> getFavoriteList(Long userId, Pageable pageable) {
        return userFavoriteRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<UserFavorite> searchFavorites(Long userId, String keyword, Pageable pageable) {
        return userFavoriteRepository.searchByUserId(userId, keyword, pageable);
    }

    @Override
    public long getFavoriteCount(Long userId) {
        return userFavoriteRepository.countByUserId(userId);
    }

    // ==================== 用户评价 ====================

    @Override
    @Transactional
    public ProductReview createReview(Long userId, Long productId, Long orderId,
                                      Integer rating, String content, String images) {
        // 检查是否已评价
        if (productReviewRepository.existsByUserIdAndProductIdAndOrderId(userId, productId, orderId)) {
            throw new RuntimeException("您已对该商品评价过");
        }

        // 验证评分
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("评分必须在 1-5 之间");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        ProductReview review = new ProductReview();
        review.setUserId(userId);
        review.setUserName(user.getName());
        review.setUserAvatar(user.getAvatarUrl());
        review.setProductId(productId);
        review.setOrderId(orderId);
        review.setRating(rating);
        review.setContent(content);
        review.setImages(images);
        review.setStatus("PENDING"); // 待审核

        return productReviewRepository.save(review);
    }

    @Override
    @Transactional
    public ProductReview updateReview(Long reviewId, Integer rating, String content, String images) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));

        // 只有待审核或已拒绝的评价才能修改
        if ("APPROVED".equals(review.getStatus())) {
            throw new RuntimeException("已通过的评价不能修改");
        }

        if (rating != null) {
            if (rating < 1 || rating > 5) {
                throw new RuntimeException("评分必须在 1-5 之间");
            }
            review.setRating(rating);
        }
        if (content != null) {
            review.setContent(content);
        }
        if (images != null) {
            review.setImages(images);
        }

        review.setUpdatedAt(LocalDateTime.now());
        return productReviewRepository.save(review);
    }

    @Override
    @Transactional
    public boolean deleteReview(Long reviewId) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));

        // 只有通过的评价才能删除（待审核和已拒绝的可以直接修改）
        if (!"APPROVED".equals(review.getStatus())) {
            throw new RuntimeException("该评价不能删除");
        }

        productReviewRepository.delete(review);
        return true;
    }

    @Override
    public Page<ProductReview> getProductReviews(Long productId, Pageable pageable) {
        return productReviewRepository.findByProductIdAndStatus(productId, "APPROVED", pageable);
    }

    @Override
    public Page<ProductReview> getUserReviews(Long userId, Pageable pageable) {
        return productReviewRepository.findByUserId(userId, pageable);
    }

    @Override
    public Double getProductAverageRating(Long productId) {
        return productReviewRepository.getAverageRating(productId);
    }

    @Override
    public Map<Integer, Long> getProductRatingDistribution(Long productId) {
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, productReviewRepository.countByProductIdAndRating(productId, i));
        }
        return distribution;
    }

    @Override
    @Transactional
    public boolean replyToReview(Long reviewId, String reply) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));

        review.setMerchantReply(reply);
        review.setReplyTime(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        productReviewRepository.save(review);

        return true;
    }

    @Override
    @Transactional
    public boolean auditReview(Long reviewId, boolean approved) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));

        review.setStatus(approved ? "APPROVED" : "REJECTED");
        review.setUpdatedAt(LocalDateTime.now());
        productReviewRepository.save(review);

        return true;
    }

    // ==================== 辅助方法 ====================

    /**
     * 将 User 实体转换为 UserProfileResponse
     */
    private UserProfileResponse convertToUserProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setCredit(user.getCredit());
        response.setTotalCredit(user.getTotalCredit());
        response.setVipLevel(user.getVipLevel());
        response.setGrowthValue(user.getGrowthValue());
        response.setConsecutiveCheckinDays(user.getConsecutiveCheckinDays());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setLastLoginAt(user.getLastLoginAt());
        return response;
    }
}
