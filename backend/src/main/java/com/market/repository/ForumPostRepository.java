package com.market.repository;

import com.market.entity.ForumPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 论坛帖子数据访问层
 * 对应实体：ForumPost
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    /**
     * 获取帖子列表（分页）
     */
    @Query("SELECT p FROM ForumPost p WHERE p.status = 'ACTIVE' ORDER BY p.isPinned DESC, p.createdAt DESC")
    Page<ForumPost> findActivePosts(Pageable pageable);

    /**
     * 获取用户的帖子列表
     */
    Page<ForumPost> findByUserId(Long userId, Pageable pageable);

    /**
     * 获取用户的帖子数量
     */
    long countByUserId(Long userId);

    /**
     * 按分类获取帖子
     */
    Page<ForumPost> findByCategoryAndStatus(String category, String status, Pageable pageable);

    /**
     * 按标签搜索帖子
     */
    @Query("SELECT p FROM ForumPost p WHERE p.tags LIKE CONCAT('%', :tag, '%') AND p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    Page<ForumPost> findByTag(@Param("tag") String tag, Pageable pageable);

    /**
     * 搜索帖子
     */
    @Query("SELECT p FROM ForumPost p WHERE p.status = 'ACTIVE' AND " +
           "(p.title LIKE CONCAT('%', :keyword, '%') OR p.content LIKE CONCAT('%', :keyword, '%') OR p.userName LIKE CONCAT('%', :keyword, '%')) " +
           "ORDER BY p.isPinned DESC, p.createdAt DESC")
    Page<ForumPost> searchPosts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 获取热门帖子
     */
    @Query("SELECT p FROM ForumPost p WHERE p.status = 'ACTIVE' AND p.createdAt > :sinceTime " +
           "ORDER BY p.likeCount DESC, p.viewCount DESC")
    Page<ForumPost> findHotPosts(@Param("sinceTime") LocalDateTime sinceTime, Pageable pageable);

    /**
     * 获取精华帖子
     */
    Page<ForumPost> findByIsFeaturedTrueAndStatus(String status, Pageable pageable);

    /**
     * 获取置顶帖子
     */
    List<ForumPost> findByIsPinnedTrueAndStatusOrderByCreatedAtDesc(String status);

    /**
     * 统计帖子数量
     */
    long countByStatus(String status);

    /**
     * 统计精华帖子数量
     */
    long countByIsFeaturedTrueAndStatus(String status);
}
