package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户通知消息实体类
 * 对应数据库表：user_notification
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "user_notification")
public class UserNotification {

    /**
     * 通知消息唯一标识
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
     * 消息标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 消息内容
     */
    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    /**
     * 消息类型
     * SYSTEM - 系统通知
     * ORDER - 订单通知
     * PROMOTION - 促销通知
     * REMINDER - 提醒通知
     */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    /**
     * 消息级别
     * INFO - 普通
     * WARNING - 重要
     * URGENT - 紧急
     */
    @Column(name = "level", nullable = false, length = 20)
    private String level = "INFO";

    /**
     * 是否已读
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    /**
     * 业务类型（关联的业务类型，如 order_id, product_id 等）
     */
    @Column(name = "biz_type", length = 50)
    private String bizType;

    /**
     * 业务 ID（关联的业务 ID）
     */
    @Column(name = "biz_id")
    private Long bizId;

    /**
     * 跳转链接
     */
    @Column(name = "jump_url", length = 500)
    private String jumpUrl;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 阅读时间
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UserNotification() {
    }

    public UserNotification(Long userId, String title, String content, String type) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
