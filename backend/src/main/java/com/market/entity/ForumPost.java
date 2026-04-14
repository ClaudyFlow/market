package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 论坛帖子实体类
 * 对应数据库表：forum_post
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "forum_post")
public class ForumPost {

    /**
     * 帖子唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 用户名（冗余存储）
     */
    @Column(name = "user_name", length = 50)
    private String userName;

    /**
     * 用户头像（冗余存储）
     */
    @Column(name = "user_avatar", length = 500)
    private String userAvatar;

    /**
     * 帖子标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 帖子内容
     */
    @Column(name = "content", nullable = false, length = 10000)
    private String content;

    /**
     * 标签（逗号分隔）
     */
    @Column(name = "tags", length = 500)
    private String tags;

    /**
     * 分类
     */
    @Column(name = "category", length = 50)
    private String category;

    /**
     * 点赞数
     */
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    /**
     * 评论数
     */
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    /**
     * 浏览数
     */
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    /**
     * 是否置顶
     */
    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    /**
     * 是否精华
     */
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    /**
     * 状态（ACTIVE, DELETED, HIDDEN）
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "ACTIVE";

    /**
     * 审核状态 (PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, FILTERED-已过滤)
     */
    @Column(name = "audit_status", length = 20)
    private String auditStatus = "APPROVED"; // 默认通过

    /**
     * 审核原因
     */
    @Column(name = "audit_reason", length = 500)
    private String auditReason;

    /**
     * 过滤后的内容
     */
    @Column(name = "filtered_content", columnDefinition = "TEXT")
    private String filteredContent;

    /**
     * 过滤后的标题
     */
    @Column(name = "filtered_title", length = 200)
    private String filteredTitle;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public ForumPost() {
    }

    public ForumPost(Long userId, String userName, String title, String content) {
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Boolean isPinned) {
        this.isPinned = isPinned;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }

    public String getAuditReason() { return auditReason; }
    public void setAuditReason(String auditReason) { this.auditReason = auditReason; }

    public String getFilteredContent() { return filteredContent; }
    public void setFilteredContent(String filteredContent) { this.filteredContent = filteredContent; }

    public String getFilteredTitle() { return filteredTitle; }
    public void setFilteredTitle(String filteredTitle) { this.filteredTitle = filteredTitle; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
