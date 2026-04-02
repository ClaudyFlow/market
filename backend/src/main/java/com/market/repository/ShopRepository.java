package com.market.repository;

import com.market.entity.Shop;
import com.market.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 店铺数据访问层
 */
@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    /**
     * 根据状态查询店铺
     */
    Page<Shop> findByStatus(String status, Pageable pageable);

    /**
     * 根据认证状态查询店铺
     */
    Page<Shop> findByCertified(Boolean certified, Pageable pageable);

    /**
     * 根据店主查询店铺
     */
    Optional<Shop> findByOwnerId(Long ownerId);

    /**
     * 根据店主查询店铺
     */
    Optional<Shop> findByOwner(User owner);

    /**
     * 搜索店铺
     */
    @Query("SELECT s FROM Shop s WHERE s.status = 'active' AND " +
           "(s.name LIKE %:keyword% OR s.description LIKE %:keyword%) " +
           "ORDER BY s.rating DESC, s.followers DESC")
    Page<Shop> searchShops(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 获取认证店铺
     */
    List<Shop> findByCertifiedAndStatus(Boolean certified, String status);

    /**
     * 获取高评分店铺
     */
    @Query("SELECT s FROM Shop s WHERE s.status = 'active' AND s.rating >= :minRating ORDER BY s.rating DESC")
    Page<Shop> findByRatingGreaterThanEqual(@Param("minRating") Double minRating, Pageable pageable);

    /**
     * 统计店铺数量
     */
    long countByStatus(String status);

    /**
     * 统计认证店铺数量
     */
    long countByCertifiedAndStatus(Boolean certified, String status);
}
