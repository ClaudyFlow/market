package com.market.service;

import com.market.entity.ShopReview;
import com.market.entity.Shop;
import com.market.entity.User;
import com.market.repository.ShopReviewRepository;
import com.market.repository.ShopRepository;
import com.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ShopReviewService {

    @Autowired
    private ShopReviewRepository shopReviewRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ShopReview createReview(Long userId, Long shopId, Long orderId, Integer rating,
                                    BigDecimal descriptionScore, BigDecimal serviceScore,
                                    BigDecimal logisticsScore, String content, String images) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("评分必须在1-5之间");
        }
        if (shopReviewRepository.existsByUserIdAndShopIdAndOrderId(userId, shopId, orderId)) {
            throw new RuntimeException("该订单已评价");
        }

        Shop shop = shopRepository.findById(shopId)
            .orElseThrow(() -> new RuntimeException("店铺不存在"));

        ShopReview review = new ShopReview(userId, shopId, rating);
        review.setOrderId(orderId);
        review.setDescriptionScore(descriptionScore);
        review.setServiceScore(serviceScore);
        review.setLogisticsScore(logisticsScore);
        review.setContent(content);
        review.setImages(images);
        review.setStatus("APPROVED");

        ShopReview saved = shopReviewRepository.save(review);
        updateShopRating(shopId);
        return saved;
    }

    @Transactional
    public ShopReview updateReview(Long reviewId, Long userId, Integer rating,
                                    BigDecimal descriptionScore, BigDecimal serviceScore,
                                    BigDecimal logisticsScore, String content, String images) {
        ShopReview review = shopReviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("评价不存在"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此评价");
        }

        if (rating != null) {
            if (rating < 1 || rating > 5) {
                throw new RuntimeException("评分必须在1-5之间");
            }
            review.setRating(rating);
        }
        if (descriptionScore != null) review.setDescriptionScore(descriptionScore);
        if (serviceScore != null) review.setServiceScore(serviceScore);
        if (logisticsScore != null) review.setLogisticsScore(logisticsScore);
        if (content != null) review.setContent(content);
        if (images != null) review.setImages(images);

        ShopReview saved = shopReviewRepository.save(review);
        updateShopRating(review.getShopId());
        return saved;
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        ShopReview review = shopReviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("评价不存在"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评价");
        }

        Long shopId = review.getShopId();
        shopReviewRepository.delete(review);
        updateShopRating(shopId);
    }

    public Page<ShopReview> getShopReviews(Long shopId, Pageable pageable) {
        return shopReviewRepository.findByShopId(shopId, pageable);
    }

    public Page<ShopReview> getUserReviews(Long userId, Pageable pageable) {
        return shopReviewRepository.findByUserId(userId, pageable);
    }

    public ShopReview getReviewById(Long reviewId) {
        return shopReviewRepository.findById(reviewId).orElse(null);
    }

    @Transactional
    public boolean replyToReview(Long reviewId, String reply, Long merchantUserId) {
        ShopReview review = shopReviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("评价不存在"));

        Shop shop = shopRepository.findById(review.getShopId())
            .orElseThrow(() -> new RuntimeException("店铺不存在"));
        
        if (shop.getOwner() == null || !shop.getOwner().getId().equals(merchantUserId)) {
            throw new RuntimeException("无权回复此评价");
        }

        review.setMerchantReply(reply);
        review.setMerchantReplyAt(LocalDateTime.now());
        shopReviewRepository.save(review);
        return true;
    }

    public Map<String, Object> getShopRatingStats(Long shopId) {
        List<ShopReview> reviews = shopReviewRepository.findByShopId(shopId);
        long total = reviews.size();
        if (total == 0) {
            return Map.of(
                "totalReviews", 0,
                "averageRating", BigDecimal.ZERO,
                "descriptionScore", BigDecimal.ZERO,
                "serviceScore", BigDecimal.ZERO,
                "logisticsScore", BigDecimal.ZERO
            );
        }

        BigDecimal totalRating = BigDecimal.ZERO;
        BigDecimal totalDesc = BigDecimal.ZERO;
        BigDecimal totalService = BigDecimal.ZERO;
        BigDecimal totalLogistics = BigDecimal.ZERO;

        for (ShopReview r : reviews) {
            totalRating = totalRating.add(BigDecimal.valueOf(r.getRating()));
            if (r.getDescriptionScore() != null) totalDesc = totalDesc.add(r.getDescriptionScore());
            if (r.getServiceScore() != null) totalService = totalService.add(r.getServiceScore());
            if (r.getLogisticsScore() != null) totalLogistics = totalLogistics.add(r.getLogisticsScore());
        }

        return Map.of(
            "totalReviews", total,
            "averageRating", totalRating.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP),
            "descriptionScore", totalDesc.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP),
            "serviceScore", totalService.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP),
            "logisticsScore", totalLogistics.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
        );
    }

    private void updateShopRating(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElse(null);
        if (shop == null) return;

        List<ShopReview> reviews = shopReviewRepository.findByShopId(shopId);
        if (reviews.isEmpty()) {
            shop.setRating(BigDecimal.ZERO);
            shop.setDescriptionScore(BigDecimal.ZERO);
            shop.setServiceScore(BigDecimal.ZERO);
            shop.setLogisticsScore(BigDecimal.ZERO);
        } else {
            BigDecimal totalRating = BigDecimal.ZERO;
            BigDecimal totalDesc = BigDecimal.ZERO;
            BigDecimal totalService = BigDecimal.ZERO;
            BigDecimal totalLogistics = BigDecimal.ZERO;

            for (ShopReview r : reviews) {
                totalRating = totalRating.add(BigDecimal.valueOf(r.getRating()));
                if (r.getDescriptionScore() != null) totalDesc = totalDesc.add(r.getDescriptionScore());
                if (r.getServiceScore() != null) totalService = totalService.add(r.getServiceScore());
                if (r.getLogisticsScore() != null) totalLogistics = totalLogistics.add(r.getLogisticsScore());
            }

            int count = reviews.size();
            shop.setRating(totalRating.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
            shop.setDescriptionScore(totalDesc.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
            shop.setServiceScore(totalService.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
            shop.setLogisticsScore(totalLogistics.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
        }

        shopRepository.save(shop);
    }
}