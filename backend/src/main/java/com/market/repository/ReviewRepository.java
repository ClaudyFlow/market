package com.market.repository;

import com.market.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评价数据访问层
 * 对应实体：Review
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 根据商品 ID 查找所有评价
     */
    List<Review> findByProductId(Long productId);

    /**
     * 根据商品 ID 查找所有评价（带用户信息）
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.productId = :productId")
    List<Review> findByProductIdWithUser(Long productId);

    /**
     * 根据用户 ID 查找所有评价
     */
    List<Review> findByUserId(Long userId);

    /**
     * 根据用户 ID 和商品 ID 查找评价
     */
    Review findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 检查用户是否已评价某商品
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    /**
     * 统计商品评价数量
     */
    int countByProductId(Long productId);

    /**
     * 计算商品平均评分
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId")
    Double avgRatingByProductId(Long productId);

    /**
     * 根据评分统计评价数量
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.productId = :productId AND r.rating = :rating")
    int countByProductIdAndRating(Long productId, Integer rating);

    /**
     * 删除用户的某个评价
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 检查订单是否已评价
     */
    boolean existsByOrderId(Long orderId);
}
