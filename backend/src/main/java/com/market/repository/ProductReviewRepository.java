package com.market.repository;

import com.market.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品评价数据访问接口
 *
 * @author Market Team
 * @since 1.0.0
 */
@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    /**
     * 获取商品的评价列表（分页）
     *
     * @param productId 商品 ID
     * @param pageable 分页参数
     * @return 评价列表分页
     */
    Page<ProductReview> findByProductId(Long productId, Pageable pageable);

    /**
     * 获取商品的评价列表
     *
     * @param productId 商品 ID
     * @return 评价列表
     */
    List<ProductReview> findByProductId(Long productId);

    /**
     * 获取商品的评价数量
     *
     * @param productId 商品 ID
     * @return 评价数量
     */
    long countByProductId(Long productId);

    /**
     * 获取商品的平均评分
     *
     * @param productId 商品 ID
     * @return 平均评分
     */
    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.productId = :productId AND pr.status = 'APPROVED'")
    Double getAverageRating(@Param("productId") Long productId);

    /**
     * 获取用户的评价列表
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 评价列表分页
     */
    Page<ProductReview> findByUserId(Long userId, Pageable pageable);

    /**
     * 获取用户的评价列表
     *
     * @param userId 用户 ID
     * @return 评价列表
     */
    List<ProductReview> findByUserId(Long userId);

    /**
     * 获取用户的评价数量
     *
     * @param userId 用户 ID
     * @return 评价数量
     */
    long countByUserId(Long userId);

    /**
     * 检查用户是否已对某个商品评价过
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @param orderId 订单 ID
     * @return 如果已评价返回 true，否则返回 false
     */
    boolean existsByUserIdAndProductIdAndOrderId(Long userId, Long productId, Long orderId);

    /**
     * 根据订单 ID 查找评价
     *
     * @param orderId 订单 ID
     * @return 评价对象
     */
    ProductReview findByOrderId(Long orderId);

    /**
     * 获取商品各评分等级的数量
     *
     * @param productId 商品 ID
     * @param rating 评分
     * @return 该评分的数量
     */
    long countByProductIdAndRating(Long productId, Integer rating);

    /**
     * 获取商品的审核通过评价列表
     *
     * @param productId 商品 ID
     * @param pageable 分页参数
     * @return 评价列表分页
     */
    Page<ProductReview> findByProductIdAndStatus(Long productId, String status, Pageable pageable);

    /**
     * 搜索商品评价
     *
     * @param productId 商品 ID
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 评价列表分页
     */
    @Query("SELECT pr FROM ProductReview pr WHERE pr.productId = :productId " +
           "AND pr.status = 'APPROVED' " +
           "AND (pr.content LIKE %:keyword% OR pr.userName LIKE %:keyword%) " +
           "ORDER BY pr.createdAt DESC")
    Page<ProductReview> searchByProductId(@Param("productId") Long productId,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);

    /**
     * 获取商家所有商品的评价
     *
     * @param merchantId 商家 ID
     * @param pageable 分页参数
     * @return 评价列表分页
     */
    @Query("SELECT pr FROM ProductReview pr WHERE pr.merchantId = :merchantId " +
           "ORDER BY pr.createdAt DESC")
    Page<ProductReview> findByMerchantId(@Param("merchantId") Long merchantId, Pageable pageable);

    /**
     * 获取商家指定商品的评价
     *
     * @param merchantId 商家 ID
     * @param productId 商品 ID
     * @param pageable 分页参数
     * @return 评价列表分页
     */
    Page<ProductReview> findByMerchantIdAndProductId(Long merchantId, Long productId, Pageable pageable);

    /**
     * 获取商家所有商品的评价列表
     *
     * @param merchantId 商家 ID
     * @return 评价列表
     */
    List<ProductReview> findByMerchantId(@Param("merchantId") Long merchantId);
}
