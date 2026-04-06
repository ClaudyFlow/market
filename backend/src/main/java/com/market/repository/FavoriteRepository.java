package com.market.repository;

import com.market.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 收藏数据访问层
 * 对应实体：Favorite
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * 根据用户 ID 和商品 ID 查找收藏
     */
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 根据用户 ID 查找所有收藏
     */
    List<Favorite> findByUserId(Long userId);
    
    /**
     * 根据用户 ID 查找所有收藏（带商品信息）
     */
    @Query("SELECT f FROM Favorite f JOIN FETCH f.product WHERE f.userId = :userId")
    List<Favorite> findByUserIdWithProduct(Long userId);
    
    /**
     * 检查用户是否收藏了某商品
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 删除用户的某个收藏
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 统计用户收藏数量
     */
    int countByUserId(Long userId);
}
