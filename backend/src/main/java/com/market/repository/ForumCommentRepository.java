package com.market.repository;

import com.market.entity.ForumComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 论坛评论数据访问接口
 */
@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {

    /**
     * 获取帖子的评论列表（分页）
     */
    @Query("SELECT c FROM ForumComment c WHERE c.postId = :postId AND c.status = 'ACTIVE' ORDER BY c.createdAt ASC")
    Page<ForumComment> findByPostIdAndStatusOrderByCreatedAtAsc(@Param("postId") Long postId, String status, Pageable pageable);

    /**
     * 获取帖子的评论数量
     */
    long countByPostId(Long postId);

    /**
     * 获取用户的评论列表
     */
    Page<ForumComment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 获取帖子的评论列表（不分页）
     */
    List<ForumComment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, String status);

    /**
     * 删除帖子的所有评论
     */
    void deleteByPostId(Long postId);
}
