package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 论坛评论实体类
 * 对应数据库表：forum_comment
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "forum_comment")
public class ForumComment {

    /**
     * 评论唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 帖子 ID
     */
    @Column(name = "post_id", nullable = false)
    private Long postId;

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
     * 评论内容
     */
    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    /**
     * 父评论 ID（用于回复）
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 点赞数
     */
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    /**
     * 状态
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "ACTIVE";

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public ForumComment() {
    }

    public ForumComment(Long postId, Long userId, String userName, String content) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
