package com.market.service;

import com.market.entity.ForumComment;
import com.market.entity.ForumPost;
import com.market.entity.User;
import com.market.repository.ForumCommentRepository;
import com.market.repository.ForumPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 论坛服务类
 */
@Service
public class ForumService {

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private ForumCommentRepository forumCommentRepository;

    // ==================== 帖子管理 ====================

    /**
     * 创建帖子
     */
    @Transactional
    public ForumPost createPost(User user, String title, String content, String tags, String category) {
        ForumPost post = new ForumPost();
        post.setUserId(user.getId());
        post.setUserName(user.getName());
        post.setUserAvatar(user.getAvatarUrl());
        post.setTitle(title);
        post.setContent(content);
        post.setTags(tags);
        post.setCategory(category);

        return forumPostRepository.save(post);
    }

    /**
     * 更新帖子
     */
    @Transactional
    public ForumPost updatePost(Long postId, User user, String title, String content, String tags) {
        ForumPost post = forumPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("帖子不存在"));

        if (!post.getUserId().equals(user.getId())) {
            throw new RuntimeException("无权修改该帖子");
        }

        if (title != null) {
            post.setTitle(title);
        }
        if (content != null) {
            post.setContent(content);
        }
        if (tags != null) {
            post.setTags(tags);
        }

        return forumPostRepository.save(post);
    }

    /**
     * 删除帖子
     */
    @Transactional
    public void deletePost(Long postId, User user) {
        ForumPost post = forumPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("帖子不存在"));

        if (!post.getUserId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权删除该帖子");
        }

        post.setStatus("DELETED");
        forumPostRepository.save(post);
    }

    /**
     * 获取帖子详情
     */
    public ForumPost getPostDetail(Long postId) {
        ForumPost post = forumPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("帖子不存在"));

        // 增加浏览数
        post.setViewCount(post.getViewCount() + 1);
        forumPostRepository.save(post);

        return post;
    }

    /**
     * 获取帖子列表（分页）
     */
    public Page<ForumPost> getPostList(Pageable pageable) {
        return forumPostRepository.findActivePosts(pageable);
    }

    /**
     * 获取热门帖子
     */
    public Page<ForumPost> getHotPosts(LocalDateTime sinceTime, Pageable pageable) {
        return forumPostRepository.findHotPosts(sinceTime, pageable);
    }

    /**
     * 获取精华帖子
     */
    public Page<ForumPost> getFeaturedPosts(Pageable pageable) {
        return forumPostRepository.findByIsFeaturedTrueAndStatus("ACTIVE", pageable);
    }

    /**
     * 获取用户的帖子
     */
    public Page<ForumPost> getUserPosts(User user, Pageable pageable) {
        return forumPostRepository.findByUserId(user.getId(), pageable);
    }

    /**
     * 搜索帖子
     */
    public Page<ForumPost> searchPosts(String keyword, Pageable pageable) {
        return forumPostRepository.searchPosts(keyword, pageable);
    }

    /**
     * 按标签获取帖子
     */
    public Page<ForumPost> getPostsByTag(String tag, Pageable pageable) {
        return forumPostRepository.findByTag(tag, pageable);
    }

    /**
     * 点赞帖子
     */
    @Transactional
    public void likePost(Long postId) {
        ForumPost post = forumPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("帖子不存在"));

        post.setLikeCount(post.getLikeCount() + 1);
        forumPostRepository.save(post);
    }

    /**
     * 获取帖子统计
     */
    public Map<String, Object> getPostStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", forumPostRepository.countByStatus("ACTIVE"));
        stats.put("featured", forumPostRepository.countByIsFeaturedTrueAndStatus("ACTIVE"));
        return stats;
    }

    // ==================== 评论管理 ====================

    /**
     * 创建评论
     */
    @Transactional
    public ForumComment createComment(User user, Long postId, String content, Long parentId) {
        ForumPost post = forumPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("帖子不存在"));

        ForumComment comment = new ForumComment();
        comment.setPostId(postId);
        comment.setUserId(user.getId());
        comment.setUserName(user.getName());
        comment.setUserAvatar(user.getAvatarUrl());
        comment.setContent(content);
        comment.setParentId(parentId);

        forumCommentRepository.save(comment);

        // 更新帖子的评论数
        post.setCommentCount(post.getCommentCount() + 1);
        forumPostRepository.save(post);

        return comment;
    }

    /**
     * 删除评论
     */
    @Transactional
    public void deleteComment(Long commentId, User user) {
        ForumComment comment = forumCommentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("评论不存在"));

        if (!comment.getUserId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权删除该评论");
        }

        comment.setStatus("DELETED");
        forumCommentRepository.save(comment);

        // 更新帖子的评论数
        ForumPost post = forumPostRepository.findById(comment.getPostId()).orElse(null);
        if (post != null) {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            forumPostRepository.save(post);
        }
    }

    /**
     * 获取帖子的评论列表
     */
    public Page<ForumComment> getPostComments(Long postId, Pageable pageable) {
        return forumCommentRepository.findByPostIdAndStatusOrderByCreatedAtAsc(postId, "ACTIVE", pageable);
    }

    /**
     * 获取用户的评论列表
     */
    public Page<ForumComment> getUserComments(User user, Pageable pageable) {
        return forumCommentRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
    }

    /**
     * 点赞评论
     */
    @Transactional
    public void likeComment(Long commentId) {
        ForumComment comment = forumCommentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("评论不存在"));

        comment.setLikeCount(comment.getLikeCount() + 1);
        forumCommentRepository.save(comment);
    }
}
