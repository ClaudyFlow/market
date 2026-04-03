package com.market.repository;

import com.market.entity.UserBrowseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 浏览历史数据访问层
 */
@Repository
public interface UserBrowseHistoryRepository extends JpaRepository<UserBrowseHistory, Long> {

    /**
     * 检查用户是否浏览过某商品
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    /**
     * 根据用户 ID 和商品 ID 查询浏览历史
     */
    Optional<UserBrowseHistory> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 根据用户 ID 查询浏览历史（按时间倒序）
     */
    List<UserBrowseHistory> findByUserIdOrderByBrowseTimeDesc(Long userId);

    /**
     * 根据用户 ID 和商品 ID 删除浏览历史
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);

    /**
     * 根据用户 ID 删除浏览历史
     */
    void deleteByUserId(Long userId);

    /**
     * 统计用户浏览历史数量
     */
    long countByUserId(Long userId);

    /**
     * 根据用户 ID 查询浏览历史（分页）
     */
    org.springframework.data.domain.Page<UserBrowseHistory> findByUserIdOrderByBrowseTimeDesc(
        Long userId, org.springframework.data.domain.Pageable pageable);

    /**
     * 查询用户最近的浏览历史
     */
    @Query("SELECT h FROM UserBrowseHistory h WHERE h.userId = :userId ORDER BY h.browseTime DESC")
    List<UserBrowseHistory> findRecentHistory(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 搜索用户浏览历史
     */
    @Query("SELECT h FROM UserBrowseHistory h WHERE h.userId = :userId " +
           "AND h.productName LIKE %:keyword% ORDER BY h.browseTime DESC")
    org.springframework.data.domain.Page<UserBrowseHistory> searchByUserId(
        @Param("userId") Long userId, @Param("keyword") String keyword,
        org.springframework.data.domain.Pageable pageable);

    /**
     * 删除指定时间之前的浏览历史
     */
    @Query("DELETE FROM UserBrowseHistory h WHERE h.browseTime < :beforeTime")
    int deleteOldHistory(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 获取用户浏览过的商品 ID 列表
     */
    @Query("SELECT h.productId FROM UserBrowseHistory h WHERE h.userId = :userId ORDER BY h.browseTime DESC")
    List<Long> findProductIdsByUserId(@Param("userId") Long userId);
}
