package com.market.repository;

import com.market.entity.UserFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户收藏数据访问层
 * 对应实体：UserFavorite
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

    /**
     * 查找用户是否收藏了某个商品
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 包含收藏关系的 Optional 对象
     */
    Optional<UserFavorite> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 检查用户是否收藏了某个商品
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 如果已收藏返回 true，否则返回 false
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    /**
     * 获取用户的收藏列表（分页）
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 收藏列表分页
     */
    Page<UserFavorite> findByUserId(Long userId, Pageable pageable);

    /**
     * 获取用户的收藏列表
     *
     * @param userId 用户 ID
     * @return 收藏列表
     */
    List<UserFavorite> findByUserId(Long userId);

    /**
     * 统计用户的收藏数
     *
     * @param userId 用户 ID
     * @return 收藏数
     */
    long countByUserId(Long userId);

    /**
     * 根据商品 ID 删除收藏
     *
     * @param productId 商品 ID
     */
    void deleteByProductId(Long productId);

    /**
     * 根据用户 ID 删除所有收藏
     *
     * @param userId 用户 ID
     */
    void deleteByUserId(Long userId);

    /**
     * 搜索用户的收藏商品
     *
     * @param userId 用户 ID
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 收藏列表分页
     */
    @Query("SELECT uf FROM UserFavorite uf WHERE uf.userId = :userId " +
           "AND (uf.productName LIKE CONCAT('%', :keyword, '%') OR uf.shopName LIKE CONCAT('%', :keyword, '%')) " +
           "ORDER BY uf.createdAt DESC")
    Page<UserFavorite> searchByUserId(@Param("userId") Long userId,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);
}
