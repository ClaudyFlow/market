package com.market.service;

import com.market.entity.Product;
import com.market.entity.Review;
import com.market.entity.User;
import com.market.repository.ProductRepository;
import com.market.repository.ReviewRepository;
import com.market.repository.UserRepository;
import com.market.service.SensitiveWordFilterService.DetectionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 评价服务类
 */
@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SensitiveWordFilterService sensitiveWordFilterService;

    @Value("${market.review.auto-audit-enabled:true}")
    private boolean autoAuditEnabled;

    @Value("${market.review.auto-approve-low-risk:true}")
    private boolean autoApproveLowRisk;

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

        // 自动审核
        if (autoAuditEnabled) {
            autoAuditReview(review);
        } else {
            review.setAuditStatus("APPROVED"); // 默认通过
        }

        return reviewRepository.save(review);
    }

    /**
     * 自动审核评价
     */
    private void autoAuditReview(Review review) {
        DetectionResult detection = sensitiveWordFilterService.detectSensitiveWords(review.getContent());

        if (detection.hasSensitive()) {
            // 包含敏感词
            boolean hasHighRisk = detection.getFoundWords().stream()
                    .anyMatch(w -> "HIGH".equals(w.getLevel()));

            if (hasHighRisk) {
                // 高危敏感词，直接拒绝
                review.setAuditStatus("REJECTED");
                review.setAuditReason("包含违规内容，审核不通过");
                log.warn("评价审核拒绝 (高危敏感词): userId={}, productId={}", 
                        review.getUserId(), review.getProductId());
            } else {
                // 低/中危敏感词，替换后通过
                review.setFilteredContent(detection.getFilteredText());
                review.setAuditStatus("FILTERED");
                review.setAuditReason("已自动过滤敏感词");
                log.info("评价审核过滤 (敏感词): userId={}, productId={}", 
                        review.getUserId(), review.getProductId());
            }
        } else {
            // 无敏感词，自动通过
            review.setAuditStatus("APPROVED");
            log.info("评价审核自动通过: userId={}, productId={}", 
                    review.getUserId(), review.getProductId());
        }
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

        // 自动审核
        if (autoAuditEnabled) {
            autoAuditReview(review);
        } else {
            review.setAuditStatus("APPROVED");
        }

        return reviewRepository.save(review);
    }

    /**
     * 审核评价 (管理员手动审核)
     */
    public void auditReview(Long reviewId, String status, String reason) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("评价不存在"));

        review.setAuditStatus(status);
        review.setAuditReason(reason);

        reviewRepository.save(review);
        log.info("评价手动审核: reviewId={}, status={}, reason={}", reviewId, status, reason);
    }

    /**
     * 获取待审核的评价列表
     */
    public List<Review> getPendingReviews(int limit) {
        return reviewRepository.findAll().stream()
                .filter(r -> "PENDING".equals(r.getAuditStatus()))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取审核统计
     */
    public Map<String, Object> getAuditStats() {
        List<Review> allReviews = reviewRepository.findAll();
        
        long approved = allReviews.stream().filter(r -> "APPROVED".equals(r.getAuditStatus())).count();
        long rejected = allReviews.stream().filter(r -> "REJECTED".equals(r.getAuditStatus())).count();
        long filtered = allReviews.stream().filter(r -> "FILTERED".equals(r.getAuditStatus())).count();
        long pending = allReviews.stream().filter(r -> "PENDING".equals(r.getAuditStatus())).count();

        Map<String, Object> stats = new java.util.LinkedHashMap<>();
        stats.put("totalReviews", allReviews.size());
        stats.put("approved", approved);
        stats.put("rejected", rejected);
        stats.put("filtered", filtered);
        stats.put("pending", pending);
        stats.put("approvalRate", approved * 100.0 / (allReviews.size() > 0 ? allReviews.size() : 1));

        return stats;
    }
}
