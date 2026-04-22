package com.market.repository;

import com.market.entity.ShopReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopReviewRepository extends JpaRepository<ShopReview, Long> {
    List<ShopReview> findByShopId(Long shopId);
    Page<ShopReview> findByShopId(Long shopId, Pageable pageable);
    List<ShopReview> findByUserId(Long userId);
    Page<ShopReview> findByUserId(Long userId, Pageable pageable);
    List<ShopReview> findByShopIdAndStatus(Long shopId, String status);
    boolean existsByUserIdAndShopIdAndOrderId(Long userId, Long shopId, Long orderId);
    long countByShopId(Long shopId);
}