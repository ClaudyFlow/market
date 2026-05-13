package com.market.service;

import com.market.entity.ForumComment;
import com.market.entity.ForumPost;
import com.market.entity.User;
import com.market.repository.ForumCommentRepository;
import com.market.repository.ForumPostRepository;
import com.market.service.SensitiveWordFilterService.DetectionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private static final Logger log = LoggerFactory.getLogger(ForumService.class);

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private ForumCommentRepository forumCommentRepository;

    @Autowired
    private SensitiveWordFilterService sensitiveWordFilterService;

    @Value("${market.forum.auto-audit-enabled:true}")
    private boolean autoAuditEnabled;

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

        // 自动审核
        if (autoAuditEnabled) {
            autoAuditPost(post);
        }

        return forumPostRepository.save(post);
    }

    /**
     * 自动审核论坛帖子
     */
    private void autoAuditPost(ForumPost post) {
        // 检测标题
        DetectionResult titleDetection = sensitiveWordFilterService.detectSensitiveWords(post.getTitle());
        // 检测内容
        DetectionResult contentDetection = sensitiveWordFilterService.detectSensitiveWords(post.getContent());

        boolean hasSensitive = titleDetection.hasSensitive() || contentDetection.hasSensitive();

        if (hasSensitive) {
            boolean hasHighRisk = titleDetection.getFoundWords().stream()
                    .anyMatch(w -> "HIGH".equals(w.getLevel()))
                    || contentDetection.getFoundWords().stream()
                    .anyMatch(w -> "HIGH".equals(w.getLevel()));

            if (hasHighRisk) {
                // 高危敏感词，直接拒绝
                post.setAuditStatus("REJECTED");
                post.setAuditReason("包含违规内容，审核不通过");
                post.setStatus("HIDDEN"); // 隐藏帖子
                log.warn("论坛帖子审核拒绝 (高危敏感词): userId={}, title={}", post.getUserId(), post.getTitle());
            } else {
                // 低/中危敏感词，替换后通过
                post.setFilteredTitle(titleDetection.getFilteredText());
                post.setFilteredContent(contentDetection.getFilteredText());
                post.setAuditStatus("FILTERED");
                post.setAuditReason("已自动过滤敏感词");
                log.info("论坛帖子审核过滤 (敏感词): userId={}, title={}", post.getUserId(), post.getTitle());
            }
        } else {
            // 无敏感词，自动通过
            post.setAuditStatus("APPROVED");
            log.info("论坛帖子审核自动通过: userId={}, title={}", post.getUserId(), post.getTitle());
        }
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

    // ==================== 审核管理 ====================

    /**
     * 获取审核统计
     */
    public Map<String, Object> getAuditStats() {
        List<ForumPost> allPosts = forumPostRepository.findAll();
        
        long approved = allPosts.stream().filter(p -> "APPROVED".equals(p.getAuditStatus())).count();
        long rejected = allPosts.stream().filter(p -> "REJECTED".equals(p.getAuditStatus())).count();
        long filtered = allPosts.stream().filter(p -> "FILTERED".equals(p.getAuditStatus())).count();
        long pending = allPosts.stream().filter(p -> "PENDING".equals(p.getAuditStatus())).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPosts", allPosts.size());
        stats.put("approved", approved);
        stats.put("rejected", rejected);
        stats.put("filtered", filtered);
        stats.put("pending", pending);
        stats.put("approvalRate", allPosts.size() > 0 ? approved * 100.0 / allPosts.size() : 0);

        return stats;
    }

    /**
     * 获取待审核帖子列表
     */
    public Map<String, Object> getPendingPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ForumPost> allPosts = forumPostRepository.findAll(pageable);
        
        // 过滤出待审核或已过滤的帖子
        List<ForumPost> pendingPosts = allPosts.getContent().stream()
                .filter(p -> "PENDING".equals(p.getAuditStatus()) || "FILTERED".equals(p.getAuditStatus()))
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("posts", pendingPosts);
        result.put("total", pendingPosts.size());
        result.put("pages", (int) Math.ceil((double) pendingPosts.size() / size));

        return result;
    }

    /**
     * 审核帖子 (管理员手动审核)
     */
    @Transactional
    public void auditPost(Long postId, String status, String reason) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));

        post.setAuditStatus(status);
        post.setAuditReason(reason);

        if ("REJECTED".equals(status)) {
            post.setStatus("HIDDEN");
        } else if ("APPROVED".equals(status) || "FILTERED".equals(status)) {
            post.setStatus("ACTIVE");
        }

        forumPostRepository.save(post);
        log.info("论坛帖子手动审核: postId={}, status={}, reason={}", postId, status, reason);
    }
}
