package com.market.repository;

import com.market.entity.UserBrowseHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户浏览历史数据访问接口
 *
 * @author Market Team
 * @since 1.0.0
 */
@Repository
public interface UserBrowseHistoryRepository extends JpaRepository<UserBrowseHistory, Long> {

    /**
     * 获取用户的浏览历史（分页）
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 浏览历史分页
     */
    Page<UserBrowseHistory> findByUserIdOrderByBrowseTimeDesc(Long userId, Pageable pageable);

    /**
     * 获取用户的浏览历史
     *
     * @param userId 用户 ID
     * @return 浏览历史列表
     */
    List<UserBrowseHistory> findByUserIdOrderByBrowseTimeDesc(Long userId);

    /**
     * 获取用户最近的浏览记录
     *
     * @param userId 用户 ID
     * @param limit 数量限制
     * @return 浏览历史列表
     */
    @Query(value = "SELECT * FROM user_browse_history WHERE user_id = :userId " +
           "ORDER BY browse_time DESC LIMIT :limit", nativeQuery = true)
    List<UserBrowseHistory> findRecentHistory(@Param("userId") Long userId,
                                               @Param("limit") int limit);

    /**
     * 检查用户是否浏览过某商品
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 如果浏览过返回 true
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    /**
     * 根据用户 ID 和商品 ID 查找浏览历史
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 包含浏览历史的 Optional 对象
     */
    java.util.Optional<UserBrowseHistory> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 获取用户的浏览历史数量
     *
     * @param userId 用户 ID
     * @return 浏览历史数量
     */
    long countByUserId(Long userId);

    /**
     * 删除用户的浏览历史
     *
     * @param userId 用户 ID
     */
    void deleteByUserId(Long userId);

    /**
     * 删除单条浏览历史
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);

    /**
     * 删除指定时间之前的浏览历史
     *
     * @param browseTime 浏览时间
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM UserBrowseHistory h WHERE h.browseTime < :browseTime")
    int deleteOldHistory(@Param("browseTime") LocalDateTime browseTime);

    /**
     * 搜索用户的浏览历史
     *
     * @param userId 用户 ID
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 浏览历史分页
     */
    @Query("SELECT h FROM UserBrowseHistory h WHERE h.userId = :userId " +
           "AND (h.productName LIKE %:keyword% OR h.shopName LIKE %:keyword%) " +
           "ORDER BY h.browseTime DESC")
    Page<UserBrowseHistory> searchByUserId(@Param("userId") Long userId,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);
}
