package com.market.service;

import com.market.dto.*;
import com.market.entity.ProductReview;
import com.market.entity.User;
import com.market.entity.UserFavorite;
import com.market.entity.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 * <p>
 * 提供用户相关的业务逻辑接口，包括用户资料管理、关注/粉丝、收藏、评价等功能。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
public interface UserAccountService {

    // ==================== 用户资料管理 ====================

    /**
     * 获取用户完整资料
     *
     * @param userId 用户 ID
     * @return 用户资料响应
     */
    UserProfileResponse getUserProfile(Long userId);

    /**
     * 获取当前登录用户的完整资料（包含是否关注等信息）
     *
     * @param userId 用户 ID
     * @param currentUserId 当前登录用户 ID
     * @return 用户资料响应
     */
    UserProfileResponse getUserProfileWithRelation(Long userId, Long currentUserId);

    /**
     * 更新用户资料
     *
     * @param userId 用户 ID
     * @param request 更新请求
     * @return 更新后的用户资料
     */
    UserProfileResponse updateUserProfile(Long userId, UserProfileUpdateRequest request);

    /**
     * 修改密码
     *
     * @param userId 用户 ID
     * @param request 密码修改请求
     * @return 是否成功
     */
    boolean changePassword(Long userId, PasswordChangeRequest request);

    /**
     * 更新用户头像
     *
     * @param userId 用户 ID
     * @param avatarUrl 新头像 URL
     * @return 是否成功
     */
    boolean updateAvatar(Long userId, String avatarUrl);

    // ==================== 用户关注/粉丝 ====================

    /**
     * 关注用户
     *
     * @param followerId 关注者 ID
     * @param followingId 被关注者 ID
     * @return 是否成功
     */
    boolean followUser(Long followerId, Long followingId);

    /**
     * 取消关注用户
     *
     * @param followerId 关注者 ID
     * @param followingId 被关注者 ID
     * @return 是否成功
     */
    boolean unfollowUser(Long followerId, Long followingId);

    /**
     * 检查是否已关注
     *
     * @param followerId 关注者 ID
     * @param followingId 被关注者 ID
     * @return 如果已关注返回 true，否则返回 false
     */
    boolean isFollowing(Long followerId, Long followingId);

    /**
     * 获取关注列表
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 关注的用户列表
     */
    Page<UserFollow> getFollowingList(Long userId, Pageable pageable);

    /**
     * 获取粉丝列表
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 粉丝列表
     */
    Page<UserFollow> getFollowerList(Long userId, Pageable pageable);

    /**
     * 获取关注数
     *
     * @param userId 用户 ID
     * @return 关注数
     */
    long getFollowingCount(Long userId);

    /**
     * 获取粉丝数
     *
     * @param userId 用户 ID
     * @return 粉丝数
     */
    long getFollowerCount(Long userId);

    // ==================== 用户收藏 ====================

    /**
     * 添加收藏
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @param productName 商品名称
     * @param productImage 商品图片
     * @param productPrice 商品价格
     * @param shopId 店铺 ID
     * @param shopName 店铺名称
     * @return 收藏对象
     */
    UserFavorite addFavorite(Long userId, Long productId, String productName,
                             String productImage, java.math.BigDecimal productPrice,
                             Long shopId, String shopName);

    /**
     * 取消收藏
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 是否成功
     */
    boolean removeFavorite(Long userId, Long productId);

    /**
     * 检查是否已收藏
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 如果已收藏返回 true，否则返回 false
     */
    boolean isFavorite(Long userId, Long productId);

    /**
     * 获取收藏列表
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 收藏列表分页
     */
    Page<UserFavorite> getFavoriteList(Long userId, Pageable pageable);

    /**
     * 搜索收藏列表
     *
     * @param userId 用户 ID
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 收藏列表分页
     */
    Page<UserFavorite> searchFavorites(Long userId, String keyword, Pageable pageable);

    /**
     * 获取收藏数
     *
     * @param userId 用户 ID
     * @return 收藏数
     */
    long getFavoriteCount(Long userId);

    // ==================== 用户评价 ====================

    /**
     * 创建评价
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @param orderId 订单 ID
     * @param rating 评分
     * @param content 评价内容
     * @param images 评价图片（JSON 数组）
     * @return 评价对象
     */
    ProductReview createReview(Long userId, Long productId, Long orderId,
                               Integer rating, String content, String images);

    /**
     * 更新评价
     *
     * @param reviewId 评价 ID
     * @param rating 新评分
     * @param content 新评价内容
     * @param images 新评价图片
     * @return 评价对象
     */
    ProductReview updateReview(Long reviewId, Integer rating, String content, String images);

    /**
     * 删除评价
     *
     * @param reviewId 评价 ID
     * @return 是否成功
     */
    boolean deleteReview(Long reviewId);

    /**
     * 获取商品评价列表
     *
     * @param productId 商品 ID
     * @param pageable 分页参数
     * @return 评价列表分页
     */
    Page<ProductReview> getProductReviews(Long productId, Pageable pageable);

    /**
     * 获取用户评价列表
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 评价列表分页
     */
    Page<ProductReview> getUserReviews(Long userId, Pageable pageable);

    /**
     * 获取商品平均评分
     *
     * @param productId 商品 ID
     * @return 平均评分
     */
    Double getProductAverageRating(Long productId);

    /**
     * 获取商品各评分等级的数量
     *
     * @param productId 商品 ID
     * @return 评分分布 Map{rating: count}
     */
    Map<Integer, Long> getProductRatingDistribution(Long productId);

    /**
     * 商家回复评价
     *
     * @param reviewId 评价 ID
     * @param reply 回复内容
     * @return 是否成功
     */
    boolean replyToReview(Long reviewId, String reply);

    /**
     * 获取商家所有商品评价
     *
     * @param merchant 商家用户
     * @param pageable 分页参数
     * @return 评价列表分页
     */
    Page<ProductReview> getMerchantAllReviews(User merchant, Pageable pageable);

    /**
     * 获取商家指定商品的评价
     *
     * @param merchant 商家用户
     * @param productId 商品 ID
     * @param pageable 分页参数
     * @return 评价列表分页
     */
    Page<ProductReview> getMerchantProductReviews(User merchant, Long productId, Pageable pageable);

    /**
     * 商家回复评价
     *
     * @param reviewId 评价 ID
     * @param merchant 商家用户
     * @param content 回复内容
     */
    void replyMerchantReview(Long reviewId, User merchant, String content);

    /**
     * 获取商家评价统计
     *
     * @param merchant 商家用户
     * @return 统计信息
     */
    Map<String, Object> getMerchantReviewStats(User merchant);

    /**
     * 审核评价
     *
     * @param reviewId 评价 ID
     * @param approved 是否通过
     * @return 是否成功
     */
    boolean auditReview(Long reviewId, boolean approved);
}
