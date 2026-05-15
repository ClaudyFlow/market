package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 物流信息实体类
 * 对应数据库表：logistics_info
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "logistics_info")
public class LogisticsInfo {

    /**
     * 物流信息唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的订单ID
     */
    @Column(nullable = false)
    private Long orderId;

    /**
     * 物流运单号
     */
    @Column(nullable = false, length = 64)
    private String trackingNo;

    /**
     * 快递公司编码
     */
    @Column(nullable = false, length = 20)
    private String companyCode;

    /**
     * 快递公司名称
     */
    @Column(length = 100)
    private String companyName;

    /**
     * 物流状态（PENDING待发货、IN_TRANSIT运输中、DELIVERED已签收、EXCEPTION异常）
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * 预计送达时间
     */
    @Column
    private LocalDateTime estimatedDelivery;

    /**
     * 物流轨迹列表
     */
    @OneToMany(mappedBy = "logisticsInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogisticsTrack> tracks = new ArrayList<>();

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

    public LogisticsInfo() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addTrack(LogisticsTrack track) {
        tracks.add(track);
        track.setLogisticsInfo(this);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }

    public String getCompanyCode() { return companyCode; }
    public void setCompanyCode(String companyCode) { this.companyCode = companyCode; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }

    public List<LogisticsTrack> getTracks() { return tracks; }
    public void setTracks(List<LogisticsTrack> tracks) { this.tracks = tracks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
