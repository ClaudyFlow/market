package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户关注关系实体类
 * 对应数据库表：user_follow
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "user_follow",
       uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
public class UserFollow {

    /**
     * 关注关系唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关注者 ID（谁关注了别人）
     */
    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    /**
     * 被关注者 ID（被谁关注了）
     */
    @Column(name = "following_id", nullable = false)
    private Long followingId;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UserFollow() {
    }

    public UserFollow(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
