package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 物流轨迹实体类
 * 对应数据库表：logistics_track
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "logistics_track")
public class LogisticsTrack {

    /**
     * 轨迹记录唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的物流信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_id", nullable = false)
    private LogisticsInfo logisticsInfo;

    /**
     * 物流运单号
     */
    @Column(nullable = false, length = 64)
    private String trackingNo;

    /**
     * 轨迹发生时间
     */
    @Column(nullable = false)
    private LocalDateTime time;

    /**
     * 轨迹发生地点
     */
    @Column(length = 200)
    private String location;

    /**
     * 轨迹描述
     */
    @Column(nullable = false, length = 500)
    private String description;

    /**
     * 物流状态（签收、运输中、异常等）
     */
    @Column(length = 20)
    private String status;

    /**
     * 记录创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public LogisticsTrack() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LogisticsInfo getLogisticsInfo() { return logisticsInfo; }
    public void setLogisticsInfo(LogisticsInfo logisticsInfo) { this.logisticsInfo = logisticsInfo; }

    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
