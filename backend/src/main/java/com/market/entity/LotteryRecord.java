package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 抽奖记录实体类
 * 对应数据库表：lottery_record
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "lottery_record")
public class LotteryRecord {

    /**
     * 抽奖记录唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 奖品ID
     */
    @Column(nullable = false)
    private Long prizeId;

    /**
     * 奖品名称
     */
    @Column(nullable = false, length = 100)
    private String prizeName;

    /**
     * 奖品类型（1=积分，2=实物）
     */
    @Column(nullable = false)
    private Integer prizeType;

    /**
     * 消耗积分
     */
    @Column(nullable = false)
    private Integer cost;

    /**
     * 抽奖时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public LotteryRecord() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPrizeId() { return prizeId; }
    public void setPrizeId(Long prizeId) { this.prizeId = prizeId; }

    public String getPrizeName() { return prizeName; }
    public void setPrizeName(String prizeName) { this.prizeName = prizeName; }

    public Integer getPrizeType() { return prizeType; }
    public void setPrizeType(Integer prizeType) { this.prizeType = prizeType; }

    public Integer getCost() { return cost; }
    public void setCost(Integer cost) { this.cost = cost; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
