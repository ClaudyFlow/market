package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 系统消息实体
 */
@Entity
@Table(name = "system_message")
public class SystemMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 消息标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 消息内容
     */
    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    /**
     * 消息类型
     * SYSTEM - 系统通知
     * ACTIVITY - 活动通知
     * ORDER - 订单通知
     * PROMOTION - 促销通知
     */
    @Column(name = "type", length = 20, nullable = false)
    private String type;

    /**
     * 优先级（1-5，5 最高）
     */
    @Column(name = "priority", nullable = false)
    private Integer priority = 3;

    /**
     * 是否全员发送
     */
    @Column(name = "is_broadcast", nullable = false)
    private Boolean isBroadcast = false;

    /**
     * 目标用户 ID（逗号分隔）
     */
    @Column(name = "target_user_ids", length = 2000)
    private String targetUserIds;

    /**
     * 跳转链接
     */
    @Column(name = "jump_url", length = 500)
    private String jumpUrl;

    /**
     * 图片 URL
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * 发送时间
     */
    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (sendTime == null) {
            sendTime = LocalDateTime.now();
        }
    }

    public SystemMessage() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getIsBroadcast() {
        return isBroadcast;
    }

    public void setIsBroadcast(Boolean isBroadcast) {
        this.isBroadcast = isBroadcast;
    }

    public String getTargetUserIds() {
        return targetUserIds;
    }

    public void setTargetUserIds(String targetUserIds) {
        this.targetUserIds = targetUserIds;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
