package com.market.service;

import com.market.entity.Product;
import com.market.entity.Review;
import com.market.entity.User;
import com.market.repository.ProductRepository;
import com.market.repository.ReviewRepository;
import com.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评价服务类
 */
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 添加评价
     */
    public Review addReview(Long userId, Long productId, Integer rating, String content) {
        // 检查是否已评价
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("您已对该商品进行过评价");
        }

        // 检查商品是否存在
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 获取用户信息
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        Review review = new Review(
            userId,
            productId,
            rating,
            content,
            user.getUsername(),
            user.getAvatarUrl(),
            product.getName(),
            product.getImageUrl(),
            product.getPrice()
        );

        return reviewRepository.save(review);
    }

    /**
     * 更新评价
     */
    public Review updateReview(Long userId, Long productId, Integer rating, String content) {
        Review review = reviewRepository.findByUserIdAndProductId(userId, productId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }

        review.setRating(rating);
        review.setContent(content);
        return reviewRepository.save(review);
    }

    /**
     * 删除评价
     */
    public void deleteReview(Long userId, Long productId) {
        reviewRepository.deleteByUserIdAndProductId(userId, productId);
    }

    /**
     * 获取商品的所有评价
     */
    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdWithUser(productId);
    }

    /**
     * 获取用户的所有评价
     */
    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    /**
     * 获取商品平均评分
     */
    public Double getProductAverageRating(Long productId) {
        Double avg = reviewRepository.avgRatingByProductId(productId);
        return avg != null ? avg : 0.0;
    }

    /**
     * 获取商品评价数量
     */
    public int getProductReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    /**
     * 检查用户是否已评价
     */
    public boolean hasReviewed(Long userId, Long productId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }

    /**
     * 获取用户的评价
     */
    public Review getUserReview(Long userId, Long productId) {
        return reviewRepository.findByUserIdAndProductId(userId, productId);
    }

    /**
     * 获取各评分等级的评价数量
     */
    public int getRatingCount(Long productId, Integer rating) {
        return reviewRepository.countByProductIdAndRating(productId, rating);
    }

    /**
     * 基于订单提交评价
     */
    public Review addReviewFromOrder(Long userId, Long productId, Integer rating, String content, Long orderId) {
        // 检查是否已评价
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("您已对该商品进行过评价");
        }

        // 检查商品是否存在
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 获取用户信息
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        Review review = new Review(
            userId,
            productId,
            rating,
            content,
            user.getUsername(),
            user.getAvatarUrl(),
            product.getName(),
            product.getImageUrl(),
            product.getPrice()
        );
        
        // 设置订单 ID
        review.setOrderId(orderId);

        return reviewRepository.save(review);
    }
}
